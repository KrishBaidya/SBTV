import { useState, useEffect, useRef, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router';
import { motion, AnimatePresence } from 'motion/react';
import {
  Play, Pause, Volume2, VolumeX, Settings,
  ArrowLeft, Maximize, SkipForward, SkipBack, Subtitles,
} from 'lucide-react';
import { cn } from '../../lib/utils';

// Mock content metadata
const CONTENT: Record<string, { title: string; subtitle: string; bg: string }> = {
  default: {
    title: 'Interstellar Horizon',
    subtitle: 'S1 E1 · The Beginning · 4K HDR',
    bg: 'https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=2000&auto=format&fit=crop',
  },
};

export function Player() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [isPlaying, setIsPlaying] = useState(true);
  const [showControls, setShowControls] = useState(true);
  const [progress, setProgress] = useState(28);
  const [volume, setVolume] = useState(80);
  const [muted, setMuted] = useState(false);
  const [isSeeking, setIsSeeking] = useState(false);

  const hideTimer = useRef<ReturnType<typeof setTimeout> | null>(null);

  const scheduleHide = useCallback(() => {
    if (hideTimer.current) clearTimeout(hideTimer.current);
    hideTimer.current = setTimeout(() => {
      if (isPlaying) setShowControls(false);
    }, 3500);
  }, [isPlaying]);

  const revealControls = useCallback(() => {
    setShowControls(true);
    scheduleHide();
  }, [scheduleHide]);

  useEffect(() => {
    scheduleHide();
    return () => { if (hideTimer.current) clearTimeout(hideTimer.current); };
  }, [scheduleHide]);

  // Simulate progress
  useEffect(() => {
    if (!isPlaying || isSeeking) return;
    const tick = setInterval(() => {
      setProgress((p) => (p >= 100 ? 0 : p + 0.05));
    }, 100);
    return () => clearInterval(tick);
  }, [isPlaying, isSeeking]);

  const content = CONTENT[id || 'default'] ?? CONTENT.default;
  const elapsed = Math.floor((progress / 100) * 2700); // 45 min mock
  const fmt = (s: number) => `${Math.floor(s / 60).toString().padStart(2, '0')}:${(s % 60).toString().padStart(2, '0')}`;

  const handleProgressClick = (e: React.MouseEvent<HTMLDivElement>) => {
    const rect = e.currentTarget.getBoundingClientRect();
    const pct = ((e.clientX - rect.left) / rect.width) * 100;
    setProgress(Math.max(0, Math.min(100, pct)));
  };

  return (
    <div
      className={cn('fixed inset-0 bg-black z-[100] overflow-hidden select-none', showControls ? 'cursor-default' : 'cursor-none')}
      onMouseMove={revealControls}
      onClick={revealControls}
    >
      {/* Video Background (mock) */}
      <div className="absolute inset-0">
        <img
          src={content.bg}
          alt="video"
          className="w-full h-full object-cover opacity-50"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-black/30 via-transparent to-black/20" />
        {/* Watermark */}
        <div className="absolute inset-0 flex items-center justify-center pointer-events-none">
          <span className="text-white/[0.04] select-none" style={{ fontSize: 'clamp(4rem, 20vw, 18rem)', fontWeight: 900, letterSpacing: '-0.06em' }}>
            SB TV
          </span>
        </div>
      </div>

      {/* Controls Overlay */}
      <AnimatePresence>
        {showControls && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.25 }}
            className="absolute inset-0 flex flex-col justify-between"
            style={{ background: 'linear-gradient(to bottom, rgba(0,0,0,0.6) 0%, transparent 25%, transparent 65%, rgba(0,0,0,0.85) 100%)' }}
            onClick={(e) => e.stopPropagation()}
          >
            {/* ─── Top Bar ─── */}
            <div className="flex items-center justify-between px-6 md:px-10 py-6">
              <button
                onClick={() => navigate(-1)}
                className="flex items-center gap-2 px-4 py-2 bg-white/10 hover:bg-white/20 active:scale-95 backdrop-blur-md rounded-full text-white transition-all focus:outline-none focus:ring-2 focus:ring-white/30"
              >
                <ArrowLeft className="w-5 h-5" />
                <span className="text-sm hidden md:inline" style={{ fontWeight: 500 }}>Back</span>
              </button>

              <div className="flex items-center gap-2">
                <button className="p-2.5 hover:bg-white/10 active:scale-95 rounded-full text-white/80 hover:text-white transition-all focus:outline-none">
                  <Subtitles className="w-5 h-5" />
                </button>
                <button className="p-2.5 hover:bg-white/10 active:scale-95 rounded-full text-white/80 hover:text-white transition-all focus:outline-none">
                  <Settings className="w-5 h-5" />
                </button>
              </div>
            </div>

            {/* ─── Center Play indicator (shows briefly on pause) ─── */}
            <div className="flex items-center justify-center pointer-events-none">
              <AnimatePresence>
                {!isPlaying && (
                  <motion.div
                    initial={{ scale: 0.7, opacity: 0 }}
                    animate={{ scale: 1, opacity: 1 }}
                    exit={{ scale: 1.2, opacity: 0 }}
                    transition={{ duration: 0.2 }}
                    className="w-24 h-24 rounded-full bg-black/50 backdrop-blur-sm border-2 border-white/25 flex items-center justify-center"
                  >
                    <Play className="w-10 h-10 fill-white text-white ml-2" />
                  </motion.div>
                )}
              </AnimatePresence>
            </div>

            {/* ─── Bottom Controls ─── */}
            <div className="px-6 md:px-10 py-6 space-y-4 max-w-5xl mx-auto w-full">
              {/* Title */}
              <div>
                <h1 className="text-white" style={{ fontSize: 'clamp(1.1rem, 3vw, 1.6rem)', fontWeight: 800, textShadow: '0 2px 12px rgba(0,0,0,0.8)' }}>
                  {content.title}
                </h1>
                <p className="text-white/55 text-sm mt-0.5">{content.subtitle}</p>
              </div>

              {/* Progress Bar — M3 style */}
              <div
                className="group relative h-1 hover:h-3 bg-white/20 rounded-full cursor-pointer transition-all duration-150"
                onClick={handleProgressClick}
                onMouseDown={() => setIsSeeking(true)}
                onMouseUp={() => setIsSeeking(false)}
              >
                {/* Buffered */}
                <div className="absolute top-0 left-0 h-full bg-white/15 rounded-full" style={{ width: `${Math.min(100, progress + 12)}%` }} />
                {/* Played */}
                <div
                  className="absolute top-0 left-0 h-full bg-[#C6A75E] rounded-full"
                  style={{ width: `${progress}%`, boxShadow: '0 0 8px rgba(198,167,94,0.6)' }}
                >
                  <div className="absolute right-0 top-1/2 -translate-y-1/2 w-3 h-3 bg-white rounded-full shadow-lg scale-0 group-hover:scale-100 transition-transform" />
                </div>
              </div>

              {/* Actions Row */}
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-4 md:gap-6">
                  {/* Play/Pause */}
                  <button
                    onClick={() => { setIsPlaying(!isPlaying); revealControls(); }}
                    className="text-white hover:text-[#C6A75E] transition-colors focus:outline-none active:scale-90"
                    aria-label={isPlaying ? 'Pause' : 'Play'}
                  >
                    {isPlaying ? <Pause className="w-7 h-7 fill-current" /> : <Play className="w-7 h-7 fill-current" />}
                  </button>

                  {/* Skip */}
                  <button className="text-white/70 hover:text-white active:scale-90 transition-all focus:outline-none">
                    <SkipBack className="w-5 h-5" />
                  </button>
                  <button className="text-white/70 hover:text-white active:scale-90 transition-all focus:outline-none">
                    <SkipForward className="w-5 h-5" />
                  </button>

                  {/* Volume */}
                  <div className="flex items-center gap-2 group/vol">
                    <button
                      onClick={() => setMuted(!muted)}
                      className="text-white/70 hover:text-white transition-colors focus:outline-none active:scale-90"
                    >
                      {muted || volume === 0 ? <VolumeX className="w-5 h-5" /> : <Volume2 className="w-5 h-5" />}
                    </button>
                    <div className="w-0 overflow-hidden group-hover/vol:w-20 transition-all duration-250">
                      <div className="h-1 bg-white/25 rounded-full w-20 mx-1 relative cursor-pointer">
                        <div className="h-full bg-white rounded-full" style={{ width: `${muted ? 0 : volume}%` }} />
                      </div>
                    </div>
                  </div>

                  {/* Time */}
                  <div className="flex items-center gap-1.5 text-white/60" style={{ fontSize: '0.8rem', fontFamily: '"JetBrains Mono", "Fira Code", monospace', fontWeight: 500 }}>
                    <span className="text-white">{fmt(elapsed)}</span>
                    <span>/</span>
                    <span>45:00</span>
                  </div>
                </div>

                {/* Right side */}
                <button
                  className="p-2 hover:bg-white/10 rounded-full text-white/70 hover:text-white transition-all focus:outline-none active:scale-90"
                  title="Fullscreen"
                >
                  <Maximize className="w-5 h-5" />
                </button>
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

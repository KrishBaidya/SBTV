import { useEffect } from 'react';
import { useNavigate } from 'react-router';
import { motion } from 'motion/react';

// Pre-computed particle data — deterministic so no random on re-render
const PARTICLES = Array.from({ length: 14 }, (_, i) => ({
  id: i,
  left: `${(i * 7.1 + 5) % 90}%`,
  top: `${(i * 11.3 + 10) % 85}%`,
  delay: (i * 0.22) % 2.2,
  duration: 2.4 + (i % 4) * 0.4,
  size: i % 3 === 0 ? 3 : i % 3 === 1 ? 2 : 1.5,
}));

export function SplashScreen() {
  const navigate = useNavigate();

  useEffect(() => {
    const timer = setTimeout(() => {
      const hasPlaylist = localStorage.getItem('sb-tv-playlist');
      navigate(hasPlaylist ? '/home' : '/add-playlist', { replace: true });
    }, 3200);
    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <div
      className="fixed inset-0 flex items-center justify-center overflow-hidden"
      style={{ background: 'radial-gradient(ellipse at 50% 60%, #12182e 0%, #0B0F1A 70%)' }}
    >
      {/* Ambient glow blobs */}
      <div
        className="absolute w-96 h-96 rounded-full pointer-events-none"
        style={{
          top: '20%',
          left: '50%',
          transform: 'translateX(-50%)',
          background: 'radial-gradient(circle, rgba(198,167,94,0.08) 0%, transparent 70%)',
          filter: 'blur(40px)',
        }}
      />
      <div
        className="absolute w-72 h-72 rounded-full pointer-events-none"
        style={{
          bottom: '15%',
          right: '20%',
          background: 'radial-gradient(circle, rgba(110,14,46,0.12) 0%, transparent 70%)',
          filter: 'blur(60px)',
        }}
      />

      {/* Floating gold particles */}
      <div className="absolute inset-0 pointer-events-none">
        {PARTICLES.map((p) => (
          <motion.span
            key={p.id}
            className="absolute rounded-full bg-[#C6A75E]"
            style={{
              left: p.left,
              top: p.top,
              width: p.size,
              height: p.size,
            }}
            initial={{ opacity: 0, scale: 0 }}
            animate={{ opacity: [0, 0.7, 0], scale: [0, 1.2, 0], y: [0, -60] }}
            transition={{
              duration: p.duration,
              delay: p.delay,
              repeat: Infinity,
              ease: 'easeInOut',
            }}
          />
        ))}
      </div>

      {/* Logo + tagline */}
      <motion.div
        className="z-10 flex flex-col items-center gap-5 select-none"
        initial={{ opacity: 0, scale: 0.85 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 1.1, ease: [0.2, 0, 0, 1] }}
      >
        <motion.h1
          className="text-transparent bg-clip-text"
          style={{
            fontSize: 'clamp(4rem, 12vw, 8rem)',
            fontWeight: 900,
            letterSpacing: '-0.05em',
            backgroundImage: 'linear-gradient(135deg, #F5F2EB 0%, #C6A75E 55%, #E5CCa0 100%)',
          }}
          animate={{
            filter: [
              'drop-shadow(0 0 12px rgba(198,167,94,0.4))',
              'drop-shadow(0 0 30px rgba(198,167,94,0.7))',
              'drop-shadow(0 0 12px rgba(198,167,94,0.4))',
            ],
          }}
          transition={{ duration: 2.5, repeat: Infinity, ease: 'easeInOut' }}
        >
          SB TV
        </motion.h1>

        {/* Animated separator line */}
        <motion.div
          initial={{ scaleX: 0, opacity: 0 }}
          animate={{ scaleX: 1, opacity: 1 }}
          transition={{ delay: 0.6, duration: 0.9, ease: [0.2, 0, 0, 1] }}
          className="w-32 h-px"
          style={{ background: 'linear-gradient(90deg, transparent, #C6A75E, transparent)' }}
        />

        <motion.p
          className="text-[#F5F2EB]/50 tracking-[0.35em] uppercase"
          style={{ fontSize: '0.7rem', fontWeight: 500 }}
          initial={{ opacity: 0, y: 8 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.8, duration: 0.7 }}
        >
          Premium Streaming Experience
        </motion.p>

        {/* Loading dots */}
        <div className="flex gap-1.5 mt-4">
          {[0, 1, 2].map((i) => (
            <motion.span
              key={i}
              className="w-1.5 h-1.5 rounded-full bg-[#C6A75E]/60"
              animate={{ opacity: [0.3, 1, 0.3] }}
              transition={{ duration: 1.2, delay: i * 0.2, repeat: Infinity }}
            />
          ))}
        </div>
      </motion.div>
    </div>
  );
}

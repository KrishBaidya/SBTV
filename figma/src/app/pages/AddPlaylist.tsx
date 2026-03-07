import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router';
import { motion } from 'motion/react';
import { toast } from 'sonner';
import { LockKeyhole, User, Link, ShieldCheck, PlayCircle, Tv } from 'lucide-react';
import { cn } from '../../lib/utils';

type FormData = {
  name: string;
  url: string;
  username?: string;
  password?: string;
};

function FieldIcon({ children }: { children: React.ReactNode }) {
  return (
    <span className="absolute left-3.5 top-1/2 -translate-y-1/2 text-[#F5F2EB]/25 group-focus-within/input:text-[#C6A75E] transition-colors duration-150 pointer-events-none">
      {children}
    </span>
  );
}

export function AddPlaylist() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const { register, handleSubmit, formState: { errors } } = useForm<FormData>();

  const onSubmit = async (data: FormData) => {
    setLoading(true);
    await new Promise((r) => setTimeout(r, 1400));
    localStorage.setItem('sb-tv-playlist', JSON.stringify(data));
    toast.success('Playlist connected!', { description: 'Redirecting to your dashboard…' });
    setLoading(false);
    navigate('/home');
  };

  return (
    <div
      className="min-h-screen flex items-center justify-center px-4 py-10 relative overflow-hidden"
      style={{ background: '#0B0F1A' }}
    >
      {/* Ambient blobs */}
      <div className="pointer-events-none absolute inset-0">
        <div
          className="absolute rounded-full"
          style={{ top: '-10%', right: '-5%', width: '60vw', height: '60vw', background: 'radial-gradient(circle, rgba(198,167,94,0.06) 0%, transparent 65%)', filter: 'blur(60px)' }}
        />
        <div
          className="absolute rounded-full"
          style={{ bottom: '-15%', left: '-10%', width: '55vw', height: '55vw', background: 'radial-gradient(circle, rgba(110,14,46,0.10) 0%, transparent 65%)', filter: 'blur(80px)' }}
        />
      </div>

      {/* Card */}
      <motion.div
        initial={{ opacity: 0, y: 24, scale: 0.97 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        transition={{ duration: 0.6, ease: [0.2, 0, 0, 1] }}
        className="w-full max-w-md z-10"
      >
        <div
          className="relative overflow-hidden rounded-3xl p-8"
          style={{
            background: 'rgba(20, 24, 32, 0.9)',
            backdropFilter: 'blur(24px)',
            border: '1px solid rgba(255,255,255,0.07)',
            boxShadow: '0 32px 80px rgba(0,0,0,0.6)',
          }}
        >
          {/* Shine shimmer */}
          <div
            className="pointer-events-none absolute inset-0 rounded-3xl"
            style={{ background: 'linear-gradient(135deg, rgba(255,255,255,0.03) 0%, transparent 50%)' }}
          />

          {/* Header */}
          <div className="flex flex-col items-center mb-8">
            <div
              className="w-16 h-16 rounded-2xl flex items-center justify-center mb-5"
              style={{
                background: 'linear-gradient(135deg, #6E0E2E 0%, #1a1020 100%)',
                border: '1px solid rgba(198,167,94,0.2)',
                boxShadow: '0 8px 32px rgba(110,14,46,0.3)',
              }}
            >
              <Tv className="w-8 h-8 text-[#C6A75E]" />
            </div>

            <h1
              className="text-transparent bg-clip-text"
              style={{
                fontSize: '2rem',
                fontWeight: 900,
                letterSpacing: '-0.04em',
                backgroundImage: 'linear-gradient(135deg, #F5F2EB 0%, #C6A75E 100%)',
                textShadow: undefined,
              }}
            >
              SB TV
            </h1>
            <p className="text-[#F5F2EB]/40 text-sm mt-1 text-center">
              Connect your M3U / Xtream service to start watching
            </p>
          </div>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            {/* Playlist Name */}
            <div className="space-y-1">
              <label className="text-[#C6A75E] ml-1 uppercase tracking-wider" style={{ fontSize: '0.65rem', fontWeight: 700 }}>
                Playlist Name
              </label>
              <div className="relative group/input">
                <input
                  {...register('name', { required: true })}
                  placeholder="e.g. My Home TV"
                  className={cn(
                    'w-full bg-[#0B0F1A]/60 border rounded-xl pl-10 pr-4 py-3 text-[#F5F2EB] placeholder-[#F5F2EB]/20 focus:outline-none focus:ring-1 transition-all',
                    errors.name
                      ? 'border-red-500/60 focus:border-red-500 focus:ring-red-500/30'
                      : 'border-white/10 hover:border-white/20 focus:border-[#C6A75E]/50 focus:ring-[#C6A75E]/25'
                  )}
                  style={{ fontSize: '0.9rem' }}
                />
                <FieldIcon><PlayCircle className="w-4 h-4" /></FieldIcon>
              </div>
              {errors.name && <p className="text-red-400 ml-1 text-xs">Playlist name is required</p>}
            </div>

            {/* Server URL */}
            <div className="space-y-1">
              <label className="text-[#C6A75E] ml-1 uppercase tracking-wider" style={{ fontSize: '0.65rem', fontWeight: 700 }}>
                M3U URL / Server URL
              </label>
              <div className="relative group/input">
                <input
                  {...register('url', { required: true })}
                  placeholder="http://server.example.com:8080"
                  className={cn(
                    'w-full bg-[#0B0F1A]/60 border rounded-xl pl-10 pr-4 py-3 text-[#F5F2EB] placeholder-[#F5F2EB]/20 focus:outline-none focus:ring-1 transition-all',
                    errors.url
                      ? 'border-red-500/60 focus:border-red-500 focus:ring-red-500/30'
                      : 'border-white/10 hover:border-white/20 focus:border-[#C6A75E]/50 focus:ring-[#C6A75E]/25'
                  )}
                  style={{ fontSize: '0.9rem' }}
                />
                <FieldIcon><Link className="w-4 h-4" /></FieldIcon>
              </div>
              {errors.url && <p className="text-red-400 ml-1 text-xs">Server URL is required</p>}
            </div>

            {/* Username + Password */}
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1">
                <label className="text-[#C6A75E] ml-1 uppercase tracking-wider" style={{ fontSize: '0.65rem', fontWeight: 700 }}>
                  Username
                </label>
                <div className="relative group/input">
                  <input
                    {...register('username')}
                    placeholder="User"
                    className="w-full bg-[#0B0F1A]/60 border border-white/10 hover:border-white/20 focus:border-[#C6A75E]/50 focus:ring-1 focus:ring-[#C6A75E]/25 rounded-xl pl-10 pr-3 py-3 text-[#F5F2EB] placeholder-[#F5F2EB]/20 focus:outline-none transition-all"
                    style={{ fontSize: '0.9rem' }}
                  />
                  <FieldIcon><User className="w-4 h-4" /></FieldIcon>
                </div>
              </div>

              <div className="space-y-1">
                <label className="text-[#C6A75E] ml-1 uppercase tracking-wider" style={{ fontSize: '0.65rem', fontWeight: 700 }}>
                  Password
                </label>
                <div className="relative group/input">
                  <input
                    type="password"
                    {...register('password')}
                    placeholder="••••••"
                    className="w-full bg-[#0B0F1A]/60 border border-white/10 hover:border-white/20 focus:border-[#C6A75E]/50 focus:ring-1 focus:ring-[#C6A75E]/25 rounded-xl pl-10 pr-3 py-3 text-[#F5F2EB] placeholder-[#F5F2EB]/20 focus:outline-none transition-all"
                    style={{ fontSize: '0.9rem' }}
                  />
                  <FieldIcon><LockKeyhole className="w-4 h-4" /></FieldIcon>
                </div>
              </div>
            </div>

            {/* Submit — M3 Filled Button */}
            <button
              type="submit"
              disabled={loading}
              className="w-full mt-2 py-3.5 rounded-xl flex items-center justify-center gap-2 transition-all active:scale-95 focus:outline-none focus:ring-2 focus:ring-[#C6A75E]/50 disabled:opacity-60 disabled:cursor-not-allowed"
              style={{
                background: loading ? 'rgba(198,167,94,0.5)' : 'linear-gradient(135deg, #C6A75E 0%, #E5CCa0 100%)',
                color: '#0B0F1A',
                fontWeight: 700,
                fontSize: '0.95rem',
                boxShadow: loading ? 'none' : '0 4px 20px rgba(198,167,94,0.35)',
              }}
            >
              {loading ? (
                <>
                  <span className="w-4 h-4 border-2 border-[#0B0F1A]/40 border-t-[#0B0F1A] rounded-full animate-spin" />
                  <span>Connecting…</span>
                </>
              ) : (
                <>
                  <ShieldCheck className="w-5 h-5" />
                  <span>Secure Connect</span>
                </>
              )}
            </button>
          </form>
        </div>

        <p className="text-center text-[#F5F2EB]/20 mt-5" style={{ fontSize: '0.7rem' }}>
          Your credentials are stored locally and never transmitted to our servers.
        </p>
      </motion.div>
    </div>
  );
}

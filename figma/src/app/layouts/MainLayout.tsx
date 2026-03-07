import { Outlet, NavLink, useLocation } from 'react-router';
import { Tv, Film, MonitorPlay, Settings, Home as HomeIcon } from 'lucide-react';
import { useEffect, useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { cn } from '../../lib/utils';

const navItems = [
  { name: 'Home', path: '/home', icon: HomeIcon },
  { name: 'TV', path: '/tv', icon: Tv },
  { name: 'Movies', path: '/movies', icon: Film },
  { name: 'Series', path: '/series', icon: MonitorPlay },
  { name: 'Settings', path: '/settings', icon: Settings },
];

export function MainLayout() {
  const location = useLocation();
  const [isMobile, setIsMobile] = useState(false);
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const check = () => setIsMobile(window.innerWidth < 768);
    check();
    window.addEventListener('resize', check);
    return () => window.removeEventListener('resize', check);
  }, []);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 40);
    window.addEventListener('scroll', onScroll, { passive: true });
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  return (
    <div className="min-h-screen bg-[#0B0F1A] text-[#F5F2EB]" style={{ fontFamily: '"Inter", sans-serif' }}>

      {/* ─── Desktop / TV Top Navigation ─── */}
      {!isMobile && (
        <header
          className={cn(
            'fixed top-0 left-0 right-0 z-50 transition-all duration-300',
            scrolled
              ? 'bg-[#0B0F1A]/95 backdrop-blur-2xl shadow-[0_1px_0_rgba(255,255,255,0.06)]'
              : 'bg-gradient-to-b from-[#0B0F1A]/90 via-[#0B0F1A]/50 to-transparent'
          )}
        >
          <div className="flex items-center justify-between px-6 md:px-10 h-16">
            {/* Logo */}
            <NavLink to="/home" className="flex-shrink-0 select-none">
              <span
                className="text-2xl tracking-tighter text-[#C6A75E]"
                style={{
                  fontWeight: 900,
                  textShadow: '0 0 24px rgba(198,167,94,0.45)',
                  letterSpacing: '-0.04em',
                }}
              >
                SB TV
              </span>
            </NavLink>

            {/* Nav Items — M3 Navigation Bar style */}
            <nav className="flex items-center gap-1">
              {navItems.map((item) => (
                <NavLink
                  key={item.name}
                  to={item.path}
                  className={({ isActive }) =>
                    cn(
                      'relative flex items-center gap-2 px-4 py-2 rounded-full select-none transition-colors duration-200',
                      isActive
                        ? 'text-[#C6A75E]'
                        : 'text-[#F5F2EB]/55 hover:text-[#F5F2EB] hover:bg-white/[0.08]'
                    )
                  }
                >
                  {({ isActive }) => (
                    <>
                      {isActive && (
                        <motion.span
                          layoutId="nav-pill"
                          className="absolute inset-0 rounded-full bg-[#C6A75E]/[0.14]"
                          transition={{ type: 'spring', bounce: 0.2, duration: 0.45 }}
                        />
                      )}
                      <item.icon
                        className={cn('w-[18px] h-[18px] relative z-10 flex-shrink-0', isActive && 'drop-shadow-[0_0_6px_rgba(198,167,94,0.7)]')}
                      />
                      <span className="relative z-10 text-sm" style={{ fontWeight: isActive ? 600 : 500 }}>
                        {item.name}
                      </span>
                    </>
                  )}
                </NavLink>
              ))}
            </nav>

            {/* User Avatar */}
            <div className="w-9 h-9 rounded-full bg-gradient-to-br from-[#6E0E2E] to-[#1a1020] border border-[#C6A75E]/30 flex items-center justify-center text-xs text-[#C6A75E] cursor-pointer hover:ring-2 hover:ring-[#C6A75E]/40 transition-all flex-shrink-0 select-none" style={{ fontWeight: 700 }}>
              SB
            </div>
          </div>
        </header>
      )}

      {/* ─── Main Content — no horizontal padding, each page owns its own ─── */}
      <main className={cn('relative min-w-0', !isMobile ? 'pt-16' : 'pb-[72px]')}>
        <AnimatePresence mode="wait" initial={false}>
          <motion.div
            key={location.pathname}
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.18, ease: 'easeOut' }}
          >
            <Outlet />
          </motion.div>
        </AnimatePresence>
      </main>

      {/* ─── Mobile Bottom Navigation — M3 NavigationBar ─── */}
      {isMobile && (
        <nav className="fixed bottom-0 left-0 right-0 z-50 bg-[#0F121D]/95 backdrop-blur-xl border-t border-white/[0.07]">
          <div className="flex items-center justify-around px-2 py-2">
            {navItems.map((item) => (
              <NavLink
                key={item.name}
                to={item.path}
                className={({ isActive }) =>
                  cn(
                    'relative flex flex-col items-center gap-1 transition-colors duration-200 select-none',
                    isActive ? 'text-[#C6A75E]' : 'text-[#F5F2EB]/45'
                  )
                }
              >
                {({ isActive }) => (
                  <>
                    {/* M3 pill indicator behind icon */}
                    <div className="relative flex items-center justify-center w-16 h-8">
                      {isActive && (
                        <motion.span
                          layoutId="mobile-nav-pill"
                          className="absolute inset-0 rounded-full bg-[#C6A75E]/20"
                          transition={{ type: 'spring', bounce: 0.2, duration: 0.45 }}
                        />
                      )}
                      <item.icon
                        className={cn(
                          'w-5 h-5 relative z-10',
                          isActive && 'drop-shadow-[0_0_8px_rgba(198,167,94,0.6)]'
                        )}
                      />
                    </div>
                    <span className="text-[10px]" style={{ fontWeight: isActive ? 600 : 500 }}>
                      {item.name}
                    </span>
                  </>
                )}
              </NavLink>
            ))}
          </div>
        </nav>
      )}
    </div>
  );
}

import { useState } from 'react';
import {
  User, List, PlayCircle, Settings as SettingsIcon,
  Shield, Moon, Info, LogOut, ChevronRight,
  Wifi, Zap, Monitor, Bell, CreditCard,
} from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import { useNavigate } from 'react-router';
import { toast } from 'sonner';
import { cn } from '../../lib/utils';

const TABS = [
  { id: 'account',  label: 'Account Info',    icon: User },
  { id: 'playlist', label: 'Manage Playlist', icon: List },
  { id: 'player',   label: 'Player Settings', icon: PlayCircle },
  { id: 'parental', label: 'Parental Control',icon: Shield },
  { id: 'theme',    label: 'Theme & Display', icon: Moon },
  { id: 'about',    label: 'About SB TV',     icon: Info },
];

function Toggle({ checked = false, onChange }: { checked?: boolean; onChange?: (v: boolean) => void }) {
  const [on, setOn] = useState(checked);
  const toggle = () => { setOn(!on); onChange?.(!on); };
  return (
    <button
      onClick={toggle}
      className={cn(
        'relative w-12 h-6 rounded-full transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-[#C6A75E]/50',
        on ? 'bg-[#C6A75E]' : 'bg-white/15'
      )}
      role="switch"
      aria-checked={on}
    >
      <span
        className={cn(
          'absolute top-1 w-4 h-4 rounded-full bg-white shadow-sm transition-transform duration-200',
          on ? 'translate-x-7' : 'translate-x-1'
        )}
      />
    </button>
  );
}

export function Settings() {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('account');

  const handleLogout = () => {
    localStorage.removeItem('sb-tv-playlist');
    toast.success('Logged out successfully');
    navigate('/add-playlist');
  };

  return (
    <div className="px-4 md:px-8 lg:px-12 py-6 pb-16">
      {/* Page Header */}
      <div className="flex items-center gap-3 mb-8">
        <span className="w-1 h-6 rounded-full bg-[#C6A75E]" style={{ boxShadow: '0 0 8px rgba(198,167,94,0.5)' }} />
        <h1 className="text-[#F5F2EB]" style={{ fontSize: '1.6rem', fontWeight: 900, letterSpacing: '-0.02em' }}>
          Settings
        </h1>
      </div>

      <div className="flex flex-col md:flex-row gap-6">

        {/* Sidebar — M3 Navigation Drawer style */}
        <div className="w-full md:w-56 flex-shrink-0">
          <div className="bg-[#141820] rounded-2xl border border-white/[0.06] overflow-hidden">
            <div className="p-2 space-y-0.5">
              {TABS.map((tab) => (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={cn(
                    'relative w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all text-left focus:outline-none active:scale-[0.97]',
                    activeTab === tab.id
                      ? 'bg-[#C6A75E]/[0.15] text-[#C6A75E]'
                      : 'text-[#F5F2EB]/55 hover:text-[#F5F2EB] hover:bg-white/[0.06]'
                  )}
                  style={{ fontSize: '0.85rem', fontWeight: activeTab === tab.id ? 700 : 500 }}
                >
                  {activeTab === tab.id && (
                    <motion.span
                      layoutId="settings-pill"
                      className="absolute left-2 w-1 h-6 rounded-full bg-[#C6A75E]"
                      transition={{ type: 'spring', bounce: 0.2, duration: 0.4 }}
                    />
                  )}
                  <tab.icon className="w-4 h-4 flex-shrink-0 relative z-10" />
                  <span className="relative z-10">{tab.label}</span>
                </button>
              ))}
            </div>

            <div className="p-2 border-t border-white/[0.06]">
              <button
                onClick={handleLogout}
                className="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-red-400 hover:bg-red-500/10 hover:text-red-300 transition-colors text-left focus:outline-none active:scale-[0.97]"
                style={{ fontSize: '0.85rem', fontWeight: 500 }}
              >
                <LogOut className="w-4 h-4 flex-shrink-0" />
                Logout
              </button>
            </div>
          </div>
        </div>

        {/* Content Area */}
        <div className="flex-1 min-w-0">
          <AnimatePresence mode="wait" initial={false}>
            <motion.div
              key={activeTab}
              initial={{ opacity: 0, x: 12 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: -12 }}
              transition={{ duration: 0.2, ease: [0.2, 0, 0, 1] }}
              className="space-y-4"
            >

              {/* ACCOUNT */}
              {activeTab === 'account' && (
                <div className="space-y-4">
                  {/* Profile card */}
                  <div className="bg-[#141820] rounded-2xl border border-white/[0.06] p-6 flex flex-col sm:flex-row items-center sm:items-start gap-5">
                    <div
                      className="w-20 h-20 rounded-full flex items-center justify-center text-[#C6A75E] flex-shrink-0 border-2 border-[#C6A75E]/25"
                      style={{
                        background: 'linear-gradient(135deg, #1a1020 0%, #6E0E2E 100%)',
                        fontSize: '1.5rem',
                        fontWeight: 800,
                        boxShadow: '0 0 24px rgba(110,14,46,0.3)',
                      }}
                    >
                      JD
                    </div>
                    <div className="text-center sm:text-left">
                      <h3 className="text-[#F5F2EB]" style={{ fontSize: '1.2rem', fontWeight: 800 }}>John Doe</h3>
                      <p className="text-[#F5F2EB]/50 text-sm mt-0.5">john.doe@example.com</p>
                      <div className="flex items-center gap-2 mt-2 justify-center sm:justify-start flex-wrap">
                        <span className="px-2.5 py-0.5 bg-[#C6A75E]/20 text-[#C6A75E] rounded-full" style={{ fontSize: '0.65rem', fontWeight: 800 }}>PREMIUM</span>
                        <span className="px-2.5 py-0.5 bg-green-500/15 text-green-400 rounded-full" style={{ fontSize: '0.65rem', fontWeight: 700 }}>ACTIVE</span>
                        <span className="text-[#F5F2EB]/30" style={{ fontSize: '0.65rem' }}>ID: 8734-9283</span>
                      </div>
                    </div>
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    {[
                      { label: 'Subscription', value: 'Premium Plan', sub: 'Exp: Dec 2026', icon: CreditCard },
                      { label: 'Active Devices', value: '3 / 5 Devices', sub: 'Manage →', icon: Monitor },
                    ].map((card) => (
                      <div key={card.label} className="bg-[#141820] rounded-2xl border border-white/[0.06] p-5 flex items-center gap-4">
                        <div className="w-10 h-10 rounded-xl bg-[#C6A75E]/10 flex items-center justify-center flex-shrink-0">
                          <card.icon className="w-5 h-5 text-[#C6A75E]" />
                        </div>
                        <div>
                          <p className="text-[#F5F2EB]/50" style={{ fontSize: '0.7rem', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.05em' }}>{card.label}</p>
                          <p className="text-[#F5F2EB]" style={{ fontWeight: 700, fontSize: '0.9rem' }}>{card.value}</p>
                          <p className="text-[#C6A75E]" style={{ fontSize: '0.72rem', fontWeight: 500 }}>{card.sub}</p>
                        </div>
                      </div>
                    ))}
                  </div>

                  {/* Preferences */}
                  <div className="bg-[#141820] rounded-2xl border border-white/[0.06] overflow-hidden">
                    <div className="px-5 py-3 border-b border-white/[0.06]">
                      <p className="text-[#F5F2EB]/50 uppercase" style={{ fontSize: '0.65rem', fontWeight: 700, letterSpacing: '0.08em' }}>Preferences</p>
                    </div>
                    {[
                      { label: 'Stream on Wi-Fi only', sub: 'Save mobile data', icon: Wifi, checked: true },
                      { label: 'Push Notifications', sub: 'New content alerts', icon: Bell, checked: false },
                    ].map((pref) => (
                      <div key={pref.label} className="flex items-center justify-between px-5 py-4 border-b border-white/[0.04] last:border-0 hover:bg-white/[0.03] transition-colors">
                        <div className="flex items-center gap-3">
                          <div className="w-8 h-8 rounded-xl bg-[#0B0F1A] flex items-center justify-center">
                            <pref.icon className="w-4 h-4 text-[#F5F2EB]/50" />
                          </div>
                          <div>
                            <p className="text-[#F5F2EB]" style={{ fontSize: '0.85rem', fontWeight: 600 }}>{pref.label}</p>
                            <p className="text-[#F5F2EB]/40" style={{ fontSize: '0.72rem' }}>{pref.sub}</p>
                          </div>
                        </div>
                        <Toggle checked={pref.checked} />
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* PLAYER SETTINGS */}
              {activeTab === 'player' && (
                <div className="space-y-4">
                  <h2 className="text-[#F5F2EB]" style={{ fontSize: '1.2rem', fontWeight: 800, letterSpacing: '-0.01em' }}>Player Settings</h2>

                  <div className="bg-[#141820] rounded-2xl border border-white/[0.06] overflow-hidden">
                    {[
                      { label: 'Default Quality', sub: 'Auto-select best quality', options: ['Auto', '4K', '1080p', '720p', '480p'], icon: Monitor },
                      { label: 'Buffer Size', sub: 'Preload content duration', options: ['Normal (10s)', 'Large (30s)', 'Max (60s)'], icon: Zap },
                    ].map((setting, i, arr) => (
                      <div key={setting.label} className={cn('flex items-center justify-between px-5 py-4 hover:bg-white/[0.03] transition-colors', i < arr.length - 1 && 'border-b border-white/[0.04]')}>
                        <div className="flex items-center gap-3">
                          <div className="w-8 h-8 rounded-xl bg-[#0B0F1A] flex items-center justify-center">
                            <setting.icon className="w-4 h-4 text-[#F5F2EB]/50" />
                          </div>
                          <div>
                            <p className="text-[#F5F2EB]" style={{ fontSize: '0.85rem', fontWeight: 600 }}>{setting.label}</p>
                            <p className="text-[#F5F2EB]/40" style={{ fontSize: '0.72rem' }}>{setting.sub}</p>
                          </div>
                        </div>
                        <select className="bg-[#0B0F1A] text-[#F5F2EB] border border-white/10 rounded-xl px-3 py-1.5 text-sm focus:outline-none focus:border-[#C6A75E]/50 cursor-pointer">
                          {setting.options.map((o) => <option key={o}>{o}</option>)}
                        </select>
                      </div>
                    ))}

                    <div className="flex items-center justify-between px-5 py-4 hover:bg-white/[0.03] transition-colors">
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-xl bg-[#0B0F1A] flex items-center justify-center">
                          <Zap className="w-4 h-4 text-[#F5F2EB]/50" />
                        </div>
                        <div>
                          <p className="text-[#F5F2EB]" style={{ fontSize: '0.85rem', fontWeight: 600 }}>Hardware Acceleration</p>
                          <p className="text-[#F5F2EB]/40" style={{ fontSize: '0.72rem' }}>Use GPU for decoding</p>
                        </div>
                      </div>
                      <Toggle checked />
                    </div>
                  </div>
                </div>
              )}

              {/* OTHER TABS */}
              {['playlist', 'parental', 'theme', 'about'].includes(activeTab) && (
                <div className="flex flex-col items-center justify-center py-24 text-center bg-[#141820] rounded-2xl border border-white/[0.06]">
                  <div className="w-16 h-16 rounded-2xl bg-[#0B0F1A] flex items-center justify-center mb-4">
                    <SettingsIcon className="w-8 h-8 text-[#F5F2EB]/15" />
                  </div>
                  <h3 className="text-[#F5F2EB]" style={{ fontSize: '1.1rem', fontWeight: 700 }}>Coming Soon</h3>
                  <p className="text-[#F5F2EB]/35 mt-2 max-w-xs text-sm">This section is under development in this prototype.</p>
                </div>
              )}

            </motion.div>
          </AnimatePresence>
        </div>
      </div>
    </div>
  );
}
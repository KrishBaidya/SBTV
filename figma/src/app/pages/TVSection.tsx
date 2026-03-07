import { useState } from 'react';
import { channels } from '../lib/mockData';
import { Play, Calendar, Info, Tv } from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import { cn } from '../../lib/utils';

const CATEGORIES = ['All Channels', 'Sports', 'News', 'Entertainment', 'Kids', 'Regional', 'International'];

export function TVSection() {
  const [selectedChannel, setSelectedChannel] = useState(channels[0]);
  const [activeCategory, setActiveCategory] = useState('All Channels');

  const filteredChannels =
    activeCategory === 'All Channels'
      ? channels
      : channels.filter((c) => c.category === activeCategory);

  return (
    <div
      className="flex flex-col gap-4 px-4 md:px-8 lg:px-12 py-6 overflow-hidden"
      style={{ height: 'calc(100dvh - 64px)' }}
    >
      {/* Category Chips — M3 FilterChip style */}
      <div className="flex items-center gap-2 overflow-x-auto pb-1 scrollbar-hide flex-shrink-0">
        {CATEGORIES.map((cat) => (
          <button
            key={cat}
            onClick={() => setActiveCategory(cat)}
            className={cn(
              'px-4 py-1.5 rounded-full text-sm whitespace-nowrap transition-all border focus:outline-none focus:ring-2 focus:ring-[#C6A75E]/50 active:scale-95',
              activeCategory === cat
                ? 'bg-[#C6A75E] text-[#0B0F1A] border-[#C6A75E]'
                : 'bg-white/[0.05] text-[#F5F2EB]/65 border-white/[0.08] hover:bg-white/[0.1] hover:text-[#F5F2EB] hover:border-white/20'
            )}
            style={{ fontWeight: activeCategory === cat ? 700 : 500 }}
          >
            {cat}
          </button>
        ))}
      </div>

      {/* Main Grid */}
      <div className="flex-1 grid grid-cols-1 lg:grid-cols-3 gap-5 overflow-hidden min-h-0">

        {/* Channel List — M3 Surface */}
        <div className="lg:col-span-1 bg-[#141820] rounded-2xl overflow-hidden border border-white/[0.06] flex flex-col min-h-0">
          <div className="px-4 py-3 border-b border-white/[0.06] flex-shrink-0 flex items-center gap-2">
            <Tv className="w-4 h-4 text-[#C6A75E]" />
            <span className="text-[#C6A75E]" style={{ fontSize: '0.85rem', fontWeight: 700 }}>
              Channels ({filteredChannels.length})
            </span>
          </div>

          <div className="flex-1 overflow-y-auto p-2 space-y-1 scrollbar-thin">
            <AnimatePresence mode="popLayout">
              {filteredChannels.map((channel) => {
                const isActive = selectedChannel.id === channel.id;
                return (
                  <motion.button
                    key={channel.id}
                    layout
                    initial={{ opacity: 0, y: 4 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0 }}
                    transition={{ duration: 0.15 }}
                    onClick={() => setSelectedChannel(channel)}
                    className={cn(
                      'w-full flex items-center gap-3 p-3 rounded-xl transition-colors text-left focus:outline-none focus:ring-2 focus:ring-[#C6A75E]/40',
                      isActive
                        ? 'bg-[#C6A75E]/[0.12] border border-[#C6A75E]/30'
                        : 'border border-transparent hover:bg-white/[0.06]'
                    )}
                  >
                    {/* Channel Logo */}
                    <div className="w-11 h-11 rounded-xl bg-[#0B0F1A] overflow-hidden border border-white/[0.08] flex-shrink-0">
                      <img
                        src={channel.logo}
                        alt={channel.name}
                        className="w-full h-full object-cover"
                        loading="lazy"
                      />
                    </div>

                    <div className="flex-1 min-w-0">
                      <p
                        className={cn('truncate transition-colors', isActive ? 'text-[#C6A75E]' : 'text-[#F5F2EB]')}
                        style={{ fontSize: '0.85rem', fontWeight: 700 }}
                      >
                        {channel.name}
                      </p>
                      <p className="text-[#F5F2EB]/45 truncate" style={{ fontSize: '0.72rem' }}>
                        {channel.currentProgram}
                      </p>
                    </div>

                    {isActive && (
                      <span className="w-2 h-2 rounded-full bg-[#C6A75E] flex-shrink-0 animate-pulse" />
                    )}
                  </motion.button>
                );
              })}
            </AnimatePresence>
          </div>
        </div>

        {/* Right: Preview + Info */}
        <div className="lg:col-span-2 flex flex-col gap-4 overflow-y-auto scrollbar-thin min-h-0">

          {/* Video Preview */}
          <div className="relative aspect-video bg-black rounded-2xl overflow-hidden border border-white/[0.06] shadow-2xl flex-shrink-0 group">
            <AnimatePresence mode="wait">
              <motion.div
                key={selectedChannel.id}
                className="absolute inset-0"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
                transition={{ duration: 0.35 }}
              >
                <img
                  src={selectedChannel.logo}
                  alt={selectedChannel.name}
                  className="w-full h-full object-cover opacity-30 grayscale"
                />
                <div
                  className="absolute inset-0"
                  style={{ background: 'radial-gradient(ellipse at center, rgba(11,15,26,0.5) 0%, rgba(11,15,26,0.9) 100%)' }}
                />
                <div className="absolute inset-0 flex items-center justify-center">
                  <div className="text-center space-y-2">
                    <img
                      src={selectedChannel.logo}
                      alt={selectedChannel.name}
                      className="w-20 h-20 mx-auto rounded-2xl opacity-70 object-cover group-hover:opacity-100 transition-opacity"
                    />
                    <p className="text-[#F5F2EB]/30 text-sm">Loading stream…</p>
                  </div>
                </div>
              </motion.div>
            </AnimatePresence>

            {/* Hover play overlay */}
            <div className="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-200">
              <button
                className="w-16 h-16 rounded-full flex items-center justify-center active:scale-90 transition-transform"
                style={{
                  background: '#C6A75E',
                  boxShadow: '0 0 30px rgba(198,167,94,0.5)',
                }}
              >
                <Play className="w-7 h-7 fill-[#0B0F1A] text-[#0B0F1A] ml-1" />
              </button>
            </div>

            {/* Live badge */}
            <div className="absolute top-4 left-4 flex items-center gap-1.5 px-3 py-1 bg-red-600 rounded-full">
              <span className="w-1.5 h-1.5 rounded-full bg-white animate-pulse" />
              <span className="text-white" style={{ fontSize: '0.65rem', fontWeight: 800, letterSpacing: '0.08em' }}>
                LIVE
              </span>
            </div>

            <div className="absolute bottom-3 right-4 text-[#F5F2EB]/30" style={{ fontSize: '0.7rem', fontFamily: 'monospace' }}>
              {selectedChannel.name} • 1080p
            </div>
          </div>

          {/* Channel Info Card — M3 Surface */}
          <div className="bg-[#141820] rounded-2xl p-5 border border-white/[0.06] shadow-lg flex-shrink-0">
            <div className="flex items-start justify-between mb-4">
              <div>
                <h2 className="text-[#F5F2EB]" style={{ fontSize: '1.4rem', fontWeight: 800, letterSpacing: '-0.01em' }}>
                  {selectedChannel.currentProgram}
                </h2>
                <div className="flex items-center gap-3 mt-1.5">
                  <span className="text-[#C6A75E]" style={{ fontSize: '0.85rem', fontWeight: 700 }}>
                    {selectedChannel.name}
                  </span>
                  <span className="text-[#F5F2EB]/25 text-xs">•</span>
                  <span className="text-[#F5F2EB]/50 text-xs">14:00 – 15:30</span>
                  <span className="bg-white/10 px-2 py-0.5 rounded text-[#F5F2EB] border border-white/10" style={{ fontSize: '0.65rem', fontWeight: 600 }}>
                    HD
                  </span>
                </div>
              </div>
              <button className="flex items-center gap-1.5 px-3 py-1.5 bg-white/[0.07] hover:bg-white/[0.12] rounded-xl text-[#F5F2EB]/70 hover:text-[#F5F2EB] transition-colors border border-white/[0.07] text-sm focus:outline-none active:scale-95">
                <Info className="w-3.5 h-3.5" />
                <span style={{ fontSize: '0.8rem', fontWeight: 500 }}>Guide</span>
              </button>
            </div>

            {/* EPG next program */}
            <div className="bg-[#0B0F1A] rounded-xl p-4 border border-white/[0.05]">
              <div className="flex items-center gap-2 mb-3 text-[#F5F2EB]/35 uppercase" style={{ fontSize: '0.65rem', fontWeight: 700, letterSpacing: '0.08em' }}>
                <Calendar className="w-3.5 h-3.5" />
                Up Next
              </div>
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-[#F5F2EB]" style={{ fontSize: '1rem', fontWeight: 700 }}>
                    {selectedChannel.nextProgram}
                  </p>
                  <p className="text-[#F5F2EB]/45 text-xs mt-0.5">15:30 – 17:00</p>
                </div>
                <button className="text-[#C6A75E] text-xs hover:underline focus:outline-none" style={{ fontWeight: 600 }}>
                  Set Reminder
                </button>
              </div>

              {/* Progress bar */}
              <div className="mt-3 h-1 bg-white/10 rounded-full overflow-hidden">
                <div className="h-full bg-[#C6A75E] rounded-full w-[45%]" />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

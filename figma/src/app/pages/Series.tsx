import { useState } from 'react';
import { trendingItems } from '../lib/mockData';
import { Link } from 'react-router';
import { motion } from 'motion/react';
import { ChevronDown, Play, Star, Clock } from 'lucide-react';
import { cn } from '../../lib/utils';

const SEASONS = [1, 2, 3];

export function Series() {
  const [selectedSeason, setSelectedSeason] = useState(1);
  const [showSeasonMenu, setShowSeasonMenu] = useState(false);

  return (
    <div className="pb-16">

      {/* Featured Series Banner — full-bleed within page padding */}
      <div className="relative w-full overflow-hidden" style={{ height: 'clamp(340px, 58vh, 700px)' }}>
        <img
          src="https://images.unsplash.com/photo-1626814026160-2237a95fc5a0?q=80&w=1920"
          alt="Series Banner"
          className="absolute inset-0 w-full h-full object-cover"
          style={{ willChange: 'transform' }}
        />
        <div
          className="absolute inset-0"
          style={{ background: 'linear-gradient(to top, #0B0F1A 0%, rgba(11,15,26,0.5) 55%, rgba(11,15,26,0.15) 100%)' }}
        />
        <div
          className="absolute inset-0"
          style={{ background: 'linear-gradient(to right, rgba(11,15,26,0.8) 0%, transparent 60%)' }}
        />

        <div className="absolute bottom-0 left-0 px-6 md:px-10 lg:px-14 pb-12 space-y-3 max-w-2xl">
          <div className="flex items-center gap-2">
            <span
              className="bg-[#C6A75E] text-[#0B0F1A] px-2.5 py-0.5 rounded-full uppercase tracking-wider"
              style={{ fontSize: '0.6rem', fontWeight: 800 }}
            >
              New Season
            </span>
            <span className="text-[#F5F2EB]/50 text-xs">Web Series</span>
          </div>

          <h1
            className="text-[#F5F2EB]"
            style={{
              fontSize: 'clamp(2rem, 5vw, 3.5rem)',
              fontWeight: 900,
              letterSpacing: '-0.02em',
              textShadow: '0 4px 20px rgba(0,0,0,0.5)',
              lineHeight: 1.05,
            }}
          >
            Cyber Chronicles
          </h1>

          <div className="flex items-center gap-3 text-sm text-[#F5F2EB]/65">
            <span>2025</span>
            <span className="w-1 h-1 rounded-full bg-[#F5F2EB]/25" />
            <span className="border border-[#F5F2EB]/25 px-1.5 py-0.5 rounded text-xs">TV-MA</span>
            <span className="w-1 h-1 rounded-full bg-[#F5F2EB]/25" />
            <span>3 Seasons</span>
            <span className="w-1 h-1 rounded-full bg-[#F5F2EB]/25" />
            <span>Sci-Fi</span>
          </div>

          <p className="text-[#F5F2EB]/70 text-sm hidden md:block" style={{ lineHeight: 1.65, maxWidth: '480px' }}>
            In a future where technology governs all, a hacker discovers a conspiracy that threatens to rewrite reality itself.
          </p>

          <div className="flex items-center gap-3 pt-1">
            <Link
              to="/player/hero-3"
              className="flex items-center gap-2 px-7 py-2.5 rounded-full text-[#0B0F1A] hover:opacity-90 active:scale-95 transition-all focus:outline-none focus:ring-2 focus:ring-[#C6A75E]"
              style={{ background: '#C6A75E', fontWeight: 700, fontSize: '0.9rem', boxShadow: '0 4px 20px rgba(198,167,94,0.35)' }}
            >
              <Play className="w-4 h-4 fill-current" />
              Watch S1 E1
            </Link>

            {/* Season Selector */}
            <div className="relative">
              <button
                onClick={() => setShowSeasonMenu(!showSeasonMenu)}
                className="flex items-center gap-2 px-5 py-2.5 bg-white/10 backdrop-blur-sm text-white rounded-full border border-white/15 hover:bg-white/20 active:scale-95 transition-all focus:outline-none"
                style={{ fontWeight: 600, fontSize: '0.9rem' }}
              >
                Season {selectedSeason}
                <ChevronDown className={cn('w-4 h-4 transition-transform', showSeasonMenu && 'rotate-180')} />
              </button>

              {showSeasonMenu && (
                <motion.div
                  initial={{ opacity: 0, y: 4, scale: 0.95 }}
                  animate={{ opacity: 1, y: 0, scale: 1 }}
                  exit={{ opacity: 0 }}
                  transition={{ duration: 0.15 }}
                  className="absolute top-full mt-2 left-0 bg-[#1C2030] border border-white/10 rounded-2xl overflow-hidden shadow-2xl z-20 min-w-[140px]"
                >
                  {SEASONS.map((s) => (
                    <button
                      key={s}
                      onClick={() => { setSelectedSeason(s); setShowSeasonMenu(false); }}
                      className={cn(
                        'w-full px-4 py-2.5 text-left transition-colors hover:bg-white/[0.08] focus:outline-none',
                        selectedSeason === s ? 'text-[#C6A75E]' : 'text-[#F5F2EB]/70'
                      )}
                      style={{ fontSize: '0.85rem', fontWeight: selectedSeason === s ? 700 : 500 }}
                    >
                      Season {s}
                    </button>
                  ))}
                </motion.div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Rest of content with padding */}
      <div className="px-4 md:px-8 lg:px-12 mt-8 space-y-12">

        {/* Continue Watching */}
        <section>
          <div className="flex items-center gap-3 mb-5">
            <span className="w-1 h-5 rounded-full bg-[#C6A75E]" style={{ boxShadow: '0 0 8px rgba(198,167,94,0.5)' }} />
            <h2 className="text-[#F5F2EB]" style={{ fontSize: '1.15rem', fontWeight: 700 }}>Continue Watching</h2>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
            {trendingItems.slice(0, 4).map((item, idx) => (
              <Link key={item.id} to={`/player/${item.id}`} className="group block focus:outline-none focus:ring-2 focus:ring-[#C6A75E]/60 rounded-xl">
                <div
                  className="relative aspect-video rounded-xl overflow-hidden bg-[#141820]"
                  style={{
                    boxShadow: '0 2px 8px rgba(0,0,0,0.4)',
                    transition: 'box-shadow 0.2s, transform 0.2s cubic-bezier(0.2,0,0,1)',
                    willChange: 'transform',
                  }}
                  onMouseEnter={(e) => {
                    const el = e.currentTarget as HTMLElement;
                    el.style.transform = 'translateY(-4px)';
                    el.style.boxShadow = '0 12px 30px rgba(0,0,0,0.55)';
                  }}
                  onMouseLeave={(e) => {
                    const el = e.currentTarget as HTMLElement;
                    el.style.transform = 'translateY(0)';
                    el.style.boxShadow = '0 2px 8px rgba(0,0,0,0.4)';
                  }}
                >
                  <img
                    src={item.backdrop || item.poster}
                    alt={item.title}
                    className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
                    loading="lazy"
                  />
                  <div className="absolute inset-0 bg-black/30 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                    <div className="w-12 h-12 rounded-full bg-[#C6A75E] flex items-center justify-center" style={{ boxShadow: '0 4px 20px rgba(198,167,94,0.4)' }}>
                      <Play className="w-5 h-5 fill-[#0B0F1A] text-[#0B0F1A] ml-0.5" />
                    </div>
                  </div>
                  {/* Progress */}
                  <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-white/15">
                    <div className="h-full bg-[#C6A75E]" style={{ width: `${30 + idx * 15}%` }} />
                  </div>
                </div>
                <div className="mt-2.5">
                  <p className="text-[#F5F2EB] group-hover:text-[#C6A75E] transition-colors truncate" style={{ fontSize: '0.9rem', fontWeight: 700 }}>
                    {item.title}
                  </p>
                  <p className="text-[#F5F2EB]/45 mt-0.5" style={{ fontSize: '0.75rem' }}>
                    S{selectedSeason} E{idx + 1} • The Discovery
                  </p>
                </div>
              </Link>
            ))}
          </div>
        </section>

        {/* Popular Series Grid */}
        <section>
          <div className="flex items-center gap-3 mb-5">
            <span className="w-1 h-5 rounded-full bg-[#C6A75E]" style={{ boxShadow: '0 0 8px rgba(198,167,94,0.5)' }} />
            <h2 className="text-[#F5F2EB]" style={{ fontSize: '1.15rem', fontWeight: 700 }}>Popular Series</h2>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4">
            {trendingItems.map((item) => (
              <Link key={item.id} to={`/player/${item.id}`} className="group block focus:outline-none focus:ring-2 focus:ring-[#C6A75E]/60 rounded-2xl">
                <div
                  className="relative aspect-[2/3] rounded-2xl overflow-hidden bg-[#141820]"
                  style={{
                    boxShadow: '0 2px 8px rgba(0,0,0,0.45)',
                    transition: 'box-shadow 0.25s, transform 0.25s cubic-bezier(0.2,0,0,1)',
                    willChange: 'transform',
                  }}
                  onMouseEnter={(e) => {
                    const el = e.currentTarget as HTMLElement;
                    el.style.transform = 'translateY(-5px) scale(1.04)';
                    el.style.boxShadow = '0 16px 40px rgba(0,0,0,0.65), 0 0 0 1px rgba(198,167,94,0.2)';
                  }}
                  onMouseLeave={(e) => {
                    const el = e.currentTarget as HTMLElement;
                    el.style.transform = 'translateY(0) scale(1)';
                    el.style.boxShadow = '0 2px 8px rgba(0,0,0,0.45)';
                  }}
                >
                  <img
                    src={item.poster}
                    alt={item.title}
                    className="w-full h-full object-cover"
                    loading="lazy"
                    decoding="async"
                  />
                  <div className="absolute inset-0 bg-white/0 group-hover:bg-white/[0.04] transition-colors duration-200" />

                  <div className="absolute top-2 right-2">
                    <span
                      className="bg-[#C6A75E] text-[#0B0F1A] px-2 py-0.5 rounded-full"
                      style={{ fontSize: '0.55rem', fontWeight: 800 }}
                    >
                      New
                    </span>
                  </div>

                  {/* Rating bottom */}
                  <div className="absolute bottom-2 left-2 flex items-center gap-1">
                    <Star className="w-3 h-3 fill-[#C6A75E] text-[#C6A75E]" />
                    <span className="text-[#F5F2EB]" style={{ fontSize: '0.65rem', fontWeight: 600 }}>
                      {item.rating}
                    </span>
                  </div>
                </div>

                <p className="mt-2 truncate text-[#F5F2EB]/70 group-hover:text-[#F5F2EB] transition-colors" style={{ fontSize: '0.75rem', fontWeight: 500 }}>
                  {item.title}
                </p>
              </Link>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
}

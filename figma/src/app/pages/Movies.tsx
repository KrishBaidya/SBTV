import { useState, useCallback } from 'react';
import { trendingItems, heroItems } from '../lib/mockData';
import { Link } from 'react-router';
import { motion, AnimatePresence } from 'motion/react';
import { Play, Star } from 'lucide-react';
import { cn } from '../../lib/utils';

const CATEGORIES = ['All', 'Action', 'Drama', 'Comedy', 'Thriller', 'Sci-Fi', 'Romance', 'Regional', '4K'];
const allMovies = [...heroItems, ...trendingItems];

export function Movies() {
  const [activeCategory, setActiveCategory] = useState('All');

  const filteredMovies =
    activeCategory === 'All'
      ? allMovies
      : activeCategory === '4K'
      ? allMovies.filter((m) => m.rating >= 8.5)
      : allMovies.filter((m) => m.genre === activeCategory);

  return (
    <div className="px-4 md:px-8 lg:px-12 py-6 pb-16">
      {/* Page header */}
      <div className="flex items-center gap-3 mb-6">
        <span className="w-1 h-6 rounded-full bg-[#C6A75E]" style={{ boxShadow: '0 0 8px rgba(198,167,94,0.5)' }} />
        <h1 className="text-[#F5F2EB]" style={{ fontSize: '1.6rem', fontWeight: 900, letterSpacing: '-0.02em' }}>
          Movies
        </h1>
      </div>

      {/* Category Filter Chips — M3 FilterChip */}
      <div className="flex items-center gap-2 flex-wrap mb-8">
        {CATEGORIES.map((cat) => (
          <button
            key={cat}
            onClick={() => setActiveCategory(cat)}
            className={cn(
              'px-5 py-2 rounded-full transition-all border focus:outline-none focus:ring-2 focus:ring-[#C6A75E]/50 active:scale-95',
              activeCategory === cat
                ? 'bg-[#C6A75E] text-[#0B0F1A] border-[#C6A75E]'
                : 'bg-white/[0.05] text-[#F5F2EB]/65 border-white/[0.08] hover:bg-white/[0.09] hover:text-[#F5F2EB] hover:border-white/20'
            )}
            style={{ fontSize: '0.85rem', fontWeight: activeCategory === cat ? 700 : 500 }}
          >
            {cat}
          </button>
        ))}
      </div>

      {/* Grid — no layout animation (performance) */}
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4 md:gap-5">
        <AnimatePresence mode="popLayout" initial={false}>
          {filteredMovies.map((movie) => (
            <motion.div
              key={movie.id}
              initial={{ opacity: 0, scale: 0.93 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.93 }}
              transition={{ duration: 0.18 }}
            >
              <Link
                to={`/player/${movie.id}`}
                className="group block relative focus:outline-none focus:ring-2 focus:ring-[#C6A75E]/60 rounded-2xl"
              >
                {/* M3 Elevated Card */}
                <div
                  className="relative aspect-[2/3] rounded-2xl overflow-hidden bg-[#141820]"
                  style={{
                    boxShadow: '0 2px 8px rgba(0,0,0,0.5)',
                    transition: 'box-shadow 0.25s, transform 0.25s cubic-bezier(0.2,0,0,1)',
                    willChange: 'transform',
                  }}
                  onMouseEnter={(e) => {
                    const el = e.currentTarget as HTMLElement;
                    el.style.transform = 'translateY(-6px) scale(1.04)';
                    el.style.boxShadow = '0 16px 40px rgba(0,0,0,0.65), 0 0 0 1px rgba(198,167,94,0.25)';
                  }}
                  onMouseLeave={(e) => {
                    const el = e.currentTarget as HTMLElement;
                    el.style.transform = 'translateY(0) scale(1)';
                    el.style.boxShadow = '0 2px 8px rgba(0,0,0,0.5)';
                  }}
                >
                  <img
                    src={movie.poster}
                    alt={movie.title}
                    className="w-full h-full object-cover"
                    loading="lazy"
                    decoding="async"
                  />

                  {/* M3 State layer */}
                  <div className="absolute inset-0 bg-white/0 group-hover:bg-white/[0.04] transition-colors duration-200" />

                  {/* Overlay on hover */}
                  <div
                    className="absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity duration-250 flex flex-col justify-end p-3"
                    style={{ background: 'linear-gradient(to top, rgba(11,15,26,0.96) 0%, rgba(11,15,26,0.4) 55%, transparent 100%)' }}
                  >
                    <p className="text-[#F5F2EB] truncate" style={{ fontSize: '0.8rem', fontWeight: 700 }}>
                      {movie.title}
                    </p>
                    <div className="flex items-center gap-1.5 mt-1">
                      <span className="flex items-center gap-0.5 text-[#C6A75E]" style={{ fontSize: '0.7rem', fontWeight: 600 }}>
                        <Star className="w-2.5 h-2.5 fill-current" />
                        {movie.rating}
                      </span>
                      <span className="text-[#F5F2EB]/50" style={{ fontSize: '0.7rem' }}>
                        {movie.year}
                      </span>
                    </div>

                    {/* Center play icon */}
                    <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2">
                      <div
                        className="w-12 h-12 rounded-full flex items-center justify-center scale-0 group-hover:scale-100 transition-transform duration-200"
                        style={{ background: '#C6A75E', boxShadow: '0 4px 20px rgba(198,167,94,0.5)' }}
                      >
                        <Play className="w-5 h-5 fill-[#0B0F1A] text-[#0B0F1A] ml-0.5" />
                      </div>
                    </div>
                  </div>

                  {/* Genre badge */}
                  <div className="absolute top-2 right-2">
                    <span
                      className="bg-[#0B0F1A]/70 text-[#F5F2EB]/70 px-1.5 py-0.5 rounded backdrop-blur-sm"
                      style={{ fontSize: '0.55rem', fontWeight: 600 }}
                    >
                      {movie.genre}
                    </span>
                  </div>
                </div>

                <p
                  className="mt-2 truncate text-[#F5F2EB]/70 group-hover:text-[#F5F2EB] transition-colors"
                  style={{ fontSize: '0.75rem', fontWeight: 500 }}
                >
                  {movie.title}
                </p>
              </Link>
            </motion.div>
          ))}
        </AnimatePresence>
      </div>

      {filteredMovies.length === 0 && (
        <div className="flex flex-col items-center justify-center py-24 text-center">
          <p className="text-[#F5F2EB]/20 text-5xl mb-4">🎬</p>
          <p className="text-[#F5F2EB]/40" style={{ fontWeight: 600 }}>No movies found in this category</p>
        </div>
      )}
    </div>
  );
}

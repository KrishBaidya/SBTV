import Slider from 'react-slick';
import { Play, Info, Star } from 'lucide-react';
import { useNavigate } from 'react-router';
import { MediaItem } from '../lib/mockData';

interface HeroBannerProps {
  items: MediaItem[];
}

export function HeroBanner({ items }: HeroBannerProps) {
  const navigate = useNavigate();

  const settings = {
    dots: true,
    infinite: true,
    speed: 900,
    slidesToShow: 1,
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: 7000,
    fade: true,
    arrows: false,
    pauseOnHover: false,
    dotsClass: 'slick-dots hero-dots',
    cssEase: 'cubic-bezier(0.2, 0, 0, 1)',
  };

  return (
    <>
      <style>{`
        .hero-dots {
          position: absolute;
          bottom: 32px;
          left: 50%;
          transform: translateX(-50%);
          display: flex !important;
          gap: 8px;
          list-style: none;
          padding: 0;
          margin: 0;
          z-index: 20;
        }
        .hero-dots li button {
          width: 6px;
          height: 6px;
          border-radius: 50%;
          background: rgba(245,242,235,0.3);
          border: none;
          padding: 0;
          cursor: pointer;
          transition: all 0.3s;
          font-size: 0;
        }
        .hero-dots li.slick-active button {
          width: 24px;
          border-radius: 3px;
          background: #C6A75E;
        }
        .slick-slide > div { height: 100%; }
        .slick-list, .slick-track { height: 100%; }
      `}</style>

      <div className="relative w-full overflow-hidden" style={{ height: 'clamp(420px, 72vh, 860px)' }}>
        <Slider {...settings} className="h-full">
          {items.map((item) => (
            <div key={item.id} style={{ height: 'clamp(420px, 72vh, 860px)' }} className="relative outline-none">
              {/* Background image with subtle Ken Burns */}
              <div
                className="absolute inset-0 bg-cover bg-center"
                style={{
                  backgroundImage: `url(${item.backdrop || item.poster})`,
                  willChange: 'transform',
                }}
              />

              {/* Cinematic gradient overlays */}
              <div className="absolute inset-0" style={{ background: 'linear-gradient(to top, #0B0F1A 0%, rgba(11,15,26,0.55) 50%, rgba(11,15,26,0.15) 100%)' }} />
              <div className="absolute inset-0" style={{ background: 'linear-gradient(to right, rgba(11,15,26,0.85) 0%, rgba(11,15,26,0.3) 55%, transparent 100%)' }} />

              {/* Content */}
              <div className="absolute inset-0 flex flex-col justify-end px-6 md:px-10 lg:px-14 pb-16 md:pb-20">
                <div className="max-w-2xl space-y-3 md:space-y-4">
                  <div className="flex items-center gap-2">
                    <span
                      className="px-2.5 py-0.5 rounded-full bg-[#C6A75E] text-[#0B0F1A] uppercase tracking-wider"
                      style={{ fontSize: '0.65rem', fontWeight: 700 }}
                    >
                      Featured
                    </span>
                    <span className="text-[#F5F2EB]/50 text-xs">{item.genre}</span>
                  </div>

                  <h1
                    className="text-[#F5F2EB]"
                    style={{
                      fontSize: 'clamp(2rem, 5vw, 3.5rem)',
                      fontWeight: 900,
                      lineHeight: 1.05,
                      letterSpacing: '-0.02em',
                      textShadow: '0 4px 20px rgba(0,0,0,0.6)',
                    }}
                  >
                    {item.title}
                  </h1>

                  <div className="flex items-center gap-3 text-sm text-[#F5F2EB]/70">
                    <span className="flex items-center gap-1 text-[#C6A75E]">
                      <Star className="w-3.5 h-3.5 fill-current" />
                      <span style={{ fontWeight: 600 }}>{item.rating}</span>
                    </span>
                    <span className="w-1 h-1 rounded-full bg-[#F5F2EB]/30" />
                    <span>{item.year}</span>
                    <span className="w-1 h-1 rounded-full bg-[#F5F2EB]/30" />
                    <span className="px-1.5 py-0.5 border border-[#F5F2EB]/20 rounded text-xs">{item.duration || '4K'}</span>
                  </div>

                  <p
                    className="text-[#F5F2EB]/75 hidden md:block"
                    style={{
                      fontSize: '0.95rem',
                      lineHeight: 1.65,
                      display: '-webkit-box',
                      WebkitLineClamp: 2,
                      WebkitBoxOrient: 'vertical',
                      overflow: 'hidden',
                      maxWidth: '520px',
                    }}
                  >
                    {item.description}
                  </p>

                  <div className="flex items-center gap-3 pt-1 md:pt-2">
                    <button
                      onClick={() => navigate(`/player/${item.id}`)}
                      className="flex items-center gap-2 px-6 md:px-8 py-3 bg-white text-[#0B0F1A] rounded-full hover:bg-[#C6A75E] active:scale-95 transition-all focus:outline-none focus:ring-2 focus:ring-[#C6A75E] focus:ring-offset-2 focus:ring-offset-transparent"
                      style={{ fontWeight: 700, fontSize: '0.9rem' }}
                    >
                      <Play className="w-4 h-4 fill-current" />
                      Watch Now
                    </button>
                    <button
                      className="flex items-center gap-2 px-6 md:px-8 py-3 bg-white/15 backdrop-blur-sm text-white border border-white/20 rounded-full hover:bg-white/25 active:scale-95 transition-all focus:outline-none focus:ring-2 focus:ring-white/40"
                      style={{ fontWeight: 600, fontSize: '0.9rem' }}
                    >
                      <Info className="w-4 h-4" />
                      More Info
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </Slider>
      </div>
    </>
  );
}

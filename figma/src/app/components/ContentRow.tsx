import Slider from 'react-slick';
import { MediaItem } from '../lib/mockData';
import { Link } from 'react-router';
import { Play, Star } from 'lucide-react';

interface ContentRowProps {
  title: string;
  items: MediaItem[];
  isPoster?: boolean;
}

export function ContentRow({ title, items, isPoster = true }: ContentRowProps) {
  const settings = {
    dots: false,
    infinite: false,
    speed: 400,
    slidesToShow: isPoster ? 6 : 4,
    slidesToScroll: isPoster ? 3 : 2,
    lazyLoad: 'ondemand' as const,
    cssEase: 'cubic-bezier(0.2, 0, 0, 1)',
    responsive: [
      { breakpoint: 1400, settings: { slidesToShow: isPoster ? 5 : 3 } },
      { breakpoint: 1024, settings: { slidesToShow: isPoster ? 4 : 3 } },
      { breakpoint: 768, settings: { slidesToShow: isPoster ? 3 : 2 } },
      { breakpoint: 480, settings: { slidesToShow: isPoster ? 2 : 1 } },
    ],
  };

  return (
    <div className="mb-10">
      {/* Section heading — M3 style */}
      <div className="flex items-center gap-3 mb-4 px-1">
        <span
          className="w-1 h-5 rounded-full bg-[#C6A75E] flex-shrink-0"
          style={{ boxShadow: '0 0 8px rgba(198,167,94,0.5)' }}
        />
        <h2 className="text-[#F5F2EB]" style={{ fontSize: '1.15rem', fontWeight: 700, letterSpacing: '-0.01em' }}>
          {title}
        </h2>
      </div>

      {/* Slick carousel */}
      <div className="px-1">
        <Slider {...settings} className="-mx-2">
          {items.map((item) => (
            <div key={item.id} className="px-2 py-3 outline-none">
              <Link
                to={`/player/${item.id}`}
                className="group block relative outline-none focus:ring-2 focus:ring-[#C6A75E]/70 focus:rounded-2xl"
              >
                {/* Card — M3 Elevated style */}
                <div
                  className={`relative overflow-hidden bg-[#141820] ${isPoster ? 'rounded-2xl aspect-[2/3]' : 'rounded-2xl aspect-video'}`}
                  style={{
                    boxShadow: '0 2px 8px rgba(0,0,0,0.5)',
                    transition: 'box-shadow 0.25s, transform 0.25s cubic-bezier(0.2, 0, 0, 1)',
                    willChange: 'transform',
                  }}
                  onMouseEnter={(e) => {
                    (e.currentTarget as HTMLElement).style.transform = 'translateY(-6px) scale(1.04)';
                    (e.currentTarget as HTMLElement).style.boxShadow = '0 16px 40px rgba(0,0,0,0.6), 0 0 0 1px rgba(198,167,94,0.2)';
                  }}
                  onMouseLeave={(e) => {
                    (e.currentTarget as HTMLElement).style.transform = 'translateY(0) scale(1)';
                    (e.currentTarget as HTMLElement).style.boxShadow = '0 2px 8px rgba(0,0,0,0.5)';
                  }}
                >
                  <img
                    src={isPoster ? item.poster : (item.backdrop || item.poster)}
                    alt={item.title}
                    className="w-full h-full object-cover"
                    loading="lazy"
                    decoding="async"
                  />

                  {/* M3 State layer on hover */}
                  <div className="absolute inset-0 bg-white/0 group-hover:bg-white/[0.04] transition-colors duration-200" />

                  {/* Info overlay */}
                  <div
                    className="absolute inset-0 flex flex-col justify-end p-3 opacity-0 group-hover:opacity-100 transition-opacity duration-250"
                    style={{ background: 'linear-gradient(to top, rgba(11,15,26,0.95) 0%, rgba(11,15,26,0.4) 55%, transparent 100%)' }}
                  >
                    <p className="text-[#F5F2EB] truncate" style={{ fontWeight: 700, fontSize: '0.8rem' }}>
                      {item.title}
                    </p>
                    <div className="flex items-center gap-1.5 mt-1">
                      <span
                        className="bg-[#C6A75E] text-[#0B0F1A] px-1.5 py-0.5 rounded"
                        style={{ fontSize: '0.6rem', fontWeight: 800 }}
                      >
                        HD
                      </span>
                      <span className="text-[#F5F2EB]/60" style={{ fontSize: '0.7rem' }}>
                        {item.year}
                      </span>
                      <span className="flex items-center gap-0.5 text-[#C6A75E]" style={{ fontSize: '0.7rem', fontWeight: 600 }}>
                        <Star className="w-2.5 h-2.5 fill-current" />
                        {item.rating}
                      </span>
                    </div>

                    {/* Play button */}
                    <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2">
                      <div className="w-10 h-10 rounded-full bg-[#C6A75E] text-[#0B0F1A] flex items-center justify-center shadow-lg scale-0 group-hover:scale-100 transition-transform duration-200">
                        <Play className="w-4 h-4 fill-current ml-0.5" />
                      </div>
                    </div>
                  </div>
                </div>

                {/* Title below card */}
                <p
                  className="mt-2 text-[#F5F2EB]/80 truncate group-hover:text-[#F5F2EB] transition-colors"
                  style={{ fontSize: '0.75rem', fontWeight: 500 }}
                >
                  {item.title}
                </p>
              </Link>
            </div>
          ))}
        </Slider>
      </div>
    </div>
  );
}

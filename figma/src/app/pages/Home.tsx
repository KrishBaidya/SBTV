import { heroItems, trendingItems } from '../lib/mockData';
import { HeroBanner } from '../components/HeroBanner';
import { ContentRow } from '../components/ContentRow';

export function Home() {
  return (
    <div className="pb-10">
      {/* Full-bleed hero — no horizontal padding */}
      <HeroBanner items={heroItems} />

      {/* Content rows — overlaps hero slightly for continuity */}
      <div className="relative z-10 -mt-6 px-4 md:px-8 lg:px-12 space-y-2">
        <ContentRow title="Trending Now" items={trendingItems} isPoster />
        <ContentRow title="Recently Added" items={[...trendingItems].reverse()} isPoster />
        <ContentRow title="Top Rated" items={trendingItems.slice(2).concat(trendingItems.slice(0, 2))} isPoster />
        <ContentRow title="Recommended For You" items={trendingItems} isPoster />
      </div>
    </div>
  );
}

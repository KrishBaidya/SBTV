import React from 'react';
import { Box } from '@mui/material';
import { Hero } from './Hero';
import { ContentRow } from './ContentRow';

// Mock Data
const TRENDING = [
  { id: 't1', title: 'Inception', image: 'https://images.unsplash.com/photo-1596727147705-0608c643e34b?auto=format&fit=crop&q=80&w=300&h=450' },
  { id: 't2', title: 'The Matrix', image: 'https://images.unsplash.com/photo-1626814026160-2237a95fc5a0?auto=format&fit=crop&q=80&w=300&h=450' },
  { id: 't3', title: 'Interstellar', image: 'https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&q=80&w=300&h=450' },
  { id: 't4', title: 'Blade Runner 2049', image: 'https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&q=80&w=300&h=450' },
  { id: 't5', title: 'Dune', image: 'https://images.unsplash.com/photo-1541963463532-d68292c34b19?auto=format&fit=crop&q=80&w=300&h=450' },
  { id: 't6', title: 'Tenet', image: 'https://images.unsplash.com/photo-1478720568477-152d9b164e63?auto=format&fit=crop&q=80&w=300&h=450' },
];

const RECENT = [
  { id: 'r1', title: 'Avengers', image: 'https://images.unsplash.com/photo-1608889476561-6242cfdbf622?auto=format&fit=crop&q=80&w=300&h=450' },
  { id: 'r2', title: 'Spiderman', image: 'https://images.unsplash.com/photo-1635805737707-575885ab0820?auto=format&fit=crop&q=80&w=300&h=450' },
  { id: 'r3', title: 'Iron Man', image: 'https://images.unsplash.com/photo-1623984175510-d754522d32bc?auto=format&fit=crop&q=80&w=300&h=450' },
  { id: 'r4', title: 'Thor', image: 'https://images.unsplash.com/photo-1612036782180-6f0b6cd846fe?auto=format&fit=crop&q=80&w=300&h=450' },
];

const TV_CHANNELS = [
  { id: 'tv1', title: 'News 24', image: 'https://images.unsplash.com/photo-1585829365295-ab7cd400c167?auto=format&fit=crop&q=80&w=400&h=250', category: 'News' },
  { id: 'tv2', title: 'Sports Live', image: 'https://images.unsplash.com/photo-1540747913346-19e32dc3e97e?auto=format&fit=crop&q=80&w=400&h=250', category: 'Sports' },
  { id: 'tv3', title: 'Kids Cartoon', image: 'https://images.unsplash.com/photo-1560169897-fc0cdbdfa4d5?auto=format&fit=crop&q=80&w=400&h=250', category: 'Kids' },
  { id: 'tv4', title: 'Wildlife Doc', image: 'https://images.unsplash.com/photo-1535930749574-1399327ce78f?auto=format&fit=crop&q=80&w=400&h=250', category: 'Documentary' },
];

export function Home() {
  return (
    <Box sx={{ pb: 10 }}>
      <Hero />
      <Box sx={{ mt: -10, position: 'relative', zIndex: 10 }}>
        <ContentRow title="Trending Now" items={TRENDING} />
        <ContentRow title="Recently Added" items={RECENT} />
        <ContentRow title="Live TV Channels" items={TV_CHANNELS} isPoster={false} />
      </Box>
    </Box>
  );
}

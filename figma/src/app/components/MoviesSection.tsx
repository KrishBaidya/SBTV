import React, { useState } from 'react';
import { Box, Chip, Typography, Grid } from '@mui/material';
import { motion } from 'motion/react';
import { useNavigate } from 'react-router';

const GENRES = ['Action', 'Drama', 'Comedy', 'Thriller', 'Romance', 'Sci-Fi', 'Horror', 'Documentary'];

const MOVIES = [
  { id: 'm1', title: 'The Dark Knight', year: '2008', genre: 'Action', image: 'https://images.unsplash.com/photo-1509347528160-9a9e33742cd4?q=80&w=600&auto=format&fit=crop' },
  { id: 'm2', title: 'Inception', year: '2010', genre: 'Sci-Fi', image: 'https://images.unsplash.com/photo-1596727147705-0608c643e34b?q=80&w=600&auto=format&fit=crop' },
  { id: 'm3', title: 'Interstellar', year: '2014', genre: 'Sci-Fi', image: 'https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?q=80&w=600&auto=format&fit=crop' },
  { id: 'm4', title: 'Parasite', year: '2019', genre: 'Thriller', image: 'https://images.unsplash.com/photo-1536440136628-849c177e76a1?q=80&w=600&auto=format&fit=crop' },
  { id: 'm5', title: 'Joker', year: '2019', genre: 'Drama', image: 'https://images.unsplash.com/photo-1616530940355-351fabd9524b?q=80&w=600&auto=format&fit=crop' },
  { id: 'm6', title: 'Avengers: Endgame', year: '2019', genre: 'Action', image: 'https://images.unsplash.com/photo-1608889476561-6242cfdbf622?q=80&w=600&auto=format&fit=crop' },
  { id: 'm7', title: 'Coco', year: '2017', genre: 'Animation', image: 'https://images.unsplash.com/photo-1584844626127-14288f62c5b3?q=80&w=600&auto=format&fit=crop' },
  { id: 'm8', title: 'Dune', year: '2021', genre: 'Sci-Fi', image: 'https://images.unsplash.com/photo-1541963463532-d68292c34b19?q=80&w=600&auto=format&fit=crop' },
];

export function Movies() {
  const [selectedGenre, setSelectedGenre] = useState('All');
  const navigate = useNavigate();

  const filteredMovies = selectedGenre === 'All' 
    ? MOVIES 
    : MOVIES.filter(m => m.genre === selectedGenre);

  return (
    <Box sx={{ p: 4, pt: 6 }}>
      <Typography variant="h3" sx={{ fontWeight: 800, mb: 4, color: 'text.primary' }}>Movies</Typography>

      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1.5, mb: 4 }}>
        <Chip
          label="All"
          onClick={() => setSelectedGenre('All')}
          sx={{
            bgcolor: selectedGenre === 'All' ? 'primary.main' : 'rgba(255,255,255,0.05)',
            color: selectedGenre === 'All' ? 'black' : 'text.primary',
            fontWeight: selectedGenre === 'All' ? 'bold' : 'normal',
            fontSize: '1rem',
            px: 2,
            py: 2.5,
            borderRadius: 3
          }}
        />
        {GENRES.map((genre) => (
          <Chip
            key={genre}
            label={genre}
            onClick={() => setSelectedGenre(genre)}
            sx={{
              bgcolor: selectedGenre === genre ? 'primary.main' : 'rgba(255,255,255,0.05)',
              color: selectedGenre === genre ? 'black' : 'text.primary',
              fontWeight: selectedGenre === genre ? 'bold' : 'normal',
              fontSize: '1rem',
              px: 2,
              py: 2.5,
              borderRadius: 3,
              '&:hover': { bgcolor: selectedGenre === genre ? 'primary.dark' : 'rgba(255,255,255,0.1)' }
            }}
          />
        ))}
      </Box>

      <Grid container spacing={3}>
        {filteredMovies.map((movie) => (
          <Grid item xs={6} sm={4} md={3} xl={2} key={movie.id}>
            <motion.div
              whileHover={{ scale: 1.05, y: -5 }}
              transition={{ type: 'spring', stiffness: 300, damping: 20 }}
              onClick={() => navigate(`/player/${movie.id}`)}
              style={{ cursor: 'pointer' }}
            >
              <Box sx={{ position: 'relative', borderRadius: 3, overflow: 'hidden', aspectRatio: '2/3', boxShadow: '0 8px 20px rgba(0,0,0,0.4)' }}>
                <img
                  src={movie.image}
                  alt={movie.title}
                  style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                />
                <Box
                  className="hover-overlay"
                  sx={{
                    position: 'absolute',
                    inset: 0,
                    background: 'linear-gradient(to top, rgba(0,0,0,0.9) 0%, transparent 60%)',
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'flex-end',
                    p: 2,
                    opacity: 0,
                    transition: 'opacity 0.2s',
                    '&:hover': { opacity: 1 }
                  }}
                >
                  <Typography variant="h6" sx={{ fontWeight: 700, lineHeight: 1.2 }}>{movie.title}</Typography>
                  <Typography variant="body2" sx={{ color: 'primary.main', fontWeight: 500 }}>{movie.year}</Typography>
                </Box>
              </Box>
            </motion.div>
          </Grid>
        ))}
      </Grid>
      
      <style>{`
        .hover-overlay:hover {
          opacity: 1 !important;
        }
      `}</style>
    </Box>
  );
}

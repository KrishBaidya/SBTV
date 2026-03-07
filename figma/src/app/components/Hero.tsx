import React from 'react';
import Slider from 'react-slick';
import { Box, Typography, Button, Container } from '@mui/material';
import { Play, Info } from 'lucide-react';
import { useNavigate } from 'react-router';

// Sample Hero Data
const HERO_ITEMS = [
  {
    id: 'hero1',
    title: 'Interstellar Legacy',
    description: 'A team of explorers travel through a wormhole in space in an attempt to ensure humanity\'s survival.',
    image: 'https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=2694&auto=format&fit=crop',
    genre: 'Sci-Fi • Adventure',
  },
  {
    id: 'hero2',
    title: 'The Dark Knight Rises',
    description: 'Eight years after the Joker\'s reign of anarchy, Batman, with the help of the enigmatic Catwoman, is forced from his exile to save Gotham City.',
    image: 'https://images.unsplash.com/photo-1509347528160-9a9e33742cd4?q=80&w=2670&auto=format&fit=crop',
    genre: 'Action • Drama',
  },
  {
    id: 'hero3',
    title: 'Neon Dreams',
    description: 'In a future city, a lone hacker uncovers a conspiracy that threatens to tear reality apart.',
    image: 'https://images.unsplash.com/photo-1626814026160-2237a95fc5a0?q=80&w=2670&auto=format&fit=crop',
    genre: 'Cyberpunk • Thriller',
  }
];

export function Hero() {
  const navigate = useNavigate();

  const settings = {
    dots: true,
    infinite: true,
    speed: 1000,
    slidesToShow: 1,
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: 5000,
    arrows: false,
    fade: true,
  };

  return (
    <Box sx={{ 
      position: 'relative', 
      width: '100%', 
      height: '70vh', 
      overflow: 'hidden',
      // Override slick dots
      '& .slick-dots': {
        bottom: 24,
        textAlign: 'right',
        paddingRight: 8,
        '& li button:before': {
          color: 'white',
          opacity: 0.5,
          fontSize: 12
        },
        '& li.slick-active button:before': {
          color: '#C6A75E',
          opacity: 1
        }
      }
    }}>
      <Slider {...settings}>
        {HERO_ITEMS.map((item) => (
          <Box key={item.id} sx={{ position: 'relative', height: '70vh', width: '100%' }}>
            <Box
              sx={{
                position: 'absolute',
                top: 0,
                left: 0,
                width: '100%',
                height: '100%',
                backgroundImage: `url(${item.image})`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
              }}
            />
            {/* Gradient Overlay */}
            <Box
              sx={{
                position: 'absolute',
                top: 0,
                left: 0,
                width: '100%',
                height: '100%',
                background: 'linear-gradient(to top, #0B0F1A 10%, transparent 60%), linear-gradient(to right, #0B0F1A 0%, transparent 50%)',
              }}
            />

            <Container maxWidth="xl" sx={{ height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'center', position: 'relative', zIndex: 2, pl: { xs: 2, md: 8 } }}>
              <Box sx={{ maxWidth: 600 }}>
                <Typography variant="overline" sx={{ color: 'primary.main', fontWeight: 700, letterSpacing: 2 }}>
                  {item.genre}
                </Typography>
                <Typography variant="h2" sx={{ fontWeight: 800, mb: 2, color: 'white', textShadow: '0 4px 10px rgba(0,0,0,0.5)' }}>
                  {item.title}
                </Typography>
                <Typography variant="body1" sx={{ mb: 4, color: 'rgba(255,255,255,0.8)', fontSize: '1.1rem', lineHeight: 1.6 }}>
                  {item.description}
                </Typography>
                <Box sx={{ display: 'flex', gap: 2 }}>
                  <Button
                    variant="contained"
                    color="primary"
                    startIcon={<Play fill="black" size={20} />}
                    onClick={() => navigate(`/player/${item.id}`)}
                    sx={{ px: 4, py: 1.5, fontSize: '1rem', color: 'black', fontWeight: 'bold' }}
                  >
                    Watch Now
                  </Button>
                  <Button
                    variant="outlined"
                    startIcon={<Info size={20} />}
                    sx={{ px: 4, py: 1.5, fontSize: '1rem', color: 'white', borderColor: 'rgba(255,255,255,0.3)', backdropFilter: 'blur(10px)', '&:hover': { borderColor: 'white', bgcolor: 'rgba(255,255,255,0.1)' } }}
                  >
                    More Info
                  </Button>
                </Box>
              </Box>
            </Container>
          </Box>
        ))}
      </Slider>
    </Box>
  );
}

import React, { useEffect } from 'react';
import { Box, Typography } from '@mui/material';
import { motion } from 'motion/react';
import { useNavigate } from 'react-router';

export function Splash() {
  const navigate = useNavigate();

  useEffect(() => {
    const timer = setTimeout(() => {
      navigate('/login');
    }, 3000);
    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <Box
      sx={{
        height: '100vh',
        width: '100vw',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'radial-gradient(circle at center, #1a2035 0%, #0B0F1A 100%)',
        flexDirection: 'column',
      }}
    >
      <motion.div
        initial={{ opacity: 0, scale: 0.8 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 1.2, ease: "easeOut" }}
      >
        <Typography
          variant="h1"
          sx={{
            color: 'primary.main',
            textShadow: '0 0 30px rgba(198,167,94,0.6)',
            textAlign: 'center',
            letterSpacing: '-0.05em',
          }}
        >
          SB TV
        </Typography>
      </motion.div>

      <motion.div
        initial={{ width: 0 }}
        animate={{ width: 200 }}
        transition={{ delay: 0.5, duration: 1 }}
        style={{
          height: 2,
          background: 'linear-gradient(90deg, transparent, #C6A75E, transparent)',
          marginTop: 20,
        }}
      />
      
      {/* Particle effects could be added here with canvas or more divs */}
    </Box>
  );
}

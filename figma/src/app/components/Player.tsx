import React, { useState, useEffect } from 'react';
import { Box, IconButton, Typography, Slider } from '@mui/material';
import { ArrowLeft, Pause, Play, SkipForward, SkipBack, Volume2, Settings as SettingsIcon, Maximize } from 'lucide-react';
import { useNavigate, useParams } from 'react-router';
import { motion, AnimatePresence } from 'motion/react';

export function Player() {
  const navigate = useNavigate();
  const { id } = useParams();
  const [playing, setPlaying] = useState(true);
  const [progress, setProgress] = useState(30);
  const [showControls, setShowControls] = useState(true);
  let inactivityTimer: NodeJS.Timeout;

  const resetTimer = () => {
    setShowControls(true);
    clearTimeout(inactivityTimer);
    inactivityTimer = setTimeout(() => setShowControls(false), 3000);
  };

  useEffect(() => {
    window.addEventListener('mousemove', resetTimer);
    resetTimer();
    return () => {
      window.removeEventListener('mousemove', resetTimer);
      clearTimeout(inactivityTimer);
    };
  }, []);

  return (
    <Box 
      sx={{ width: '100vw', height: '100vh', bgcolor: 'black', position: 'relative', overflow: 'hidden', cursor: showControls ? 'default' : 'none' }}
    >
      {/* Video Placeholder */}
      <Box
        component="img"
        src="https://images.unsplash.com/photo-1478720568477-152d9b164e63?q=80&w=2000&auto=format&fit=crop"
        alt="Video Stream"
        sx={{ width: '100%', height: '100%', objectFit: 'cover', opacity: 0.6 }}
      />
      
      <AnimatePresence>
        {showControls && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            style={{ position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}
          >
            {/* Top Bar */}
            <Box sx={{ p: 4, background: 'linear-gradient(to bottom, rgba(0,0,0,0.8), transparent)' }}>
               <IconButton onClick={() => navigate(-1)} sx={{ color: 'white' }}>
                 <ArrowLeft size={32} />
               </IconButton>
            </Box>

            {/* Center Play Button */}
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
               <IconButton 
                 onClick={() => setPlaying(!playing)}
                 sx={{ 
                   width: 80, 
                   height: 80, 
                   bgcolor: 'rgba(0,0,0,0.5)', 
                   color: 'white',
                   '&:hover': { bgcolor: 'primary.main', color: 'black' } 
                 }}
               >
                 {playing ? <Pause size={40} /> : <Play size={40} fill="currentColor" />}
               </IconButton>
            </Box>

            {/* Bottom Controls */}
            <Box sx={{ p: 4, background: 'linear-gradient(to top, rgba(0,0,0,0.9), transparent)' }}>
               <Typography variant="h5" sx={{ color: 'white', fontWeight: 700, mb: 1 }}>Movie Title</Typography>
               <Typography variant="body2" sx={{ color: 'rgba(255,255,255,0.7)', mb: 2 }}>Season 1 • Episode 1</Typography>
               
               <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                 <Typography variant="caption" sx={{ color: 'white' }}>12:30</Typography>
                 <Slider 
                   value={progress} 
                   onChange={(_, v) => setProgress(v as number)} 
                   sx={{ 
                     color: 'primary.main',
                     '& .MuiSlider-thumb': {
                        width: 16,
                        height: 16,
                        transition: '0.3s cubic-bezier(.47,1.64,.41,.8)',
                        '&:before': {
                          boxShadow: '0 2px 12px 0 rgba(0,0,0,0.4)',
                        },
                        '&:hover, &.Mui-focusVisible': {
                          boxShadow: '0px 0px 0px 8px rgb(198 167 94 / 16%)',
                        },
                        '&.Mui-active': {
                          width: 20,
                          height: 20,
                        },
                     },
                   }} 
                 />
                 <Typography variant="caption" sx={{ color: 'white' }}>45:00</Typography>
               </Box>

               <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                 <Box sx={{ display: 'flex', gap: 2 }}>
                   <IconButton sx={{ color: 'white' }}><Volume2 /></IconButton>
                   <IconButton sx={{ color: 'white' }}><SkipBack /></IconButton>
                   <IconButton sx={{ color: 'white' }}><SkipForward /></IconButton>
                 </Box>
                 <Box sx={{ display: 'flex', gap: 2 }}>
                    <IconButton sx={{ color: 'white' }}><SettingsIcon /></IconButton>
                    <IconButton sx={{ color: 'white' }}><Maximize /></IconButton>
                 </Box>
               </Box>
            </Box>
          </motion.div>
        )}
      </AnimatePresence>
    </Box>
  );
}

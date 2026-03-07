import React, { useState } from 'react';
import { Box, Paper, Typography, TextField, Button, Grid, InputAdornment } from '@mui/material';
import { useNavigate } from 'react-router';
import { motion } from 'motion/react';
import { Tv, User, Lock, Link as LinkIcon } from 'lucide-react';

export function Login() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    // Simulate API call
    setTimeout(() => {
      setLoading(false);
      navigate('/app');
    }, 1500);
  };

  return (
    <Box
      sx={{
        height: '100vh',
        width: '100vw',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: `linear-gradient(rgba(11,15,26,0.8), rgba(11,15,26,0.9)), url('https://images.unsplash.com/photo-1574375927938-d5a98e8ffe85?q=80&w=2669&auto=format&fit=crop')`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
      }}
    >
      <motion.div
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8 }}
      >
        <Paper
          elevation={24}
          sx={{
            p: 6,
            width: '100%',
            maxWidth: 500,
            borderRadius: 4,
            bgcolor: 'rgba(18, 18, 18, 0.85)',
            backdropFilter: 'blur(20px)',
            border: '1px solid rgba(255,255,255,0.08)',
          }}
        >
          <Box sx={{ mb: 4, textAlign: 'center' }}>
             <Typography variant="h3" color="primary" sx={{ fontWeight: 800, mb: 1, textShadow: '0 0 20px rgba(198,167,94,0.4)' }}>
               SB TV
             </Typography>
             <Typography variant="body1" color="text.secondary">
               Connect your playlist to start streaming
             </Typography>
          </Box>

          <form onSubmit={handleLogin}>
            <Grid container spacing={3}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Playlist Name"
                  variant="filled"
                  placeholder="My TV"
                  InputProps={{
                    startAdornment: <InputAdornment position="start"><Tv size={20} color="#C6A75E" /></InputAdornment>,
                    sx: { borderRadius: 2 }
                  }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="M3U URL / Server URL"
                  variant="filled"
                  placeholder="http://example.com:8080"
                  InputProps={{
                    startAdornment: <InputAdornment position="start"><LinkIcon size={20} color="#C6A75E" /></InputAdornment>,
                    sx: { borderRadius: 2 }
                  }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Username"
                  variant="filled"
                  InputProps={{
                    startAdornment: <InputAdornment position="start"><User size={20} color="#C6A75E" /></InputAdornment>,
                    sx: { borderRadius: 2 }
                  }}
                />
              </Grid>
               <Grid item xs={12}>
                <TextField
                  fullWidth
                  type="password"
                  label="Password"
                  variant="filled"
                  InputProps={{
                    startAdornment: <InputAdornment position="start"><Lock size={20} color="#C6A75E" /></InputAdornment>,
                    sx: { borderRadius: 2 }
                  }}
                />
              </Grid>

              <Grid item xs={12}>
                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  size="large"
                  disabled={loading}
                  sx={{
                    mt: 2,
                    py: 2,
                    fontSize: '1.1rem',
                    background: 'linear-gradient(45deg, #C6A75E 30%, #E5Cca0 90%)',
                    color: '#0B0F1A',
                    fontWeight: 'bold'
                  }}
                >
                  {loading ? 'Connecting...' : 'Add Playlist'}
                </Button>
              </Grid>
            </Grid>
          </form>
        </Paper>
      </motion.div>
    </Box>
  );
}
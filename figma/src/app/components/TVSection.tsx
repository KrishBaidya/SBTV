import React, { useState } from 'react';
import { Box, Grid, Typography, Paper, List, ListItemButton, ListItemText, Chip, IconButton } from '@mui/material';
import { Play, Maximize2, Volume2, Info, ChevronRight } from 'lucide-react';

const CATEGORIES = ['All Channels', 'Sports', 'News', 'Entertainment', 'Kids', 'Regional', 'International'];

const CHANNELS = [
  { id: 1, name: 'CNN International', category: 'News', program: 'World News Now', next: 'Business Daily', image: 'https://images.unsplash.com/photo-1504711434969-e33886168f5c?q=80&w=1000&auto=format&fit=crop' },
  { id: 2, name: 'ESPN Sports', category: 'Sports', program: 'Live: NBA Finals', next: 'Sports Center', image: 'https://images.unsplash.com/photo-1504450758481-7338eba7524a?q=80&w=1000&auto=format&fit=crop' },
  { id: 3, name: 'BBC One', category: 'Entertainment', program: 'Doctor Who', next: 'The Graham Norton Show', image: 'https://images.unsplash.com/photo-1522869635100-1f4d061dd70d?q=80&w=1000&auto=format&fit=crop' },
  { id: 4, name: 'Cartoon Network', category: 'Kids', program: 'Tom & Jerry', next: 'Ben 10', image: 'https://images.unsplash.com/photo-1535905557558-afc4877a26fc?q=80&w=1000&auto=format&fit=crop' },
  { id: 5, name: 'Nat Geo', category: 'Entertainment', program: 'Planet Earth', next: 'Cosmos', image: 'https://images.unsplash.com/photo-1504609773096-104ff2c73ba4?q=80&w=1000&auto=format&fit=crop' },
  { id: 6, name: 'Sky Sports', category: 'Sports', program: 'Premier League Live', next: 'F1 Highlights', image: 'https://images.unsplash.com/photo-1517466787929-bc90951d6db0?q=80&w=1000&auto=format&fit=crop' },
];

export function TV() {
  const [selectedCategory, setSelectedCategory] = useState('All Channels');
  const [activeChannel, setActiveChannel] = useState(CHANNELS[0]);

  const filteredChannels = selectedCategory === 'All Channels' 
    ? CHANNELS 
    : CHANNELS.filter(c => c.category === selectedCategory);

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', height: '100vh', pt: 2, px: 4, pb: 4, overflow: 'hidden' }}>
      
      {/* Category Bar */}
      <Box sx={{ display: 'flex', gap: 2, mb: 3, overflowX: 'auto', pb: 1, scrollbarWidth: 'none' }}>
        {CATEGORIES.map((cat) => (
          <Chip
            key={cat}
            label={cat}
            clickable
            onClick={() => setSelectedCategory(cat)}
            sx={{
              bgcolor: selectedCategory === cat ? 'primary.main' : 'rgba(255,255,255,0.05)',
              color: selectedCategory === cat ? 'black' : 'text.primary',
              fontWeight: selectedCategory === cat ? 'bold' : 'normal',
              border: '1px solid',
              borderColor: selectedCategory === cat ? 'primary.main' : 'rgba(255,255,255,0.1)',
              '&:hover': {
                 bgcolor: selectedCategory === cat ? 'primary.dark' : 'rgba(255,255,255,0.1)',
              }
            }}
          />
        ))}
      </Box>

      <Grid container spacing={4} sx={{ flexGrow: 1, overflow: 'hidden' }}>
        
        {/* Channel List */}
        <Grid item xs={12} md={4} lg={3} sx={{ height: '100%', overflowY: 'auto', pr: 2 }}>
          <List>
            {filteredChannels.map((channel) => (
              <ListItemButton
                key={channel.id}
                selected={activeChannel.id === channel.id}
                onClick={() => setActiveChannel(channel)}
                sx={{
                  mb: 1,
                  borderRadius: 2,
                  bgcolor: activeChannel.id === channel.id ? 'rgba(198,167,94,0.15)' : 'transparent',
                  borderLeft: activeChannel.id === channel.id ? '4px solid #C6A75E' : '4px solid transparent',
                  '&.Mui-selected': {
                    bgcolor: 'rgba(198,167,94,0.15)',
                    '&:hover': { bgcolor: 'rgba(198,167,94,0.25)' }
                  }
                }}
              >
                <ListItemText
                  primary={channel.name}
                  secondary={channel.program}
                  primaryTypographyProps={{ fontWeight: 600, color: activeChannel.id === channel.id ? 'primary.main' : 'text.primary' }}
                  secondaryTypographyProps={{ sx: { opacity: 0.7, fontSize: '0.8rem' } }}
                />
                {activeChannel.id === channel.id && <Play size={16} fill="#C6A75E" color="#C6A75E" />}
              </ListItemButton>
            ))}
          </List>
        </Grid>

        {/* Live Preview Area */}
        <Grid item xs={12} md={8} lg={9} sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
          <Box sx={{ 
            position: 'relative', 
            width: '100%', 
            aspectRatio: '16/9', 
            bgcolor: 'black', 
            borderRadius: 3, 
            overflow: 'hidden',
            boxShadow: '0 20px 50px rgba(0,0,0,0.5)',
            mb: 3
          }}>
            <Box
              component="img"
              src={activeChannel.image}
              alt="Live"
              sx={{ width: '100%', height: '100%', objectFit: 'cover', opacity: 0.8 }}
            />
            {/* Overlay */}
            <Box sx={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <IconButton 
                sx={{ 
                  width: 80, 
                  height: 80, 
                  bgcolor: 'rgba(198,167,94,0.9)', 
                  '&:hover': { bgcolor: '#C6A75E', transform: 'scale(1.1)' },
                  transition: 'all 0.2s'
                }}
              >
                <Play size={40} fill="black" color="black" style={{ marginLeft: 4 }} />
              </IconButton>
            </Box>
            
            <Box sx={{ position: 'absolute', bottom: 20, right: 20, display: 'flex', gap: 2 }}>
               <IconButton sx={{ bgcolor: 'rgba(0,0,0,0.5)', color: 'white' }}><Volume2 /></IconButton>
               <IconButton sx={{ bgcolor: 'rgba(0,0,0,0.5)', color: 'white' }}><Maximize2 /></IconButton>
            </Box>

            <Box sx={{ position: 'absolute', top: 20, left: 20 }}>
               <Chip label="LIVE" color="error" size="small" sx={{ fontWeight: 'bold' }} />
            </Box>
          </Box>

          <Box sx={{ p: 2 }}>
            <Typography variant="h4" sx={{ fontWeight: 800, mb: 1 }}>{activeChannel.name}</Typography>
            <Typography variant="h6" color="primary" sx={{ mb: 2 }}>Now Playing: {activeChannel.program}</Typography>
            <Paper sx={{ p: 2, bgcolor: 'rgba(255,255,255,0.03)', borderRadius: 2 }}>
               <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Typography variant="body2" color="text.secondary">Up Next</Typography>
                  <Box sx={{ display: 'flex', alignItems: 'center', color: 'text.secondary' }}>
                     <Typography variant="caption" sx={{ mr: 1 }}>Full Schedule</Typography>
                     <ChevronRight size={16} />
                  </Box>
               </Box>
               <Typography variant="subtitle1" sx={{ mt: 1, fontWeight: 600 }}>{activeChannel.next}</Typography>
               <Typography variant="caption" color="text.secondary">10:30 PM - 11:30 PM</Typography>
            </Paper>
          </Box>
        </Grid>
      </Grid>
    </Box>
  );
}

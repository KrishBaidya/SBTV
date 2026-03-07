import React from 'react';
import { Outlet, NavLink, useNavigate, useLocation } from 'react-router';
import { Box, Drawer, List, ListItem, ListItemButton, ListItemIcon, ListItemText, Typography, Avatar, useMediaQuery, useTheme } from '@mui/material';
import { Home, Tv, Movie, VideoLibrary, Search, Settings, LogOut } from 'lucide-react';
import { motion } from 'motion/react';

const DRAWER_WIDTH = 240;

const MENU_ITEMS = [
  { text: 'Home', icon: Home, path: '/app' },
  { text: 'TV', icon: Tv, path: '/app/tv' },
  { text: 'Movies', icon: Movie, path: '/app/movies' },
  { text: 'Series', icon: VideoLibrary, path: '/app/series' },
  // { text: 'Search', icon: Search, path: '/app/search' }, // Placeholder for now
  { text: 'Settings', icon: Settings, path: '/app/settings' },
];

export function Layout() {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    navigate('/login');
  };

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: 'background.default' }}>
      {!isMobile && (
        <Drawer
          variant="permanent"
          sx={{
            width: DRAWER_WIDTH,
            flexShrink: 0,
            '& .MuiDrawer-paper': {
              width: DRAWER_WIDTH,
              boxSizing: 'border-box',
              borderRight: '1px solid rgba(255,255,255,0.05)',
              background: 'linear-gradient(180deg, #0F121D 0%, #0B0F1A 100%)',
            },
          }}
        >
          <Box sx={{ p: 4, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Typography variant="h4" sx={{ 
              fontWeight: 800, 
              color: 'primary.main',
              letterSpacing: '-0.05em',
              textShadow: '0 0 20px rgba(198,167,94,0.3)'
            }}>
              SB TV
            </Typography>
          </Box>

          <List sx={{ px: 2 }}>
            {MENU_ITEMS.map((item) => {
              const isActive = location.pathname === item.path;
              return (
                <ListItem key={item.text} disablePadding sx={{ mb: 1 }}>
                  <ListItemButton
                    component={NavLink}
                    to={item.path}
                    sx={{
                      borderRadius: 2,
                      py: 1.5,
                      color: isActive ? 'primary.main' : 'text.secondary',
                      bgcolor: isActive ? 'rgba(198,167,94,0.1)' : 'transparent',
                      '&:hover': {
                        bgcolor: 'rgba(255,255,255,0.05)',
                        color: 'text.primary',
                      },
                    }}
                  >
                    <ListItemIcon sx={{ 
                      minWidth: 40, 
                      color: 'inherit'
                    }}>
                      <item.icon size={24} />
                    </ListItemIcon>
                    <ListItemText 
                      primary={item.text} 
                      primaryTypographyProps={{ 
                        fontWeight: isActive ? 700 : 500,
                        fontSize: '1rem'
                      }} 
                    />
                    {isActive && (
                      <motion.div
                        layoutId="activeIndicator"
                        style={{
                          width: 4,
                          height: '100%',
                          backgroundColor: '#C6A75E',
                          position: 'absolute',
                          left: 0,
                          top: 0,
                          borderRadius: '0 4px 4px 0',
                        }}
                      />
                    )}
                  </ListItemButton>
                </ListItem>
              );
            })}
          </List>

          <Box sx={{ mt: 'auto', p: 2 }}>
             <ListItemButton onClick={handleLogout} sx={{ borderRadius: 2, color: 'text.secondary' }}>
                <ListItemIcon sx={{ minWidth: 40, color: 'inherit' }}>
                  <LogOut size={24} />
                </ListItemIcon>
                <ListItemText primary="Logout" />
             </ListItemButton>
          </Box>
        </Drawer>
      )}

      {/* Mobile Bottom Nav would go here, but for now focusing on desktop/TV structure */}
      
      <Box component="main" sx={{ flexGrow: 1, p: 0, overflowX: 'hidden' }}>
         <Outlet />
      </Box>
      
      {isMobile && (
        <Box sx={{ 
          position: 'fixed', 
          bottom: 0, 
          left: 0, 
          right: 0, 
          bgcolor: '#121212', 
          borderTop: '1px solid rgba(255,255,255,0.1)',
          display: 'flex',
          justifyContent: 'space-around',
          p: 1,
          zIndex: 1000
        }}>
           {MENU_ITEMS.map((item) => {
             const isActive = location.pathname === item.path;
             return (
               <Box 
                 key={item.text} 
                 onClick={() => navigate(item.path)}
                 sx={{ 
                   display: 'flex', 
                   flexDirection: 'column', 
                   alignItems: 'center',
                   color: isActive ? 'primary.main' : 'text.secondary',
                   p: 1
                 }}
               >
                 <item.icon size={24} />
                 <Typography variant="caption" sx={{ mt: 0.5 }}>{item.text}</Typography>
               </Box>
             )
           })}
        </Box>
      )}
    </Box>
  );
}

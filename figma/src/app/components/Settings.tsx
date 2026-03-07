import React from 'react';
import { Box, Typography, List, ListItem, ListItemButton, ListItemIcon, ListItemText, Switch, Divider, Paper, Grid } from '@mui/material';
import { User, List as ListIcon, Sliders, Wifi, Lock, Moon, Info, LogOut } from 'lucide-react';

export function Settings() {
  return (
    <Box sx={{ p: 6, maxWidth: 1000, margin: '0 auto' }}>
      <Typography variant="h3" sx={{ fontWeight: 800, mb: 4 }}>Settings</Typography>

      <Grid container spacing={4}>
        <Grid item xs={12} md={4}>
           <Paper sx={{ bgcolor: 'background.paper', borderRadius: 4, overflow: 'hidden' }}>
             <List>
               <ListItemButton selected>
                 <ListItemIcon><User size={20} /></ListItemIcon>
                 <ListItemText primary="Account" />
               </ListItemButton>
               <ListItemButton>
                 <ListItemIcon><ListIcon size={20} /></ListItemIcon>
                 <ListItemText primary="Playlists" />
               </ListItemButton>
               <ListItemButton>
                 <ListItemIcon><Sliders size={20} /></ListItemIcon>
                 <ListItemText primary="Player" />
               </ListItemButton>
               <ListItemButton>
                 <ListItemIcon><Lock size={20} /></ListItemIcon>
                 <ListItemText primary="Parental Control" />
               </ListItemButton>
               <ListItemButton>
                 <ListItemIcon><Info size={20} /></ListItemIcon>
                 <ListItemText primary="About" />
               </ListItemButton>
             </List>
           </Paper>
        </Grid>

        <Grid item xs={12} md={8}>
           <Paper sx={{ p: 4, bgcolor: 'background.paper', borderRadius: 4 }}>
              <Typography variant="h5" sx={{ fontWeight: 700, mb: 3 }}>Account Information</Typography>
              
              <Box sx={{ mb: 3 }}>
                <Typography variant="body2" color="text.secondary">Username</Typography>
                <Typography variant="h6">demo_user</Typography>
              </Box>
              <Box sx={{ mb: 3 }}>
                <Typography variant="body2" color="text.secondary">Subscription</Typography>
                <Typography variant="h6" color="primary">Premium Plan (Expires in 30 days)</Typography>
              </Box>

              <Divider sx={{ my: 3, bgcolor: 'rgba(255,255,255,0.1)' }} />

              <Typography variant="h5" sx={{ fontWeight: 700, mb: 3 }}>Preferences</Typography>
              
              <List>
                <ListItem>
                  <ListItemIcon><Wifi size={20} /></ListItemIcon>
                  <ListItemText primary="Stream Only on Wi-Fi" secondary="Save mobile data" />
                  <Switch defaultChecked color="primary" />
                </ListItem>
                <ListItem>
                  <ListItemIcon><Moon size={20} /></ListItemIcon>
                  <ListItemText primary="Dark Mode" secondary="Always on" />
                  <Switch defaultChecked disabled />
                </ListItem>
              </List>

              <Divider sx={{ my: 3, bgcolor: 'rgba(255,255,255,0.1)' }} />
              
              <ListItemButton sx={{ borderRadius: 2, color: 'error.main' }}>
                 <ListItemIcon><LogOut color="#cf6679" /></ListItemIcon>
                 <ListItemText primary="Logout from all devices" />
              </ListItemButton>

           </Paper>
        </Grid>
      </Grid>
    </Box>
  );
}

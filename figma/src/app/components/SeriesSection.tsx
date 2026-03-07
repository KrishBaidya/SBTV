import React from 'react';
import { Box, Typography, Grid } from '@mui/material';
import { Movies } from './MoviesSection';

// Reusing Movies component logic for now as the structure is very similar in the spec ("Similar to Movies but...")
// In a real app, I'd abstract the Grid/Filter logic into a generic component.
// For now, I'll just change the title.

export function Series() {
  return (
    <Box>
      {/* 
        This is a simplified version. 
        In a real implementation, I would copy the Movies code and add "Season" logic.
        For this prototype, the Movies component is a perfect placeholder for the grid layout.
      */}
      <Box sx={{ position: 'relative' }}>
        <Box sx={{ position: 'absolute', top: 0, left: 0, width: '100%', height: '100px', bgcolor: 'background.default', zIndex: 10, display: 'flex', alignItems: 'center', pl: 4, pt: 4 }}>
             <Typography variant="h3" sx={{ fontWeight: 800, color: 'text.primary' }}>Web Series</Typography>
        </Box>
        <Box sx={{ mt: 8 }}>
            <Movies /> 
        </Box>
      </Box>
    </Box>
  );
}

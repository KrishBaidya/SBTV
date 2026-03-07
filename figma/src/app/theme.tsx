import { createTheme, responsiveFontSizes } from '@mui/material/styles';

// SB TV — Material 3 Dark Theme
let theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#C6A75E',      // Soft Gold
      light: '#E5CCa0',
      dark: '#947938',
      contrastText: '#0B0F1A',
    },
    secondary: {
      main: '#6E0E2E',      // Rich Burgundy
      light: '#9e3b56',
      dark: '#420008',
      contrastText: '#F5F2EB',
    },
    background: {
      default: '#0B0F1A',   // Deep Midnight Blue
      paper: '#141820',     // Surface
    },
    text: {
      primary: '#F5F2EB',
      secondary: 'rgba(245,242,235,0.65)',
    },
    error: {
      main: '#CF6679',
    },
    divider: 'rgba(255,255,255,0.07)',
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    button: {
      fontWeight: 600,
      textTransform: 'none',
      letterSpacing: '0.01em',
    },
  },
  shape: {
    borderRadius: 16,
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        body: {
          backgroundColor: '#0B0F1A',
          fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
          scrollbarColor: 'rgba(255,255,255,0.1) transparent',
          scrollbarWidth: 'thin',
          '&::-webkit-scrollbar': {
            width: '6px',
            height: '6px',
          },
          '&::-webkit-scrollbar-track': {
            background: 'transparent',
          },
          '&::-webkit-scrollbar-thumb': {
            borderRadius: '4px',
            backgroundColor: 'rgba(255,255,255,0.1)',
          },
          '&::-webkit-scrollbar-thumb:hover': {
            backgroundColor: 'rgba(198,167,94,0.4)',
          },
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 50,
          padding: '10px 24px',
          transition: 'all 0.2s cubic-bezier(0.2, 0, 0, 1)',
          '&:active': {
            transform: 'scale(0.96)',
          },
        },
        containedPrimary: {
          background: 'linear-gradient(135deg, #C6A75E 0%, #E5CCa0 100%)',
          color: '#0B0F1A',
          boxShadow: '0 4px 14px rgba(198,167,94,0.3)',
          '&:hover': {
            boxShadow: '0 6px 20px rgba(198,167,94,0.45)',
            background: 'linear-gradient(135deg, #D4B870 0%, #EDD8B0 100%)',
          },
        },
        outlined: {
          borderColor: 'rgba(255,255,255,0.2)',
          '&:hover': {
            borderColor: 'rgba(255,255,255,0.4)',
            backgroundColor: 'rgba(255,255,255,0.07)',
          },
        },
      },
    },
    MuiSlider: {
      styleOverrides: {
        root: {
          color: '#C6A75E',
          height: 4,
        },
        thumb: {
          width: 14,
          height: 14,
          '&:hover, &.Mui-focusVisible': {
            boxShadow: '0 0 0 8px rgba(198,167,94,0.18)',
          },
          '&.Mui-active': {
            width: 18,
            height: 18,
          },
        },
        track: {
          border: 'none',
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          borderRadius: 50,
          transition: 'all 0.2s',
          fontWeight: 500,
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: 'none',
          backgroundColor: '#141820',
          border: '1px solid rgba(255,255,255,0.06)',
        },
      },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          backgroundColor: '#0F121D',
          borderRight: '1px solid rgba(255,255,255,0.05)',
          backgroundImage: 'none',
        },
      },
    },
    MuiIconButton: {
      styleOverrides: {
        root: {
          transition: 'all 0.2s',
          '&:hover': {
            backgroundColor: 'rgba(255,255,255,0.08)',
          },
        },
      },
    },
    MuiTextField: {
      defaultProps: {
        variant: 'filled',
      },
      styleOverrides: {
        root: {
          '& .MuiFilledInput-root': {
            backgroundColor: 'rgba(255,255,255,0.05)',
            borderRadius: 12,
            '&:before': { borderBottom: 'none' },
            '&:after': { borderBottom: '2px solid #C6A75E' },
            '&:hover:not(.Mui-disabled):before': { borderBottom: 'none' },
          },
        },
      },
    },
    MuiSwitch: {
      styleOverrides: {
        switchBase: {
          '&.Mui-checked': {
            color: '#C6A75E',
            '& + .MuiSwitch-track': {
              backgroundColor: '#C6A75E',
              opacity: 0.4,
            },
          },
        },
      },
    },
  },
});

theme = responsiveFontSizes(theme);

export default theme;

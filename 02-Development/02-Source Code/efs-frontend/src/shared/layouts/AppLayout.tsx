import type { ReactNode } from 'react'
import {
  AppBar,
  Box,
  Drawer,
  Toolbar,
  Typography,
} from '@mui/material'

const DRAWER_WIDTH = 240

type AppLayoutProps = {
  children: ReactNode
  navigation: ReactNode
}

function AppLayout({
  children,
  navigation,
}: AppLayoutProps) {
  return (
    <Box
      sx={{
        display: 'flex',
        minHeight: '100vh',
      }}
    >
      <AppBar
        position="fixed"
        sx={{
          zIndex: (theme) =>
            theme.zIndex.drawer + 1,
        }}
      >
        <Toolbar>
          <Typography
            component="h1"
            variant="h6"
          >
            Enterprise Fraud System
          </Typography>
        </Toolbar>
      </AppBar>

      <Drawer
        variant="permanent"
        sx={{
          width: DRAWER_WIDTH,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: DRAWER_WIDTH,
            boxSizing: 'border-box',
          },
        }}
      >
        <Toolbar />

        <Box
          sx={{
            py: 2,
          }}
        >
          {navigation}
        </Box>
      </Drawer>

      <Box
        component="main"
        sx={{
          flexGrow: 1,
          minWidth: 0,
          bgcolor: 'background.default',
        }}
      >
        <Toolbar />

        <Box
          sx={{
            p: 3,
          }}
        >
          {children}
        </Box>
      </Box>
    </Box>
  )
}

export default AppLayout
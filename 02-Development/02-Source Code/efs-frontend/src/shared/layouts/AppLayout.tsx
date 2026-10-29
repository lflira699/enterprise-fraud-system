import type { ReactNode } from 'react'
import {
  AppBar,
  Box,
  Button,
  Drawer,
  Toolbar,
  Typography,
} from '@mui/material'
import { useTranslation } from 'react-i18next'
import { useAuthentication } from '../../security/AuthenticationProvider'

const DRAWER_WIDTH = 240

type AppLayoutProps = {
  children: ReactNode
  navigation: ReactNode
}

function AppLayout({
  children,
  navigation,
}: AppLayoutProps) {
  const { t } = useTranslation()
  const { logout } = useAuthentication()

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
            {t('app.title')}
          </Typography>

          <Box sx={{ flexGrow: 1 }} />

          <Button
            color="inherit"
            onClick={() => {
              void logout().catch(() => {
                window.alert(t('app.logoutError'))
              })
            }}
          >
            {t('app.logout')}
          </Button>
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
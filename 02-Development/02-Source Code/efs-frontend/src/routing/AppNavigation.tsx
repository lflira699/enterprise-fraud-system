import {
  List,
  ListItemButton,
  ListItemText,
} from '@mui/material'
import { useTranslation } from 'react-i18next'
import {
  useLocation,
  useNavigate,
} from 'react-router-dom'

import { ROUTE_PATHS } from './routePaths'

const NAVIGATION_ITEMS = [
  {
    labelKey: 'navigation.dashboard',
    path: ROUTE_PATHS.dashboard,
  },
  {
    labelKey: 'navigation.events',
    path: ROUTE_PATHS.events,
  },
  {
    labelKey: 'navigation.rules',
    path: ROUTE_PATHS.rules,
  },
  {
    labelKey: 'navigation.detection',
    path: ROUTE_PATHS.detection,
  },
  {
    labelKey: 'navigation.risk',
    path: ROUTE_PATHS.risk,
  },
  {
    labelKey: 'navigation.alerts',
    path: ROUTE_PATHS.alerts,
  },
  {
    labelKey: 'navigation.cases',
    path: ROUTE_PATHS.cases,
  },
  {
    labelKey: 'navigation.evidence',
    path: ROUTE_PATHS.evidence,
  },
  {
    labelKey: 'navigation.reports',
    path: ROUTE_PATHS.reports,
  },
  {
    labelKey: 'navigation.administration',
    path: ROUTE_PATHS.administration,
  },
  {
    labelKey: 'navigation.configuration',
    path: ROUTE_PATHS.configuration,
  },
  {
    labelKey: 'navigation.audit',
    path: ROUTE_PATHS.audit,
  },
] as const

function AppNavigation() {
  const { t } = useTranslation()
  const location = useLocation()
  const navigate = useNavigate()

  return (
    <List
      component="nav"
      aria-label={t('navigation.primary')}
      disablePadding
    >
      {NAVIGATION_ITEMS.map((item) => (
        <ListItemButton
          key={item.path}
          selected={
            location.pathname === item.path
          }
          onClick={() => {
            void navigate(item.path)
          }}
        >
          <ListItemText
            primary={t(item.labelKey)}
          />
        </ListItemButton>
      ))}
    </List>
  )
}

export default AppNavigation
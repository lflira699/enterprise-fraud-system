import {
  List,
  ListItemButton,
  ListItemText,
} from '@mui/material'
import {
  useLocation,
  useNavigate,
} from 'react-router-dom'

import { ROUTE_PATHS } from './routePaths'

const NAVIGATION_ITEMS = [
  {
    label: 'Dashboard',
    path: ROUTE_PATHS.dashboard,
  },
  {
    label: 'Events',
    path: ROUTE_PATHS.events,
  },
  {
    label: 'Rules',
    path: ROUTE_PATHS.rules,
  },
  {
    label: 'Detection',
    path: ROUTE_PATHS.detection,
  },
  {
    label: 'Risk',
    path: ROUTE_PATHS.risk,
  },
  {
    label: 'Alerts',
    path: ROUTE_PATHS.alerts,
  },
  {
    label: 'Cases',
    path: ROUTE_PATHS.cases,
  },
  {
    label: 'Evidence',
    path: ROUTE_PATHS.evidence,
  },
  {
    label: 'Reports',
    path: ROUTE_PATHS.reports,
  },
  {
    label: 'Administration',
    path: ROUTE_PATHS.administration,
  },
  {
    label: 'Configuration',
    path: ROUTE_PATHS.configuration,
  },
  {
    label: 'Audit',
    path: ROUTE_PATHS.audit,
  },
] as const

function AppNavigation() {
  const location = useLocation()
  const navigate = useNavigate()

  return (
    <List
      component="nav"
      aria-label="Primary navigation"
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
            primary={item.label}
          />
        </ListItemButton>
      ))}
    </List>
  )
}

export default AppNavigation
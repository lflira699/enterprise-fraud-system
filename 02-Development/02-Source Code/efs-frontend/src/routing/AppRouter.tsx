import { Box, Typography } from '@mui/material'
import { Route, Routes } from 'react-router-dom'

import AlertsPage from '../modules/alerts/pages/AlertsPage'
import AuditPage from '../modules/audit/pages/AuditPage'
import CasesPage from '../modules/cases/pages/CasesPage'
import ConfigurationPage from '../modules/configuration/pages/ConfigurationPage'
import DashboardPage from '../modules/dashboard/pages/DashboardPage'
import DetectionPage from '../modules/detection/pages/DetectionPage'
import ReportsPage from '../modules/reports/pages/ReportsPage'
import RiskPage from '../modules/risk/pages/RiskPage'
import RulesPage from '../modules/rules/pages/RulesPage'
import AppLayout from '../shared/layouts/AppLayout'
import AppNavigation from './AppNavigation'
import { ROUTE_PATHS, type RoutePath } from './routePaths'

type RouteBoundaryProps = {
  path: RoutePath
}

function RouteBoundary({
  path,
}: RouteBoundaryProps) {
  return (
    <Box>
      <Typography
        component="h2"
        variant="h4"
      >
        {path}
      </Typography>
    </Box>
  )
}

function AppRouter() {
  return (
    <AppLayout
      navigation={<AppNavigation />}
    >
      <Routes>
        <Route
          path={ROUTE_PATHS.dashboard}
          element={<DashboardPage />}
        />

        <Route
          path={ROUTE_PATHS.events}
          element={
            <RouteBoundary
              path={ROUTE_PATHS.events}
            />
          }
        />

        <Route
          path={ROUTE_PATHS.rules}
          element={<RulesPage />}
        />

        <Route
          path={ROUTE_PATHS.detection}
          element={<DetectionPage />}
        />

        <Route
          path={ROUTE_PATHS.risk}
          element={<RiskPage />}
        />

        <Route
          path={ROUTE_PATHS.alerts}
          element={<AlertsPage />}
        />

        <Route
          path={ROUTE_PATHS.cases}
          element={<CasesPage />}
        />

        <Route
          path={ROUTE_PATHS.evidence}
          element={
            <RouteBoundary
              path={ROUTE_PATHS.evidence}
            />
          }
        />

        <Route
          path={ROUTE_PATHS.reports}
          element={<ReportsPage />}
        />

        <Route
          path={ROUTE_PATHS.administration}
          element={
            <RouteBoundary
              path={ROUTE_PATHS.administration}
            />
          }
        />

        <Route
          path={ROUTE_PATHS.configuration}
          element={<ConfigurationPage />}
        />

        <Route
          path={ROUTE_PATHS.audit}
          element={<AuditPage />}
        />
      </Routes>
    </AppLayout>
  )
}

export default AppRouter

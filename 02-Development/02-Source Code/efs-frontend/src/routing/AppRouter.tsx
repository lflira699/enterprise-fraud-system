import { Box, Typography } from '@mui/material'
import { Route, Routes } from 'react-router-dom'

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
          element={
            <RouteBoundary
              path={ROUTE_PATHS.dashboard}
            />
          }
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
          element={
            <RouteBoundary
              path={ROUTE_PATHS.rules}
            />
          }
        />

        <Route
          path={ROUTE_PATHS.detection}
          element={
            <RouteBoundary
              path={ROUTE_PATHS.detection}
            />
          }
        />

        <Route
          path={ROUTE_PATHS.risk}
          element={
            <RouteBoundary
              path={ROUTE_PATHS.risk}
            />
          }
        />

        <Route
          path={ROUTE_PATHS.alerts}
          element={
            <RouteBoundary
              path={ROUTE_PATHS.alerts}
            />
          }
        />

        <Route
          path={ROUTE_PATHS.cases}
          element={
            <RouteBoundary
              path={ROUTE_PATHS.cases}
            />
          }
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
          element={
            <RouteBoundary
              path={ROUTE_PATHS.reports}
            />
          }
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
          element={
            <RouteBoundary
              path={ROUTE_PATHS.configuration}
            />
          }
        />

        <Route
          path={ROUTE_PATHS.audit}
          element={
            <RouteBoundary
              path={ROUTE_PATHS.audit}
            />
          }
        />
      </Routes>
    </AppLayout>
  )
}

export default AppRouter
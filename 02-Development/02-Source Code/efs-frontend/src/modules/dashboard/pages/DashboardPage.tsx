import {
  Box,
  Typography,
} from '@mui/material'
import { useTranslation } from 'react-i18next'

import DashboardMetricCard from '../components/DashboardMetricCard'

function DashboardPage() {
  const { t } = useTranslation()

  const metrics = [
    t('dashboard.metrics.criticalAlerts'),
    t('dashboard.metrics.openAlerts'),
    t('dashboard.metrics.openCases'),
    t('dashboard.metrics.closedCases'),
    t('dashboard.metrics.averageRisk'),
    t('dashboard.metrics.activatedDetectionScenarios'),
  ]

  return (
    <Box>
      <Typography
        component="h2"
        variant="h4"
      >
        {t('navigation.dashboard')}
      </Typography>

      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns:
            'repeat(auto-fit, minmax(220px, 1fr))',
          gap: 2,
          mt: 3,
        }}
      >
        {metrics.map((label) => (
          <DashboardMetricCard
            key={label}
            label={label}
            value="—"
          />
        ))}
      </Box>
    </Box>
  )
}

export default DashboardPage
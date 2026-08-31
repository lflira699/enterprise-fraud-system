import { Typography } from '@mui/material'
import { useTranslation } from 'react-i18next'

function DashboardPage() {
  const { t } = useTranslation()

  return (
    <Typography
      component="h2"
      variant="h4"
    >
      {t('navigation.dashboard')}
    </Typography>
  )
}

export default DashboardPage
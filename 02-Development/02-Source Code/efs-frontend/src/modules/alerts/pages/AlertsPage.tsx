import { Typography } from '@mui/material'
import { useTranslation } from 'react-i18next'

function AlertsPage() {
  const { t } = useTranslation()

  return (
    <Typography
      component="h2"
      variant="h4"
    >
      {t('navigation.alerts')}
    </Typography>
  )
}

export default AlertsPage
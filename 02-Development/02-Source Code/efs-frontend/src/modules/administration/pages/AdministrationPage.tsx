import {
  Alert as MuiAlert,
  Box,
  CircularProgress,
  Paper,
  Typography,
} from '@mui/material'
import { useTranslation } from 'react-i18next'

import { useSystemHealthQuery } from '../hooks/useSystemHealthQuery'

function formatDateTime(
  value: string | null | undefined,
) {
  if (!value) {
    return '—'
  }

  const date =
    new Date(value)

  if (Number.isNaN(date.getTime())) {
    return value
  }

  return new Intl.DateTimeFormat(
    'es-GT',
    {
      dateStyle: 'short',
      timeStyle: 'short',
    },
  ).format(date)
}

function AdministrationPage() {
  const { t } = useTranslation()

  const healthQuery =
    useSystemHealthQuery()

  return (
    <Box>
      <Typography
        component="h2"
        variant="h4"
      >
        {t('navigation.administration')}
      </Typography>

      <Paper
        sx={{
          p: 2,
          mt: 3,
        }}
      >
        <Typography
          component="h3"
          variant="h6"
          sx={{ mb: 2 }}
        >
          {t('systemHealth.title')}
        </Typography>

        {healthQuery.isLoading
          ? (
            <Box
              sx={{
                display: 'flex',
                alignItems: 'center',
                gap: 2,
              }}
            >
              <CircularProgress
                size={24}
                aria-label={t('systemHealth.loading')}
              />

              <Typography>
                {t('systemHealth.loading')}
              </Typography>
            </Box>
          )
          : null}

        {healthQuery.isError
          ? (
            <MuiAlert severity="error">
              {t('systemHealth.error')}
            </MuiAlert>
          )
          : null}

        {healthQuery.data
          ? (
            <Box>
              <Box
                sx={{
                  display: 'grid',
                  gridTemplateColumns:
                    'repeat(auto-fit, minmax(220px, 1fr))',
                  gap: 2,
                }}
              >
                <Box>
                  <Typography variant="body2">
                    {t('systemHealth.overallStatus')}
                  </Typography>

                  <Typography variant="h6">
                    {healthQuery.data.status}
                  </Typography>
                </Box>

                <Box>
                  <Typography variant="body2">
                    {t('systemHealth.informationStatus')}
                  </Typography>

                  <Typography variant="h6">
                    {healthQuery.data.informationStatus}
                  </Typography>
                </Box>

                <Box>
                  <Typography variant="body2">
                    {t('systemHealth.checkedAt')}
                  </Typography>

                  <Typography variant="h6">
                    {formatDateTime(
                      healthQuery.data.checkedAt,
                    )}
                  </Typography>
                </Box>
              </Box>

              <Typography
                component="h4"
                variant="h6"
                sx={{ mt: 3 }}
              >
                {t('systemHealth.components')}
              </Typography>

              {healthQuery.data.components.length === 0
                ? (
                  <Typography sx={{ mt: 1 }}>
                    {t('systemHealth.noComponents')}
                  </Typography>
                )
                : (
                  <Box
                    component="ul"
                    sx={{
                      mt: 1,
                      mb: 0,
                    }}
                  >
                    {healthQuery.data.components.map(
                      (component) => (
                        <Box
                          component="li"
                          key={component.name}
                          sx={{ mb: 1 }}
                        >
                          <Typography component="span">
                            {component.name}
                            {': '}
                            {component.status}
                          </Typography>
                        </Box>
                      ),
                    )}
                  </Box>
                )}
            </Box>
          )
          : null}
      </Paper>
    </Box>
  )
}

export default AdministrationPage

import {
  Alert as MuiAlert,
  Box,
  Button,
  Paper,
  Typography,
} from '@mui/material'
import { useTranslation } from 'react-i18next'
import {
  useLocation,
  useNavigate,
  useParams,
} from 'react-router-dom'

import { ROUTE_PATHS } from '../../../routing/routePaths'
import { HttpError } from '../../../services/httpClient'
import { useAlertQuery } from '../hooks/useAlertsQuery'

function formatDateTime(
  value: string | null,
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

function formatNumber(
  value: number | null,
) {
  if (
    value === null
    || value === undefined
  ) {
    return '—'
  }

  return String(value)
}

function DetailField({
  label,
  value,
}: {
  label: string
  value: string | null
}) {
  return (
    <Box>
      <Typography
        variant="body2"
        color="text.secondary"
      >
        {label}
      </Typography>

      <Typography>
        {value || '—'}
      </Typography>
    </Box>
  )
}

function resolveErrorKey(
  error: unknown,
) {
  if (error instanceof HttpError) {
    if (error.status === 404) {
      return 'alerts.detail.notFound'
    }

    if (
      error.status === 401
      || error.status === 403
    ) {
      return 'alerts.detail.unauthorized'
    }
  }

  return 'alerts.detail.error'
}

function AlertDetailPage() {
  const { t } =
    useTranslation()

  const navigate =
    useNavigate()

  const location =
    useLocation()

  const { alertId } =
    useParams<{
      alertId: string
    }>()

  const alertQuery =
    useAlertQuery(
      alertId ?? null,
    )

  const alert =
    alertQuery.data

  return (
    <Box>
      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          gap: 2,
        }}
      >
        <Button
          variant="outlined"
          onClick={() => {
            void navigate(
              ROUTE_PATHS.alerts,
              {
                state:
                  location.state
                  ?? null,
              },
            )
          }}
        >
          {t(
            'alerts.detail.back',
          )}
        </Button>

        <Typography
          component="h2"
          variant="h4"
        >
          {t(
            'alerts.detail.title',
          )}
        </Typography>
      </Box>

      {alertQuery.isError && (
        <MuiAlert
          severity="error"
          sx={{
            mt: 3,
          }}
        >
          {t(
            resolveErrorKey(
              alertQuery.error,
            ),
          )}
        </MuiAlert>
      )}

      {alertQuery.isFetching
        && !alert
        && (
          <Typography
            sx={{
              mt: 3,
            }}
          >
            {t(
              'alerts.detail.loading',
            )}
          </Typography>
        )}

      {alert && (
        <Paper
          sx={{
            p: 2,
            mt: 3,
          }}
        >
          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns:
                'repeat(auto-fit, minmax(220px, 1fr))',
              gap: 2,
            }}
          >
            <DetailField
              label={t(
                'alerts.detail.fields.alertId',
              )}
              value={alert.alertId}
            />

            <DetailField
              label={t(
                'alerts.detail.fields.reference',
              )}
              value={
                alert.alertReference
              }
            />

            <DetailField
              label={t(
                'alerts.detail.fields.status',
              )}
              value={alert.status}
            />

            <DetailField
              label={t(
                'alerts.detail.fields.priority',
              )}
              value={alert.priority}
            />

            <DetailField
              label={t(
                'alerts.detail.fields.priorityScore',
              )}
              value={formatNumber(
                alert.priorityScore,
              )}
            />

            <DetailField
              label={t(
                'alerts.detail.fields.severity',
              )}
              value={alert.severity}
            />

            <DetailField
              label={t(
                'alerts.detail.fields.riskScore',
              )}
              value={formatNumber(
                alert.riskScore,
              )}
            />

            <DetailField
              label={t(
                'alerts.detail.fields.alertType',
              )}
              value={alert.alertType}
            />

            <DetailField
              label={t(
                'alerts.detail.fields.category',
              )}
              value={alert.category}
            />

            <DetailField
              label={t(
                'alerts.detail.fields.customerId',
              )}
              value={alert.customerId}
            />

            <DetailField
              label={t(
                'alerts.detail.fields.transactionId',
              )}
              value={alert.transactionId}
            />

            <DetailField
              label={t(
                'alerts.detail.fields.decisionId',
              )}
              value={alert.decisionId}
            />

            <DetailField
              label={t(
                'alerts.detail.fields.riskAssessmentId',
              )}
              value={
                alert.riskAssessmentId
              }
            />

            <DetailField
              label={t(
                'alerts.detail.fields.scenarioId',
              )}
              value={alert.scenarioId}
            />

            <DetailField
              label={t(
                'alerts.detail.fields.ruleId',
              )}
              value={alert.ruleId}
            />

            <DetailField
              label={t(
                'alerts.detail.fields.assignedTo',
              )}
              value={alert.assignedTo}
            />

            <DetailField
              label={t(
                'alerts.detail.fields.assignedTeam',
              )}
              value={
                alert.assignedTeam
              }
            />

            <DetailField
              label={t(
                'alerts.detail.fields.correlationId',
              )}
              value={
                alert.correlationId
              }
            />

            <DetailField
              label={t(
                'alerts.detail.fields.generatedAt',
              )}
              value={formatDateTime(
                alert.generatedAt,
              )}
            />

            <DetailField
              label={t(
                'alerts.detail.fields.dueAt',
              )}
              value={formatDateTime(
                alert.dueAt,
              )}
            />

            <DetailField
              label={t(
                'alerts.detail.fields.closedAt',
              )}
              value={formatDateTime(
                alert.closedAt,
              )}
            />

            <DetailField
              label={t(
                'alerts.detail.fields.createdAt',
              )}
              value={formatDateTime(
                alert.createdAt,
              )}
            />

            <DetailField
              label={t(
                'alerts.detail.fields.updatedAt',
              )}
              value={formatDateTime(
                alert.updatedAt,
              )}
            />
          </Box>

          <Box
            sx={{
              mt: 3,
            }}
          >
            <DetailField
              label={t(
                'alerts.detail.fields.title',
              )}
              value={alert.title}
            />
          </Box>

          <Box
            sx={{
              mt: 2,
            }}
          >
            <DetailField
              label={t(
                'alerts.detail.fields.description',
              )}
              value={alert.description}
            />
          </Box>

          <Box
            sx={{
              mt: 2,
            }}
          >
            <DetailField
              label={t(
                'alerts.detail.fields.closureReason',
              )}
              value={
                alert.closureReason
              }
            />
          </Box>
        </Paper>
      )}
    </Box>
  )
}

export default AlertDetailPage

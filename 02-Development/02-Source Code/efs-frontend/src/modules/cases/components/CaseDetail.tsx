import {
  Alert as MuiAlert,
  Box,
  Button,
  Paper,
  Typography,
} from '@mui/material'
import { useTranslation } from 'react-i18next'

import CaseEvidenceSection from '../../evidence/components/CaseEvidenceSection'
import { useCaseQuery } from '../hooks/useCasesQuery'

type CaseDetailProps = {
  caseId: string
  onBack: () => void
}

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

function CaseDetail({
  caseId,
  onBack,
}: CaseDetailProps) {
  const { t } =
    useTranslation()

  const caseQuery =
    useCaseQuery(
      caseId,
    )

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
          onClick={onBack}
        >
          {t(
            'cases.detail.back',
          )}
        </Button>

        <Typography
          component="h2"
          variant="h4"
        >
          {t(
            'cases.detail.title',
          )}
        </Typography>
      </Box>

      {caseQuery.isError && (
        <MuiAlert
          severity="error"
          sx={{
            mt: 3,
          }}
        >
          {t(
            'cases.detail.error',
          )}
        </MuiAlert>
      )}

      {caseQuery.isFetching
        && !caseQuery.data
        && (
          <Typography
            sx={{
              mt: 3,
            }}
          >
            {t(
              'cases.detail.loading',
            )}
          </Typography>
        )}

      {caseQuery.data && (
        <>
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
                  'cases.detail.fields.caseId',
                )}
                value={
                  caseQuery.data.caseId
                }
              />

              <DetailField
                label={t(
                  'cases.detail.fields.caseNumber',
                )}
                value={
                  caseQuery.data.caseNumber
                }
              />

              <DetailField
                label={t(
                  'cases.detail.fields.caseType',
                )}
                value={
                  caseQuery.data.caseType
                }
              />

              <DetailField
                label={t(
                  'cases.detail.fields.category',
                )}
                value={
                  caseQuery.data.category
                }
              />

              <DetailField
                label={t(
                  'cases.detail.fields.status',
                )}
                value={
                  caseQuery.data.currentStatus
                }
              />

              <DetailField
                label={t(
                  'cases.detail.fields.priority',
                )}
                value={
                  caseQuery.data.priority
                }
              />

              <DetailField
                label={t(
                  'cases.detail.fields.severity',
                )}
                value={
                  caseQuery.data.severity
                }
              />

              <DetailField
                label={t(
                  'cases.detail.fields.assignedTeam',
                )}
                value={
                  caseQuery.data.assignedTeam
                }
              />

              <DetailField
                label={t(
                  'cases.detail.fields.assignedUser',
                )}
                value={
                  caseQuery.data.assignedUser
                }
              />

              <DetailField
                label={t(
                  'cases.detail.fields.transactionId',
                )}
                value={
                  caseQuery.data.transactionId
                }
              />

              <DetailField
                label={t(
                  'cases.detail.fields.customerId',
                )}
                value={
                  caseQuery.data.customerId
                }
              />

              <DetailField
                label={t(
                  'cases.detail.fields.organizationId',
                )}
                value={
                  caseQuery.data.organizationId
                }
              />

              <DetailField
                label={t(
                  'cases.detail.fields.tenantId',
                )}
                value={
                  caseQuery.data.tenantId
                }
              />

              <DetailField
                label={t(
                  'cases.detail.fields.createdAt',
                )}
                value={
                  formatDateTime(
                    caseQuery.data.createdAt,
                  )
                }
              />

              <DetailField
                label={t(
                  'cases.detail.fields.updatedAt',
                )}
                value={
                  formatDateTime(
                    caseQuery.data.updatedAt,
                  )
                }
              />

              <DetailField
                label={t(
                  'cases.detail.fields.dueDate',
                )}
                value={
                  formatDateTime(
                    caseQuery.data.dueDate,
                  )
                }
              />

              <DetailField
                label={t(
                  'cases.detail.fields.closedAt',
                )}
                value={
                  formatDateTime(
                    caseQuery.data.closedAt,
                  )
                }
              />
            </Box>
          </Paper>

          <CaseEvidenceSection
            caseId={caseId}
          />
        </>
      )}
    </Box>
  )
}

export default CaseDetail

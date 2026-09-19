import {
  useMemo,
  useState,
} from 'react'
import {
  Alert as MuiAlert,
  Box,
  Button,
  MenuItem,
  Paper,
  TextField,
  Typography,
} from '@mui/material'
import { useTranslation } from 'react-i18next'

import {
  useExportReportMutation,
  useGenerateReportMutation,
  useReportDefinitionsQuery,
  useReportExportOptionsQuery,
  useRetrieveReportMutation,
} from '../hooks/useReports'
import type {
  GeneratedReport,
  ReportCriteria,
} from '../types/report'

type CriteriaDraft = {
  tenantId: string
  components: string
  status: string
  priority: string
  assignedUser: string
  assignedTeam: string
}

const EMPTY_CRITERIA: CriteriaDraft = {
  tenantId: '',
  components: '',
  status: '',
  priority: '',
  assignedUser: '',
  assignedTeam: '',
}

function normalizeText(
  value: string,
): string | undefined {
  const normalized =
    value.trim()

  return normalized || undefined
}

function normalizeComponents(
  value: string,
): string[] | undefined {
  const components =
    value
      .split(',')
      .map((component) =>
        component.trim(),
      )
      .filter(Boolean)

  if (components.length === 0) {
    return undefined
  }

  return components
}

function buildCriteria(
  allowedCriteria: string[],
  draft: CriteriaDraft,
): ReportCriteria {
  const criteria: ReportCriteria = {}

  if (
    allowedCriteria.includes(
      'tenantId',
    )
  ) {
    criteria.tenantId =
      normalizeText(
        draft.tenantId,
      )
  }

  if (
    allowedCriteria.includes(
      'components',
    )
  ) {
    criteria.components =
      normalizeComponents(
        draft.components,
      )
  }

  if (
    allowedCriteria.includes(
      'status',
    )
  ) {
    criteria.status =
      normalizeText(
        draft.status,
      )
  }

  if (
    allowedCriteria.includes(
      'priority',
    )
  ) {
    criteria.priority =
      normalizeText(
        draft.priority,
      )
  }

  if (
    allowedCriteria.includes(
      'assignedUser',
    )
  ) {
    criteria.assignedUser =
      normalizeText(
        draft.assignedUser,
      )
  }

  if (
    allowedCriteria.includes(
      'assignedTeam',
    )
  ) {
    criteria.assignedTeam =
      normalizeText(
        draft.assignedTeam,
      )
  }

  return Object.fromEntries(
    Object.entries(criteria)
      .filter(
        ([, value]) =>
          value !== undefined,
      ),
  ) as ReportCriteria
}

function formatDateTime(
  value: string,
) {
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

function ReportsPage() {
  const { t } =
    useTranslation()

  const definitionsQuery =
    useReportDefinitionsQuery()

  const generateMutation =
    useGenerateReportMutation()

  const retrieveMutation =
    useRetrieveReportMutation()

  const exportMutation =
    useExportReportMutation()

  const [
    selectedReportCode,
    setSelectedReportCode,
  ] = useState('')

  const [
    criteriaDraft,
    setCriteriaDraft,
  ] = useState<CriteriaDraft>(
    EMPTY_CRITERIA,
  )

  const [
    lookupReportId,
    setLookupReportId,
  ] = useState('')

  const [
    currentReport,
    setCurrentReport,
  ] = useState<GeneratedReport | null>(
    null,
  )

  const [
    exportFormat,
    setExportFormat,
  ] = useState('')

  const selectedDefinition =
    useMemo(
      () =>
        definitionsQuery.data
          ?.find(
            (definition) =>
              definition.reportCode
              === selectedReportCode,
          )
        ?? null,
      [
        definitionsQuery.data,
        selectedReportCode,
      ],
    )

  const exportOptionsQuery =
    useReportExportOptionsQuery(
      currentReport?.reportId
      ?? null,
    )

  function updateCriterion(
    field: keyof CriteriaDraft,
    value: string,
  ) {
    setCriteriaDraft(
      (current) => ({
        ...current,
        [field]: value,
      }),
    )
  }

  async function handleGenerate() {
    if (!selectedDefinition) {
      return
    }

    const report =
      await generateMutation
        .mutateAsync({
          reportCode:
            selectedDefinition
              .reportCode,

          criteria:
            buildCriteria(
              selectedDefinition
                .allowedCriteria,
              criteriaDraft,
            ),
        })

    setCurrentReport(report)
    setLookupReportId(
      report.reportId,
    )
    setExportFormat('')
  }

  async function handleRetrieve() {
    const reportId =
      lookupReportId.trim()

    if (!reportId) {
      return
    }

    const report =
      await retrieveMutation
        .mutateAsync(
          reportId,
        )

    setCurrentReport(report)
    setExportFormat('')
  }

  async function handleExport() {
    if (
      !currentReport
      || !exportFormat
    ) {
      return
    }

    const response =
      await exportMutation
        .mutateAsync({
          reportId:
            currentReport.reportId,
          format:
            exportFormat,
        })

    const objectUrl =
      URL.createObjectURL(
        response.blob,
      )

    const anchor =
      document.createElement('a')

    anchor.href =
      objectUrl

    anchor.download =
      response.fileName
      ?? `efs-report-${currentReport.reportId}`

    document.body.appendChild(
      anchor,
    )

    try {
      anchor.click()
    } finally {
      anchor.remove()

      URL.revokeObjectURL(
        objectUrl,
      )
    }
  }

  const allowedCriteria =
    selectedDefinition
      ?.allowedCriteria
    ?? []

  const exportFormats =
    exportOptionsQuery.data
      ?.formats
    ?? []

  return (
    <Box>
      <Typography
        component="h2"
        variant="h4"
      >
        {t('navigation.reports')}
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
        >
          {t(
            'reports.generation.title',
          )}
        </Typography>

        {definitionsQuery.isError && (
          <MuiAlert
            severity="error"
            sx={{
              mt: 2,
            }}
          >
            {t(
              'reports.generation.definitionsError',
            )}
          </MuiAlert>
        )}

        <Box
          sx={{
            display: 'grid',
            gridTemplateColumns:
              'repeat(auto-fit, minmax(240px, 1fr))',
            gap: 2,
            mt: 2,
          }}
        >
          <TextField
            select
            label={t(
              'reports.generation.definition',
            )}
            size="small"
            value={selectedReportCode}
            disabled={
              definitionsQuery.isFetching
            }
            onChange={(event) => {
              setSelectedReportCode(
                event.target.value,
              )

              setCriteriaDraft(
                EMPTY_CRITERIA,
              )

              setCurrentReport(null)
              setExportFormat('')
            }}
          >
            {(
              definitionsQuery.data
              ?? []
            ).map(
              (definition) => (
                <MenuItem
                  key={
                    definition.reportCode
                  }
                  value={
                    definition.reportCode
                  }
                >
                  {
                    definition.reportCode
                  }
                  {' — '}
                  {
                    definition.category
                  }
                </MenuItem>
              ),
            )}
          </TextField>

          {allowedCriteria.includes(
            'tenantId',
          ) && (
            <TextField
              label={t(
                'reports.criteria.tenantId',
              )}
              size="small"
              value={
                criteriaDraft.tenantId
              }
              onChange={(event) =>
                updateCriterion(
                  'tenantId',
                  event.target.value,
                )
              }
            />
          )}

          {allowedCriteria.includes(
            'components',
          ) && (
            <TextField
              label={t(
                'reports.criteria.components',
              )}
              size="small"
              value={
                criteriaDraft.components
              }
              helperText={t(
                'reports.criteria.componentsHelp',
              )}
              onChange={(event) =>
                updateCriterion(
                  'components',
                  event.target.value,
                )
              }
            />
          )}

          {allowedCriteria.includes(
            'status',
          ) && (
            <TextField
              label={t(
                'reports.criteria.status',
              )}
              size="small"
              value={
                criteriaDraft.status
              }
              onChange={(event) =>
                updateCriterion(
                  'status',
                  event.target.value,
                )
              }
            />
          )}

          {allowedCriteria.includes(
            'priority',
          ) && (
            <TextField
              label={t(
                'reports.criteria.priority',
              )}
              size="small"
              value={
                criteriaDraft.priority
              }
              onChange={(event) =>
                updateCriterion(
                  'priority',
                  event.target.value,
                )
              }
            />
          )}

          {allowedCriteria.includes(
            'assignedUser',
          ) && (
            <TextField
              label={t(
                'reports.criteria.assignedUser',
              )}
              size="small"
              value={
                criteriaDraft.assignedUser
              }
              onChange={(event) =>
                updateCriterion(
                  'assignedUser',
                  event.target.value,
                )
              }
            />
          )}

          {allowedCriteria.includes(
            'assignedTeam',
          ) && (
            <TextField
              label={t(
                'reports.criteria.assignedTeam',
              )}
              size="small"
              value={
                criteriaDraft.assignedTeam
              }
              onChange={(event) =>
                updateCriterion(
                  'assignedTeam',
                  event.target.value,
                )
              }
            />
          )}
        </Box>

        <Button
          variant="contained"
          sx={{
            mt: 2,
          }}
          disabled={
            !selectedDefinition
            || generateMutation.isPending
          }
          onClick={() => {
            void handleGenerate()
          }}
        >
          {t(
            'reports.generation.generate',
          )}
        </Button>

        {generateMutation.isError && (
          <MuiAlert
            severity="error"
            sx={{
              mt: 2,
            }}
          >
            {t(
              'reports.generation.error',
            )}
          </MuiAlert>
        )}
      </Paper>

      <Paper
        sx={{
          p: 2,
          mt: 3,
        }}
      >
        <Typography
          component="h3"
          variant="h6"
        >
          {t(
            'reports.lookup.title',
          )}
        </Typography>

        <Box
          sx={{
            display: 'flex',
            alignItems: 'flex-start',
            gap: 2,
            mt: 2,
            flexWrap: 'wrap',
          }}
        >
          <TextField
            label={t(
              'reports.lookup.reportId',
            )}
            size="small"
            value={lookupReportId}
            sx={{
              minWidth: 360,
            }}
            onChange={(event) =>
              setLookupReportId(
                event.target.value,
              )
            }
          />

          <Button
            variant="outlined"
            disabled={
              !lookupReportId.trim()
              || retrieveMutation.isPending
            }
            onClick={() => {
              void handleRetrieve()
            }}
          >
            {t(
              'reports.lookup.load',
            )}
          </Button>
        </Box>

        {retrieveMutation.isError && (
          <MuiAlert
            severity="error"
            sx={{
              mt: 2,
            }}
          >
            {t(
              'reports.lookup.error',
            )}
          </MuiAlert>
        )}
      </Paper>

      {currentReport && (
        <Paper
          sx={{
            p: 2,
            mt: 3,
          }}
        >
          <Typography
            component="h3"
            variant="h6"
          >
            {t(
              'reports.result.title',
            )}
          </Typography>

          <Box
            sx={{
              mt: 2,
              display: 'grid',
              gap: 1,
            }}
          >
            <Typography>
              {t(
                'reports.result.reportId',
              )}
              {': '}
              {currentReport.reportId}
            </Typography>

            <Typography>
              {t(
                'reports.result.reportCode',
              )}
              {': '}
              {currentReport.reportCode}
            </Typography>

            <Typography>
              {t(
                'reports.result.generatedAt',
              )}
              {': '}
              {formatDateTime(
                currentReport.generatedAt,
              )}
            </Typography>
          </Box>

          <Typography
            component="h4"
            variant="subtitle1"
            sx={{
              mt: 2,
            }}
          >
            {t(
              'reports.result.content',
            )}
          </Typography>

          <Box
            component="pre"
            sx={{
              p: 2,
              mt: 1,
              overflow: 'auto',
              backgroundColor:
                'action.hover',
              whiteSpace: 'pre-wrap',
              wordBreak: 'break-word',
            }}
          >
            {JSON.stringify(
              currentReport.content,
              null,
              2,
            )}
          </Box>

          <Typography
            component="h4"
            variant="subtitle1"
            sx={{
              mt: 3,
            }}
          >
            {t(
              'reports.export.title',
            )}
          </Typography>

          {exportOptionsQuery.isError && (
            <MuiAlert
              severity="error"
              sx={{
                mt: 2,
              }}
            >
              {t(
                'reports.export.optionsError',
              )}
            </MuiAlert>
          )}

          <Box
            sx={{
              display: 'flex',
              alignItems: 'flex-start',
              gap: 2,
              mt: 2,
              flexWrap: 'wrap',
            }}
          >
            <TextField
              select
              label={t(
                'reports.export.format',
              )}
              size="small"
              value={exportFormat}
              disabled={
                exportOptionsQuery
                  .isFetching
              }
              sx={{
                minWidth: 180,
              }}
              onChange={(event) =>
                setExportFormat(
                  event.target.value,
                )
              }
            >
              {exportFormats.map(
                (format) => (
                  <MenuItem
                    key={format}
                    value={format}
                  >
                    {format}
                  </MenuItem>
                ),
              )}
            </TextField>

            <Button
              variant="contained"
              disabled={
                !exportFormat
                || exportMutation.isPending
              }
              onClick={() => {
                void handleExport()
              }}
            >
              {t(
                'reports.export.download',
              )}
            </Button>
          </Box>

          {exportMutation.isError && (
            <MuiAlert
              severity="error"
              sx={{
                mt: 2,
              }}
            >
              {t(
                'reports.export.error',
              )}
            </MuiAlert>
          )}
        </Paper>
      )}
    </Box>
  )
}

export default ReportsPage

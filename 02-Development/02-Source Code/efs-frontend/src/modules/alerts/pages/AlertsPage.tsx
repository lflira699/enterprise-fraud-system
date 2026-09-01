import {
  useMemo,
  useState,
} from 'react'
import {
  Alert as MuiAlert,
  Box,
  Button,
  Paper,
  TextField,
  Typography,
} from '@mui/material'
import {
  DataGrid,
  type GridColDef,
  type GridPaginationModel,
  type GridSortModel,
} from '@mui/x-data-grid'
import { useTranslation } from 'react-i18next'

import { useAlertsQuery } from '../hooks/useAlertsQuery'
import type {
  Alert as AlertRecord,
  AlertSearchParams,
  AlertSortDirection,
  AlertSortField,
} from '../types/alert'

type AlertFilterDraft = Pick<
  AlertSearchParams,
  | 'status'
  | 'priority'
  | 'riskLevel'
  | 'assignedTo'
  | 'createdFrom'
  | 'createdTo'
  | 'customerId'
  | 'scenarioCode'
  | 'caseId'
>

const DEFAULT_PAGINATION_MODEL: GridPaginationModel = {
  page: 0,
  pageSize: 25,
}

const DEFAULT_SORT_MODEL: GridSortModel = [
  {
    field: 'generatedAt',
    sort: 'desc',
  },
]

const ALERT_SORT_FIELDS: AlertSortField[] = [
  'generatedAt',
  'priorityScore',
  'riskScore',
  'dueAt',
]

function isAlertSortField(
  value: string | undefined,
): value is AlertSortField {
  if (!value) {
    return false
  }

  return ALERT_SORT_FIELDS.includes(
    value as AlertSortField,
  )
}

function normalizeText(
  value: string | undefined,
) {
  const normalized =
    value?.trim()

  if (!normalized) {
    return undefined
  }

  return normalized
}

function normalizeDateTime(
  value: string | undefined,
) {
  const normalized =
    normalizeText(value)

  if (!normalized) {
    return undefined
  }

  if (normalized.length === 16) {
    return `${normalized}:00`
  }

  return normalized
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

function AlertsPage() {
  const { t } = useTranslation()

  const [
    filterDraft,
    setFilterDraft,
  ] = useState<AlertFilterDraft>({})

  const [
    appliedFilters,
    setAppliedFilters,
  ] = useState<AlertFilterDraft>({})

  const [
    paginationModel,
    setPaginationModel,
  ] = useState<GridPaginationModel>(
    DEFAULT_PAGINATION_MODEL,
  )

  const [
    sortModel,
    setSortModel,
  ] = useState<GridSortModel>(
    DEFAULT_SORT_MODEL,
  )

  const activeSort =
    sortModel[0]

  const sortField: AlertSortField =
    isAlertSortField(
      activeSort?.field,
    )
      ? activeSort.field
      : 'generatedAt'

  const sortDirection: AlertSortDirection =
    activeSort?.sort === 'asc'
      ? 'ASC'
      : 'DESC'

  const queryParams =
    useMemo<AlertSearchParams>(
      () => ({
        ...appliedFilters,
        page:
          paginationModel.page,
        size:
          paginationModel.pageSize,
        sort:
          sortField,
        direction:
          sortDirection,
      }),
      [
        appliedFilters,
        paginationModel.page,
        paginationModel.pageSize,
        sortDirection,
        sortField,
      ],
    )

  const alertsQuery =
    useAlertsQuery(
      queryParams,
    )

  const columns =
    useMemo<GridColDef<AlertRecord>[]>(
      () => [
        {
          field: 'alertReference',
          headerName:
            t(
              'alerts.columns.reference',
            ),
          minWidth: 170,
          flex: 1,
          sortable: false,
          renderCell: (params) =>
            params.row.alertReference
            ?? params.row.alertId,
        },
        {
          field: 'title',
          headerName:
            t(
              'alerts.columns.title',
            ),
          minWidth: 220,
          flex: 1.4,
          sortable: false,
          renderCell: (params) =>
            params.row.title
            ?? '—',
        },
        {
          field: 'status',
          headerName:
            t(
              'alerts.columns.status',
            ),
          minWidth: 140,
          flex: 0.8,
          sortable: false,
        },
        {
          field: 'priority',
          headerName:
            t(
              'alerts.columns.priority',
            ),
          minWidth: 130,
          flex: 0.8,
          sortable: false,
        },
        {
          field: 'priorityScore',
          headerName:
            t(
              'alerts.columns.priorityScore',
            ),
          minWidth: 170,
          flex: 0.9,
          sortable: true,
          renderCell: (params) =>
            params.row.priorityScore
            ?? '—',
        },
        {
          field: 'severity',
          headerName:
            t(
              'alerts.columns.severity',
            ),
          minWidth: 130,
          flex: 0.8,
          sortable: false,
          renderCell: (params) =>
            params.row.severity
            ?? '—',
        },
        {
          field: 'riskScore',
          headerName:
            t(
              'alerts.columns.riskScore',
            ),
          minWidth: 160,
          flex: 0.9,
          sortable: true,
          renderCell: (params) =>
            params.row.riskScore
            ?? '—',
        },
        {
          field: 'assignedTeam',
          headerName:
            t(
              'alerts.columns.assignedTeam',
            ),
          minWidth: 170,
          flex: 1,
          sortable: false,
          renderCell: (params) =>
            params.row.assignedTeam
            ?? '—',
        },
        {
          field: 'generatedAt',
          headerName:
            t(
              'alerts.columns.generatedAt',
            ),
          minWidth: 190,
          flex: 1,
          sortable: true,
          renderCell: (params) =>
            formatDateTime(
              params.row.generatedAt,
            ),
        },
        {
          field: 'dueAt',
          headerName:
            t(
              'alerts.columns.dueAt',
            ),
          minWidth: 190,
          flex: 1,
          sortable: true,
          renderCell: (params) =>
            formatDateTime(
              params.row.dueAt,
            ),
        },
      ],
      [t],
    )

  function updateFilter(
    field: keyof AlertFilterDraft,
    value: string,
  ) {
    setFilterDraft(
      (current) => ({
        ...current,
        [field]: value,
      }),
    )
  }

  function applyFilters() {
    setAppliedFilters({
      status:
        normalizeText(
          filterDraft.status,
        ),
      priority:
        normalizeText(
          filterDraft.priority,
        ),
      riskLevel:
        normalizeText(
          filterDraft.riskLevel,
        ),
      assignedTo:
        normalizeText(
          filterDraft.assignedTo,
        ),
      createdFrom:
        normalizeDateTime(
          filterDraft.createdFrom,
        ),
      createdTo:
        normalizeDateTime(
          filterDraft.createdTo,
        ),
      customerId:
        normalizeText(
          filterDraft.customerId,
        ),
      scenarioCode:
        normalizeText(
          filterDraft.scenarioCode,
        ),
      caseId:
        normalizeText(
          filterDraft.caseId,
        ),
    })

    setPaginationModel(
      (current) => ({
        ...current,
        page: 0,
      }),
    )
  }

  function resetFilters() {
    setFilterDraft({})
    setAppliedFilters({})

    setPaginationModel(
      (current) => ({
        ...current,
        page: 0,
      }),
    )
  }

  function handleSortModelChange(
    model: GridSortModel,
  ) {
    const nextSort =
      model[0]

    if (
      nextSort
      && !isAlertSortField(
        nextSort.field,
      )
    ) {
      return
    }

    setSortModel(model)

    setPaginationModel(
      (current) => ({
        ...current,
        page: 0,
      }),
    )
  }

  return (
    <Box>
      <Typography
        component="h2"
        variant="h4"
      >
        {t('navigation.alerts')}
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
            'alerts.filters.title',
          )}
        </Typography>

        <Box
          sx={{
            display: 'grid',
            gridTemplateColumns:
              'repeat(auto-fit, minmax(220px, 1fr))',
            gap: 2,
            mt: 2,
          }}
        >
          <TextField
            label={t(
              'alerts.filters.status',
            )}
            size="small"
            value={
              filterDraft.status
              ?? ''
            }
            onChange={(event) =>
              updateFilter(
                'status',
                event.target.value,
              )
            }
          />

          <TextField
            label={t(
              'alerts.filters.priority',
            )}
            size="small"
            value={
              filterDraft.priority
              ?? ''
            }
            onChange={(event) =>
              updateFilter(
                'priority',
                event.target.value,
              )
            }
          />

          <TextField
            label={t(
              'alerts.filters.riskLevel',
            )}
            size="small"
            value={
              filterDraft.riskLevel
              ?? ''
            }
            onChange={(event) =>
              updateFilter(
                'riskLevel',
                event.target.value,
              )
            }
          />

          <TextField
            label={t(
              'alerts.filters.assignedTo',
            )}
            size="small"
            value={
              filterDraft.assignedTo
              ?? ''
            }
            onChange={(event) =>
              updateFilter(
                'assignedTo',
                event.target.value,
              )
            }
          />

          <TextField
            label={t(
              'alerts.filters.customerId',
            )}
            size="small"
            value={
              filterDraft.customerId
              ?? ''
            }
            onChange={(event) =>
              updateFilter(
                'customerId',
                event.target.value,
              )
            }
          />

          <TextField
            label={t(
              'alerts.filters.scenarioCode',
            )}
            size="small"
            value={
              filterDraft.scenarioCode
              ?? ''
            }
            onChange={(event) =>
              updateFilter(
                'scenarioCode',
                event.target.value,
              )
            }
          />

          <TextField
            label={t(
              'alerts.filters.caseId',
            )}
            size="small"
            value={
              filterDraft.caseId
              ?? ''
            }
            onChange={(event) =>
              updateFilter(
                'caseId',
                event.target.value,
              )
            }
          />

          <TextField
            label={t(
              'alerts.filters.createdFrom',
            )}
            type="datetime-local"
            size="small"
            value={
              filterDraft.createdFrom
              ?? ''
            }
            slotProps={{
              inputLabel: {
                shrink: true,
              },
            }}
            onChange={(event) =>
              updateFilter(
                'createdFrom',
                event.target.value,
              )
            }
          />

          <TextField
            label={t(
              'alerts.filters.createdTo',
            )}
            type="datetime-local"
            size="small"
            value={
              filterDraft.createdTo
              ?? ''
            }
            slotProps={{
              inputLabel: {
                shrink: true,
              },
            }}
            onChange={(event) =>
              updateFilter(
                'createdTo',
                event.target.value,
              )
            }
          />
        </Box>

        <Box
          sx={{
            display: 'flex',
            gap: 1,
            mt: 2,
          }}
        >
          <Button
            variant="contained"
            onClick={
              applyFilters
            }
          >
            {t(
              'alerts.filters.apply',
            )}
          </Button>

          <Button
            variant="outlined"
            onClick={
              resetFilters
            }
          >
            {t(
              'alerts.filters.reset',
            )}
          </Button>
        </Box>
      </Paper>

      <Box
        sx={{
          mt: 3,
        }}
      >
        <Typography
          component="h3"
          variant="h6"
          sx={{
            mb: 2,
          }}
        >
          {t(
            'alerts.list.title',
          )}
        </Typography>

        {alertsQuery.isError && (
          <MuiAlert
            severity="error"
            sx={{
              mb: 2,
            }}
          >
            {t(
              'alerts.list.error',
            )}
          </MuiAlert>
        )}

        <Paper>
          <Box
            sx={{
              height: 650,
              width: '100%',
            }}
          >
            <DataGrid<AlertRecord>
              rows={
                alertsQuery.data
                  ?.content
                ?? []
              }
              columns={columns}
              getRowId={(row) =>
                row.alertId
              }
              loading={
                alertsQuery.isFetching
              }
              rowCount={
                alertsQuery.data
                  ?.totalElements
                ?? 0
              }
              pagination
              paginationMode="server"
              paginationModel={
                paginationModel
              }
              onPaginationModelChange={
                setPaginationModel
              }
              pageSizeOptions={[
                25,
                50,
                100,
              ]}
              sortingMode="server"
              sortModel={
                sortModel
              }
              onSortModelChange={
                handleSortModelChange
              }
              disableRowSelectionOnClick
              localeText={{
                noRowsLabel:
                  t(
                    'alerts.list.noRows',
                  ),
              }}
              sx={{
                border: 0,
              }}
            />
          </Box>
        </Paper>
      </Box>
    </Box>
  )
}

export default AlertsPage
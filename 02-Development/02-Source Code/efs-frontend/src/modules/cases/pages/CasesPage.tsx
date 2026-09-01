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

import { useCasesQuery } from '../hooks/useCasesQuery'
import type {
  Case as CaseRecord,
  CaseSearchParams,
  CaseSortDirection,
  CaseSortField,
} from '../types/case'

type CaseFilterDraft = Pick<
  CaseSearchParams,
  | 'status'
  | 'priority'
  | 'assignedUser'
  | 'assignedTeam'
>

const DEFAULT_PAGINATION_MODEL: GridPaginationModel = {
  page: 0,
  pageSize: 25,
}

const DEFAULT_SORT_MODEL: GridSortModel = [
  {
    field: 'createdAt',
    sort: 'desc',
  },
]

const CASE_SORT_FIELDS: CaseSortField[] = [
  'createdAt',
]

function isCaseSortField(
  value: string | undefined,
): value is CaseSortField {
  if (!value) {
    return false
  }

  return CASE_SORT_FIELDS.includes(
    value as CaseSortField,
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

function CasesPage() {
  const { t } = useTranslation()

  const [
    filterDraft,
    setFilterDraft,
  ] = useState<CaseFilterDraft>({})

  const [
    appliedFilters,
    setAppliedFilters,
  ] = useState<CaseFilterDraft>({})

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

  const sortField: CaseSortField =
    isCaseSortField(
      activeSort?.field,
    )
      ? activeSort.field
      : 'createdAt'

  const sortDirection: CaseSortDirection =
    activeSort?.sort === 'asc'
      ? 'ASC'
      : 'DESC'

  const queryParams =
    useMemo<CaseSearchParams>(
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

  const casesQuery =
    useCasesQuery(
      queryParams,
    )

  const columns =
    useMemo<GridColDef<CaseRecord>[]>(
      () => [
        {
          field: 'caseNumber',
          headerName:
            t(
              'cases.columns.caseNumber',
            ),
          minWidth: 180,
          flex: 1,
          sortable: false,
        },
        {
          field: 'caseType',
          headerName:
            t(
              'cases.columns.caseType',
            ),
          minWidth: 190,
          flex: 1,
          sortable: false,
        },
        {
          field: 'currentStatus',
          headerName:
            t(
              'cases.columns.status',
            ),
          minWidth: 150,
          flex: 0.8,
          sortable: false,
        },
        {
          field: 'priority',
          headerName:
            t(
              'cases.columns.priority',
            ),
          minWidth: 130,
          flex: 0.8,
          sortable: false,
        },
        {
          field: 'severity',
          headerName:
            t(
              'cases.columns.severity',
            ),
          minWidth: 130,
          flex: 0.8,
          sortable: false,
          renderCell: (params) =>
            params.row.severity
            ?? '—',
        },
        {
          field: 'assignedTeam',
          headerName:
            t(
              'cases.columns.assignedTeam',
            ),
          minWidth: 190,
          flex: 1,
          sortable: false,
          renderCell: (params) =>
            params.row.assignedTeam
            ?? '—',
        },
        {
          field: 'assignedUser',
          headerName:
            t(
              'cases.columns.assignedUser',
            ),
          minWidth: 240,
          flex: 1.2,
          sortable: false,
          renderCell: (params) =>
            params.row.assignedUser
            ?? '—',
        },
        {
          field: 'createdAt',
          headerName:
            t(
              'cases.columns.createdAt',
            ),
          minWidth: 190,
          flex: 1,
          sortable: true,
          renderCell: (params) =>
            formatDateTime(
              params.row.createdAt,
            ),
        },
        {
          field: 'dueDate',
          headerName:
            t(
              'cases.columns.dueDate',
            ),
          minWidth: 190,
          flex: 1,
          sortable: false,
          renderCell: (params) =>
            formatDateTime(
              params.row.dueDate,
            ),
        },
      ],
      [t],
    )

  function updateFilter(
    field: keyof CaseFilterDraft,
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
      assignedUser:
        normalizeText(
          filterDraft.assignedUser,
        ),
      assignedTeam:
        normalizeText(
          filterDraft.assignedTeam,
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
      && !isCaseSortField(
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
        {t('navigation.cases')}
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
            'cases.filters.title',
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
              'cases.filters.status',
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
              'cases.filters.priority',
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
              'cases.filters.assignedUser',
            )}
            size="small"
            value={
              filterDraft.assignedUser
              ?? ''
            }
            onChange={(event) =>
              updateFilter(
                'assignedUser',
                event.target.value,
              )
            }
          />

          <TextField
            label={t(
              'cases.filters.assignedTeam',
            )}
            size="small"
            value={
              filterDraft.assignedTeam
              ?? ''
            }
            onChange={(event) =>
              updateFilter(
                'assignedTeam',
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
              'cases.filters.apply',
            )}
          </Button>

          <Button
            variant="outlined"
            onClick={
              resetFilters
            }
          >
            {t(
              'cases.filters.reset',
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
            'cases.list.title',
          )}
        </Typography>

        {casesQuery.isError && (
          <MuiAlert
            severity="error"
            sx={{
              mb: 2,
            }}
          >
            {t(
              'cases.list.error',
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
            <DataGrid<CaseRecord>
              rows={
                casesQuery.data
                  ?.content
                ?? []
              }
              columns={columns}
              getRowId={(row) =>
                row.caseId
              }
              loading={
                casesQuery.isFetching
              }
              rowCount={
                casesQuery.data
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
                    'cases.list.noRows',
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

export default CasesPage
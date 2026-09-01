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

import { useDetectionScenariosQuery } from '../hooks/useDetectionScenariosQuery'
import type {
  DetectionScenario,
  DetectionScenarioSearchParams,
  DetectionScenarioSortDirection,
  DetectionScenarioSortField,
} from '../types/detectionScenario'

type DetectionScenarioFilterDraft = Pick<
  DetectionScenarioSearchParams,
  | 'scenarioCode'
  | 'category'
  | 'status'
  | 'criticality'
  | 'owner'
>

const DEFAULT_PAGINATION_MODEL: GridPaginationModel = {
  page: 0,
  pageSize: 25,
}

const DEFAULT_SORT_MODEL: GridSortModel = [
  {
    field: 'scenarioName',
    sort: 'asc',
  },
]

const DETECTION_SCENARIO_SORT_FIELDS:
  DetectionScenarioSortField[] = [
    'scenarioName',
  ]

function isDetectionScenarioSortField(
  value: string | undefined,
): value is DetectionScenarioSortField {
  if (!value) {
    return false
  }

  return DETECTION_SCENARIO_SORT_FIELDS.includes(
    value as DetectionScenarioSortField,
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

function DetectionPage() {
  const { t } = useTranslation()

  const [
    filterDraft,
    setFilterDraft,
  ] = useState<DetectionScenarioFilterDraft>({})

  const [
    appliedFilters,
    setAppliedFilters,
  ] = useState<DetectionScenarioFilterDraft>({})

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

  const sortField: DetectionScenarioSortField =
    isDetectionScenarioSortField(
      activeSort?.field,
    )
      ? activeSort.field
      : 'scenarioName'

  const sortDirection:
    DetectionScenarioSortDirection =
    activeSort?.sort === 'desc'
      ? 'DESC'
      : 'ASC'

  const queryParams =
    useMemo<DetectionScenarioSearchParams>(
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

  const detectionScenariosQuery =
    useDetectionScenariosQuery(
      queryParams,
    )

  const columns =
    useMemo<GridColDef<DetectionScenario>[]>(
      () => [
        {
          field: 'scenarioCode',
          headerName:
            t(
              'detection.columns.scenarioCode',
            ),
          minWidth: 170,
          flex: 0.9,
          sortable: false,
        },
        {
          field: 'scenarioName',
          headerName:
            t(
              'detection.columns.scenarioName',
            ),
          minWidth: 240,
          flex: 1.3,
          sortable: true,
        },
        {
          field: 'category',
          headerName:
            t(
              'detection.columns.category',
            ),
          minWidth: 150,
          flex: 0.8,
          sortable: false,
        },
        {
          field: 'criticality',
          headerName:
            t(
              'detection.columns.criticality',
            ),
          minWidth: 150,
          flex: 0.8,
          sortable: false,
          renderCell: (params) =>
            params.row.criticality
            ?? '—',
        },
        {
          field: 'status',
          headerName:
            t(
              'detection.columns.status',
            ),
          minWidth: 140,
          flex: 0.8,
          sortable: false,
        },
        {
          field: 'owner',
          headerName:
            t(
              'detection.columns.owner',
            ),
          minWidth: 180,
          flex: 0.9,
          sortable: false,
          renderCell: (params) =>
            params.row.owner
            ?? '—',
        },
        {
          field: 'version',
          headerName:
            t(
              'detection.columns.version',
            ),
          minWidth: 110,
          flex: 0.6,
          sortable: false,
        },
        {
          field: 'minimumConfidence',
          headerName:
            t(
              'detection.columns.minimumConfidence',
            ),
          minWidth: 180,
          flex: 0.9,
          sortable: false,
          renderCell: (params) =>
            params.row.minimumConfidence
            ?? '—',
        },
        {
          field: 'updatedAt',
          headerName:
            t(
              'detection.columns.updatedAt',
            ),
          minWidth: 190,
          flex: 1,
          sortable: false,
          renderCell: (params) =>
            formatDateTime(
              params.row.updatedAt,
            ),
        },
      ],
      [t],
    )

  function updateFilter(
    field: keyof DetectionScenarioFilterDraft,
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
      scenarioCode:
        normalizeText(
          filterDraft.scenarioCode,
        ),
      category:
        normalizeText(
          filterDraft.category,
        ),
      status:
        normalizeText(
          filterDraft.status,
        ),
      criticality:
        normalizeText(
          filterDraft.criticality,
        ),
      owner:
        normalizeText(
          filterDraft.owner,
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
      && !isDetectionScenarioSortField(
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
        {t('navigation.detection')}
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
            'detection.filters.title',
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
              'detection.filters.scenarioCode',
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
              'detection.filters.category',
            )}
            size="small"
            value={
              filterDraft.category
              ?? ''
            }
            onChange={(event) =>
              updateFilter(
                'category',
                event.target.value,
              )
            }
          />

          <TextField
            label={t(
              'detection.filters.status',
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
              'detection.filters.criticality',
            )}
            size="small"
            value={
              filterDraft.criticality
              ?? ''
            }
            onChange={(event) =>
              updateFilter(
                'criticality',
                event.target.value,
              )
            }
          />

          <TextField
            label={t(
              'detection.filters.owner',
            )}
            size="small"
            value={
              filterDraft.owner
              ?? ''
            }
            onChange={(event) =>
              updateFilter(
                'owner',
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
              'detection.filters.apply',
            )}
          </Button>

          <Button
            variant="outlined"
            onClick={
              resetFilters
            }
          >
            {t(
              'detection.filters.reset',
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
            'detection.list.title',
          )}
        </Typography>

        {detectionScenariosQuery.isError && (
          <MuiAlert
            severity="error"
            sx={{
              mb: 2,
            }}
          >
            {t(
              'detection.list.error',
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
            <DataGrid<DetectionScenario>
              rows={
                detectionScenariosQuery.data
                  ?.content
                ?? []
              }
              columns={columns}
              getRowId={(row) =>
                row.scenarioId
              }
              loading={
                detectionScenariosQuery.isFetching
              }
              rowCount={
                detectionScenariosQuery.data
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
                    'detection.list.noRows',
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

export default DetectionPage
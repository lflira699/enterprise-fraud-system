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

import { useRiskAssessmentsQuery } from '../hooks/useRiskAssessmentsQuery'
import type {
  RiskAssessment,
  RiskAssessmentSearchParams,
  RiskAssessmentSortDirection,
  RiskAssessmentSortField,
} from '../types/riskAssessment'

type RiskAssessmentFilterDraft = Pick<
  RiskAssessmentSearchParams,
  | 'riskLevel'
  | 'assessmentResult'
>

const DEFAULT_PAGINATION_MODEL: GridPaginationModel = {
  page: 0,
  pageSize: 25,
}

const DEFAULT_SORT_MODEL: GridSortModel = [
  {
    field: 'assessmentTimestamp',
    sort: 'desc',
  },
]

const RISK_ASSESSMENT_SORT_FIELDS:
  RiskAssessmentSortField[] = [
    'assessmentTimestamp',
  ]

function isRiskAssessmentSortField(
  value: string | undefined,
): value is RiskAssessmentSortField {
  if (!value) {
    return false
  }

  return RISK_ASSESSMENT_SORT_FIELDS.includes(
    value as RiskAssessmentSortField,
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

function RiskPage() {
  const { t } = useTranslation()

  const [
    filterDraft,
    setFilterDraft,
  ] = useState<RiskAssessmentFilterDraft>({})

  const [
    appliedFilters,
    setAppliedFilters,
  ] = useState<RiskAssessmentFilterDraft>({})

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

  const sortField: RiskAssessmentSortField =
    isRiskAssessmentSortField(
      activeSort?.field,
    )
      ? activeSort.field
      : 'assessmentTimestamp'

  const sortDirection:
    RiskAssessmentSortDirection =
    activeSort?.sort === 'asc'
      ? 'ASC'
      : 'DESC'

  const queryParams =
    useMemo<RiskAssessmentSearchParams>(
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

  const riskAssessmentsQuery =
    useRiskAssessmentsQuery(
      queryParams,
    )

  const columns =
    useMemo<GridColDef<RiskAssessment>[]>(
      () => [
        {
          field: 'transactionId',
          headerName:
            t(
              'risk.columns.transactionId',
            ),
          minWidth: 240,
          flex: 1.2,
          sortable: false,
        },
        {
          field: 'assessmentType',
          headerName:
            t(
              'risk.columns.assessmentType',
            ),
          minWidth: 180,
          flex: 1,
          sortable: false,
        },
        {
          field: 'assessmentStage',
          headerName:
            t(
              'risk.columns.assessmentStage',
            ),
          minWidth: 170,
          flex: 0.9,
          sortable: false,
        },
        {
          field: 'overallRiskScore',
          headerName:
            t(
              'risk.columns.overallRiskScore',
            ),
          minWidth: 190,
          flex: 0.9,
          sortable: false,
        },
        {
          field: 'riskLevel',
          headerName:
            t(
              'risk.columns.riskLevel',
            ),
          minWidth: 150,
          flex: 0.8,
          sortable: false,
        },
        {
          field: 'riskCategory',
          headerName:
            t(
              'risk.columns.riskCategory',
            ),
          minWidth: 170,
          flex: 0.9,
          sortable: false,
          renderCell: (params) =>
            params.row.riskCategory
            ?? '—',
        },
        {
          field: 'assessmentResult',
          headerName:
            t(
              'risk.columns.assessmentResult',
            ),
          minWidth: 180,
          flex: 0.9,
          sortable: false,
        },
        {
          field: 'confidenceScore',
          headerName:
            t(
              'risk.columns.confidenceScore',
            ),
          minWidth: 190,
          flex: 0.9,
          sortable: false,
          renderCell: (params) =>
            params.row.confidenceScore
            ?? '—',
        },
        {
          field: 'modelName',
          headerName:
            t(
              'risk.columns.modelName',
            ),
          minWidth: 170,
          flex: 0.9,
          sortable: false,
          renderCell: (params) =>
            params.row.modelName
            ?? '—',
        },
        {
          field: 'assessmentTimestamp',
          headerName:
            t(
              'risk.columns.assessmentTimestamp',
            ),
          minWidth: 200,
          flex: 1,
          sortable: true,
          renderCell: (params) =>
            formatDateTime(
              params.row.assessmentTimestamp,
            ),
        },
      ],
      [t],
    )

  function updateFilter(
    field: keyof RiskAssessmentFilterDraft,
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
      riskLevel:
        normalizeText(
          filterDraft.riskLevel,
        ),
      assessmentResult:
        normalizeText(
          filterDraft.assessmentResult,
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
      && !isRiskAssessmentSortField(
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
        {t('navigation.risk')}
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
            'risk.filters.title',
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
              'risk.filters.riskLevel',
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
              'risk.filters.assessmentResult',
            )}
            size="small"
            value={
              filterDraft.assessmentResult
              ?? ''
            }
            onChange={(event) =>
              updateFilter(
                'assessmentResult',
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
              'risk.filters.apply',
            )}
          </Button>

          <Button
            variant="outlined"
            onClick={
              resetFilters
            }
          >
            {t(
              'risk.filters.reset',
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
            'risk.list.title',
          )}
        </Typography>

        {riskAssessmentsQuery.isError && (
          <MuiAlert
            severity="error"
            sx={{
              mb: 2,
            }}
          >
            {t(
              'risk.list.error',
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
            <DataGrid<RiskAssessment>
              rows={
                riskAssessmentsQuery.data
                  ?.content
                ?? []
              }
              columns={columns}
              getRowId={(row) =>
                row.riskAssessmentId
              }
              loading={
                riskAssessmentsQuery.isFetching
              }
              rowCount={
                riskAssessmentsQuery.data
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
                    'risk.list.noRows',
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

export default RiskPage
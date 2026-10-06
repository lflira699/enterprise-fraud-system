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

import { useEventsQuery } from '../hooks/useEventsQuery'
import type {
  FraudEvent,
  FraudEventSearchParams,
  FraudEventSortDirection,
  FraudEventSortField,
} from '../types/fraudEvent'

type EventFilterDraft = Pick<
  FraudEventSearchParams,
  | 'transactionId'
  | 'eventType'
  | 'sourceType'
  | 'sourceReference'
  | 'correlationId'
  | 'occurredFrom'
  | 'occurredTo'
  | 'receivedFrom'
  | 'receivedTo'
>

const DEFAULT_PAGINATION_MODEL: GridPaginationModel = {
  page: 0,
  pageSize: 25,
}

const DEFAULT_SORT_MODEL: GridSortModel = [
  {
    field: 'occurredAt',
    sort: 'desc',
  },
]

const EVENT_SORT_FIELDS: FraudEventSortField[] = [
  'occurredAt',
]

function isEventSortField(
  value: string | undefined,
): value is FraudEventSortField {
  if (!value) {
    return false
  }

  return EVENT_SORT_FIELDS.includes(
    value as FraudEventSortField,
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

function EventsPage() {
  const { t } = useTranslation()

  const [
    filterDraft,
    setFilterDraft,
  ] = useState<EventFilterDraft>({})

  const [
    appliedFilters,
    setAppliedFilters,
  ] = useState<EventFilterDraft>({})

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

  const sortField: FraudEventSortField =
    isEventSortField(
      activeSort?.field,
    )
      ? activeSort.field
      : 'occurredAt'

  const sortDirection: FraudEventSortDirection =
    activeSort?.sort === 'asc'
      ? 'ASC'
      : 'DESC'

  const queryParams =
    useMemo<FraudEventSearchParams>(
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

  const eventsQuery =
    useEventsQuery(
      queryParams,
    )

  const columns =
    useMemo<GridColDef<FraudEvent>[]>(
      () => [
        {
          field: 'fraudEventId',
          headerName:
            t('events.columns.fraudEventId'),
          minWidth: 220,
          flex: 1.2,
          sortable: false,
        },
        {
          field: 'eventType',
          headerName:
            t('events.columns.eventType'),
          minWidth: 170,
          flex: 1,
          sortable: false,
        },
        {
          field: 'sourceType',
          headerName:
            t('events.columns.sourceType'),
          minWidth: 160,
          flex: 1,
          sortable: false,
        },
        {
          field: 'sourceReference',
          headerName:
            t('events.columns.sourceReference'),
          minWidth: 190,
          flex: 1,
          sortable: false,
          renderCell: (params) =>
            params.row.sourceReference
            ?? '—',
        },
        {
          field: 'transactionId',
          headerName:
            t('events.columns.transactionId'),
          minWidth: 220,
          flex: 1.1,
          sortable: false,
          renderCell: (params) =>
            params.row.transactionId
            ?? '—',
        },
        {
          field: 'correlationId',
          headerName:
            t('events.columns.correlationId'),
          minWidth: 220,
          flex: 1.1,
          sortable: false,
        },
        {
          field: 'occurredAt',
          headerName:
            t('events.columns.occurredAt'),
          minWidth: 190,
          flex: 1,
          sortable: true,
          renderCell: (params) =>
            formatDateTime(
              params.row.occurredAt,
            ),
        },
        {
          field: 'receivedAt',
          headerName:
            t('events.columns.receivedAt'),
          minWidth: 190,
          flex: 1,
          sortable: false,
          renderCell: (params) =>
            formatDateTime(
              params.row.receivedAt,
            ),
        },
      ],
      [t],
    )

  function updateFilter(
    field: keyof EventFilterDraft,
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
      transactionId:
        normalizeText(
          filterDraft.transactionId,
        ),
      eventType:
        normalizeText(
          filterDraft.eventType,
        ),
      sourceType:
        normalizeText(
          filterDraft.sourceType,
        ),
      sourceReference:
        normalizeText(
          filterDraft.sourceReference,
        ),
      correlationId:
        normalizeText(
          filterDraft.correlationId,
        ),
      occurredFrom:
        normalizeDateTime(
          filterDraft.occurredFrom,
        ),
      occurredTo:
        normalizeDateTime(
          filterDraft.occurredTo,
        ),
      receivedFrom:
        normalizeDateTime(
          filterDraft.receivedFrom,
        ),
      receivedTo:
        normalizeDateTime(
          filterDraft.receivedTo,
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
      && !isEventSortField(
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
        {t('navigation.events')}
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
          {t('events.filters.title')}
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
            label={t('events.filters.transactionId')}
            size="small"
            value={filterDraft.transactionId ?? ''}
            onChange={(event) =>
              updateFilter(
                'transactionId',
                event.target.value,
              )
            }
          />

          <TextField
            label={t('events.filters.eventType')}
            size="small"
            value={filterDraft.eventType ?? ''}
            onChange={(event) =>
              updateFilter(
                'eventType',
                event.target.value,
              )
            }
          />

          <TextField
            label={t('events.filters.sourceType')}
            size="small"
            value={filterDraft.sourceType ?? ''}
            onChange={(event) =>
              updateFilter(
                'sourceType',
                event.target.value,
              )
            }
          />

          <TextField
            label={t('events.filters.sourceReference')}
            size="small"
            value={filterDraft.sourceReference ?? ''}
            onChange={(event) =>
              updateFilter(
                'sourceReference',
                event.target.value,
              )
            }
          />

          <TextField
            label={t('events.filters.correlationId')}
            size="small"
            value={filterDraft.correlationId ?? ''}
            onChange={(event) =>
              updateFilter(
                'correlationId',
                event.target.value,
              )
            }
          />

          <TextField
            label={t('events.filters.occurredFrom')}
            size="small"
            type="datetime-local"
            slotProps={{
              inputLabel: {
                shrink: true,
              },
            }}
            value={filterDraft.occurredFrom ?? ''}
            onChange={(event) =>
              updateFilter(
                'occurredFrom',
                event.target.value,
              )
            }
          />

          <TextField
            label={t('events.filters.occurredTo')}
            size="small"
            type="datetime-local"
            slotProps={{
              inputLabel: {
                shrink: true,
              },
            }}
            value={filterDraft.occurredTo ?? ''}
            onChange={(event) =>
              updateFilter(
                'occurredTo',
                event.target.value,
              )
            }
          />

          <TextField
            label={t('events.filters.receivedFrom')}
            size="small"
            type="datetime-local"
            slotProps={{
              inputLabel: {
                shrink: true,
              },
            }}
            value={filterDraft.receivedFrom ?? ''}
            onChange={(event) =>
              updateFilter(
                'receivedFrom',
                event.target.value,
              )
            }
          />

          <TextField
            label={t('events.filters.receivedTo')}
            size="small"
            type="datetime-local"
            slotProps={{
              inputLabel: {
                shrink: true,
              },
            }}
            value={filterDraft.receivedTo ?? ''}
            onChange={(event) =>
              updateFilter(
                'receivedTo',
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
            onClick={applyFilters}
          >
            {t('events.filters.apply')}
          </Button>

          <Button
            variant="outlined"
            onClick={resetFilters}
          >
            {t('events.filters.reset')}
          </Button>
        </Box>
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
          sx={{ mb: 2 }}
        >
          {t('events.list.title')}
        </Typography>

        {eventsQuery.isError && (
          <MuiAlert
            severity="error"
            sx={{ mb: 2 }}
          >
            {t('events.list.error')}
          </MuiAlert>
        )}

        <DataGrid
          autoHeight
          rows={
            eventsQuery.data?.content
            ?? []
          }
          columns={columns}
          getRowId={(row) =>
            row.fraudEventId
          }
          rowCount={
            eventsQuery.data?.totalElements
            ?? 0
          }
          loading={
            eventsQuery.isLoading
          }
          paginationMode="server"
          sortingMode="server"
          paginationModel={
            paginationModel
          }
          onPaginationModelChange={
            setPaginationModel
          }
          sortModel={sortModel}
          onSortModelChange={
            handleSortModelChange
          }
          pageSizeOptions={[
            25,
            50,
            100,
          ]}
          disableRowSelectionOnClick
          localeText={{
            noRowsLabel:
              t('events.list.noRows'),
          }}
        />
      </Paper>
    </Box>
  )
}

export default EventsPage

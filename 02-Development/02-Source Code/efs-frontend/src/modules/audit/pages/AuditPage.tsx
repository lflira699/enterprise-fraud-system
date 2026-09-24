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

import { useAuditEventsQuery } from '../hooks/useAuditEventsQuery'
import type {
  AuditEvent,
  AuditEventSearchParams,
  AuditSortDirection,
} from '../types/auditEvent'

type AuditFilterDraft = Pick<
  AuditEventSearchParams,
  | 'userId'
  | 'from'
  | 'to'
  | 'entityType'
  | 'entityId'
  | 'action'
>

const DEFAULT_PAGINATION_MODEL: GridPaginationModel = {
  page: 0,
  pageSize: 25,
}

const DEFAULT_SORT_MODEL: GridSortModel = [
  {
    field: 'eventTimestamp',
    sort: 'desc',
  },
]

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

function AuditPage() {
  const { t } = useTranslation()

  const [
    filterDraft,
    setFilterDraft,
  ] = useState<AuditFilterDraft>({})

  const [
    appliedFilters,
    setAppliedFilters,
  ] = useState<AuditFilterDraft>({})

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

  const sortDirection: AuditSortDirection =
    sortModel[0]?.sort === 'asc'
      ? 'ASC'
      : 'DESC'

  const queryParams =
    useMemo<AuditEventSearchParams>(
      () => ({
        ...appliedFilters,
        page:
          paginationModel.page,
        size:
          paginationModel.pageSize,
        sort:
          'eventTimestamp',
        direction:
          sortDirection,
      }),
      [
        appliedFilters,
        paginationModel.page,
        paginationModel.pageSize,
        sortDirection,
      ],
    )

  const auditQuery =
    useAuditEventsQuery(
      queryParams,
    )

  const columns =
    useMemo<GridColDef<AuditEvent>[]>(
      () => [
        {
          field: 'eventTimestamp',
          headerName:
            t('auditLog.columns.eventTimestamp'),
          minWidth: 180,
          flex: 1,
          valueFormatter:
            (value) =>
              formatDateTime(
                value as string | null,
              ),
        },
        {
          field: 'eventType',
          headerName:
            t('auditLog.columns.eventType'),
          minWidth: 180,
          flex: 1,
          sortable: false,
        },
        {
          field: 'entityType',
          headerName:
            t('auditLog.columns.entityType'),
          minWidth: 150,
          flex: 1,
          sortable: false,
        },
        {
          field: 'action',
          headerName:
            t('auditLog.columns.action'),
          minWidth: 130,
          flex: 1,
          sortable: false,
        },
        {
          field: 'sourceComponent',
          headerName:
            t('auditLog.columns.sourceComponent'),
          minWidth: 160,
          flex: 1,
          sortable: false,
        },
        {
          field: 'eventResult',
          headerName:
            t('auditLog.columns.eventResult'),
          minWidth: 130,
          flex: 1,
          sortable: false,
        },
        {
          field: 'userId',
          headerName:
            t('auditLog.columns.userId'),
          minWidth: 220,
          flex: 1,
          sortable: false,
        },
      ],
      [t],
    )

  function applyFilters() {
    setAppliedFilters({
      userId:
        normalizeText(
          filterDraft.userId,
        ),
      from:
        normalizeText(
          filterDraft.from,
        ),
      to:
        normalizeText(
          filterDraft.to,
        ),
      entityType:
        normalizeText(
          filterDraft.entityType,
        ),
      entityId:
        normalizeText(
          filterDraft.entityId,
        ),
      action:
        normalizeText(
          filterDraft.action,
        ),
    })

    setPaginationModel(
      DEFAULT_PAGINATION_MODEL,
    )
  }

  function clearFilters() {
    setFilterDraft({})
    setAppliedFilters({})
    setPaginationModel(
      DEFAULT_PAGINATION_MODEL,
    )
  }

  return (
    <Box>
      <Typography
        component="h1"
        variant="h4"
        sx={{ mb: 2 }}
      >
        {t('auditLog.title')}
      </Typography>

      <Paper
        sx={{
          p: 2,
          mb: 2,
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
          <TextField
            label={t('auditLog.filters.userId')}
            value={filterDraft.userId ?? ''}
            onChange={(event) => {
              setFilterDraft(
                (current) => ({
                  ...current,
                  userId:
                    event.target.value,
                }),
              )
            }}
          />

          <TextField
            label={t('auditLog.filters.from')}
            type="datetime-local"
            value={filterDraft.from ?? ''}
            slotProps={{ inputLabel: { shrink: true } }}
            onChange={(event) => {
              setFilterDraft(
                (current) => ({
                  ...current,
                  from:
                    event.target.value,
                }),
              )
            }}
          />

          <TextField
            label={t('auditLog.filters.to')}
            type="datetime-local"
            value={filterDraft.to ?? ''}
            slotProps={{ inputLabel: { shrink: true } }}
            onChange={(event) => {
              setFilterDraft(
                (current) => ({
                  ...current,
                  to:
                    event.target.value,
                }),
              )
            }}
          />

          <TextField
            label={t('auditLog.filters.entityType')}
            value={filterDraft.entityType ?? ''}
            onChange={(event) => {
              setFilterDraft(
                (current) => ({
                  ...current,
                  entityType:
                    event.target.value,
                }),
              )
            }}
          />

          <TextField
            label={t('auditLog.filters.entityId')}
            value={filterDraft.entityId ?? ''}
            onChange={(event) => {
              setFilterDraft(
                (current) => ({
                  ...current,
                  entityId:
                    event.target.value,
                }),
              )
            }}
          />

          <TextField
            label={t('auditLog.filters.action')}
            value={filterDraft.action ?? ''}
            onChange={(event) => {
              setFilterDraft(
                (current) => ({
                  ...current,
                  action:
                    event.target.value,
                }),
              )
            }}
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
            {t('auditLog.filters.apply')}
          </Button>

          <Button
            variant="outlined"
            onClick={clearFilters}
          >
            {t('auditLog.filters.clear')}
          </Button>
        </Box>
      </Paper>

      {auditQuery.isError
        ? (
          <MuiAlert
            severity="error"
            sx={{ mb: 2 }}
          >
            {t('auditLog.error')}
          </MuiAlert>
        )
        : null}

      <Paper>
        <DataGrid
          autoHeight
          rows={
            auditQuery.data?.content
            ?? []
          }
          columns={columns}
          getRowId={
            (row) =>
              row.auditEventId
          }
          rowCount={
            auditQuery.data
              ?.totalElements
            ?? 0
          }
          loading={
            auditQuery.isFetching
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
            (model) => {
              const first =
                model[0]

              if (
                first
                && first.field
                  === 'eventTimestamp'
              ) {
                setSortModel(model)
                return
              }

              setSortModel(
                DEFAULT_SORT_MODEL,
              )
            }
          }
          pageSizeOptions={[
            25,
            50,
            100,
          ]}
          disableRowSelectionOnClick
          localeText={{
            noRowsLabel:
              t('auditLog.noRows'),
          }}
        />
      </Paper>
    </Box>
  )
}

export default AuditPage
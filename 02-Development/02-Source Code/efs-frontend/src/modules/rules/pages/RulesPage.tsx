import {
  useMemo,
} from 'react'
import {
  Alert as MuiAlert,
  Box,
  Paper,
  Typography,
} from '@mui/material'
import {
  DataGrid,
  type GridColDef,
} from '@mui/x-data-grid'
import { useTranslation } from 'react-i18next'

import { useRulesQuery } from '../hooks/useRulesQuery'
import type { Rule } from '../types/rule'

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

function RulesPage() {
  const { t } = useTranslation()

  const rulesQuery =
    useRulesQuery()

  const columns =
    useMemo<GridColDef<Rule>[]>(
      () => [
        {
          field: 'ruleCode',
          headerName:
            t(
              'rules.columns.ruleCode',
            ),
          minWidth: 170,
          flex: 0.9,
          sortable: false,
        },
        {
          field: 'ruleName',
          headerName:
            t(
              'rules.columns.ruleName',
            ),
          minWidth: 240,
          flex: 1.3,
          sortable: false,
        },
        {
          field: 'category',
          headerName:
            t(
              'rules.columns.category',
            ),
          minWidth: 150,
          flex: 0.8,
          sortable: false,
        },
        {
          field: 'severity',
          headerName:
            t(
              'rules.columns.severity',
            ),
          minWidth: 140,
          flex: 0.7,
          sortable: false,
        },
        {
          field: 'priority',
          headerName:
            t(
              'rules.columns.priority',
            ),
          minWidth: 120,
          flex: 0.6,
          sortable: false,
          renderCell: (params) =>
            params.row.priority
            ?? '—',
        },
        {
          field: 'ownerTeam',
          headerName:
            t(
              'rules.columns.ownerTeam',
            ),
          minWidth: 180,
          flex: 0.9,
          sortable: false,
          renderCell: (params) =>
            params.row.ownerTeam
            ?? '—',
        },
        {
          field: 'currentVersion',
          headerName:
            t(
              'rules.columns.currentVersion',
            ),
          minWidth: 130,
          flex: 0.6,
          sortable: false,
        },
        {
          field: 'status',
          headerName:
            t(
              'rules.columns.status',
            ),
          minWidth: 140,
          flex: 0.7,
          sortable: false,
        },
        {
          field: 'updatedAt',
          headerName:
            t(
              'rules.columns.updatedAt',
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

  return (
    <Box>
      <Typography
        component="h2"
        variant="h4"
      >
        {t('navigation.rules')}
      </Typography>

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
            'rules.list.title',
          )}
        </Typography>

        {rulesQuery.isError && (
          <MuiAlert
            severity="error"
            sx={{
              mb: 2,
            }}
          >
            {t(
              'rules.list.error',
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
            <DataGrid<Rule>
              rows={
                rulesQuery.data
                ?? []
              }
              columns={columns}
              getRowId={(row) =>
                row.ruleId
              }
              loading={
                rulesQuery.isFetching
              }
              disableRowSelectionOnClick
              localeText={{
                noRowsLabel:
                  t(
                    'rules.list.noRows',
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

export default RulesPage
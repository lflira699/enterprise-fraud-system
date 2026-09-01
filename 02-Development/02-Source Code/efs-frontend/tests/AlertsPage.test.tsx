import {
  cleanup,
  fireEvent,
  render,
  screen,
  waitFor,
} from '@testing-library/react'
import {
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import '../src/configuration/i18n'
import { useAlertsQuery } from '../src/modules/alerts/hooks/useAlertsQuery'
import AlertsPage from '../src/modules/alerts/pages/AlertsPage'

vi.mock(
  '../src/modules/alerts/hooks/useAlertsQuery',
  () => ({
    useAlertsQuery: vi.fn(),
  }),
)

vi.mock(
  '@mui/x-data-grid',
  () => ({
    DataGrid: (
      props: {
        onPaginationModelChange?: (
          model: {
            page: number
            pageSize: number
          },
        ) => void
        onSortModelChange?: (
          model: Array<{
            field: string
            sort: 'asc' | 'desc' | null
          }>,
        ) => void
      },
    ) => (
      <div>
        <button
          type="button"
          onClick={() =>
            props.onPaginationModelChange?.({
              page: 1,
              pageSize: 50,
            })
          }
        >
          Cambiar página
        </button>

        <button
          type="button"
          onClick={() =>
            props.onSortModelChange?.([
              {
                field: 'riskScore',
                sort: 'asc',
              },
            ])
          }
        >
          Ordenar por riesgo
        </button>
      </div>
    ),
  }),
)

const useAlertsQueryMock =
  vi.mocked(
    useAlertsQuery,
  )

beforeEach(() => {
  useAlertsQueryMock
    .mockReset()

  useAlertsQueryMock
    .mockReturnValue(
      {
        data: {
          content: [],
          page: 0,
          size: 25,
          totalElements: 0,
          totalPages: 0,
          hasNext: false,
          hasPrevious: false,
        },
        isError: false,
        isFetching: false,
      } as unknown as ReturnType<
        typeof useAlertsQuery
      >,
    )
})

afterEach(() => {
  cleanup()
})

describe('AlertsPage', () => {
  it(
    'loads alerts using the canonical server defaults',
    () => {
      render(
        <AlertsPage />,
      )

      expect(
        useAlertsQueryMock,
      ).toHaveBeenCalledWith({
        page: 0,
        size: 25,
        sort: 'generatedAt',
        direction: 'DESC',
      })
    },
  )

  it(
    'updates server pagination and sorting parameters',
    async () => {
      render(
        <AlertsPage />,
      )

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name: 'Cambiar página',
          },
        ),
      )

      await waitFor(
        () => {
          expect(
            useAlertsQueryMock,
          ).toHaveBeenLastCalledWith({
            page: 1,
            size: 50,
            sort: 'generatedAt',
            direction: 'DESC',
          })
        },
      )

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name: 'Ordenar por riesgo',
          },
        ),
      )

      await waitFor(
        () => {
          expect(
            useAlertsQueryMock,
          ).toHaveBeenLastCalledWith({
            page: 0,
            size: 50,
            sort: 'riskScore',
            direction: 'ASC',
          })
        },
      )
    },
  )

  it(
    'applies the canonical alert filters',
    async () => {
      render(
        <AlertsPage />,
      )

      fireEvent.change(
        screen.getByLabelText(
          'Estado',
        ),
        {
          target: {
            value:
              'IN_PROGRESS',
          },
        },
      )

      fireEvent.change(
        screen.getByLabelText(
          'Prioridad',
        ),
        {
          target: {
            value: 'HIGH',
          },
        },
      )

      fireEvent.change(
        screen.getByLabelText(
          'Nivel de riesgo',
        ),
        {
          target: {
            value: 'ALTO',
          },
        },
      )

      fireEvent.change(
        screen.getByLabelText(
          'Asignado a',
        ),
        {
          target: {
            value:
              '11111111-1111-1111-1111-111111111111',
          },
        },
      )

      fireEvent.change(
        screen.getByLabelText(
          'ID de cliente',
        ),
        {
          target: {
            value:
              '22222222-2222-2222-2222-222222222222',
          },
        },
      )

      fireEvent.change(
        screen.getByLabelText(
          'Código de escenario',
        ),
        {
          target: {
            value: 'ATO-001',
          },
        },
      )

      fireEvent.change(
        screen.getByLabelText(
          'ID de caso',
        ),
        {
          target: {
            value:
              '33333333-3333-3333-3333-333333333333',
          },
        },
      )

      fireEvent.change(
        screen.getByLabelText(
          'Creado desde',
        ),
        {
          target: {
            value:
              '2026-09-01T08:00',
          },
        },
      )

      fireEvent.change(
        screen.getByLabelText(
          'Creado hasta',
        ),
        {
          target: {
            value:
              '2026-09-01T18:00',
          },
        },
      )

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name: 'Aplicar filtros',
          },
        ),
      )

      await waitFor(
        () => {
          expect(
            useAlertsQueryMock,
          ).toHaveBeenLastCalledWith({
            status:
              'IN_PROGRESS',
            priority:
              'HIGH',
            riskLevel:
              'ALTO',
            assignedTo:
              '11111111-1111-1111-1111-111111111111',
            createdFrom:
              '2026-09-01T08:00:00',
            createdTo:
              '2026-09-01T18:00:00',
            customerId:
              '22222222-2222-2222-2222-222222222222',
            scenarioCode:
              'ATO-001',
            caseId:
              '33333333-3333-3333-3333-333333333333',
            page: 0,
            size: 25,
            sort: 'generatedAt',
            direction: 'DESC',
          })
        },
      )
    },
  )
})
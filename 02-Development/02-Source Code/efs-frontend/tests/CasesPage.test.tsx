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
import { useCasesQuery } from '../src/modules/cases/hooks/useCasesQuery'
import CasesPage from '../src/modules/cases/pages/CasesPage'

vi.mock(
  '../src/modules/cases/hooks/useCasesQuery',
  () => ({
    useCasesQuery: vi.fn(),
  }),
)

vi.mock(
  '../src/modules/cases/components/CaseDetail',
  () => ({
    default: ({
      caseId,
      onBack,
    }: {
      caseId: string
      onBack: () => void
    }) => (
      <div data-testid="case-detail">
        <span>{caseId}</span>

        <button
          type="button"
          onClick={onBack}
        >
          Volver desde detalle
        </button>
      </div>
    ),
  }),
)

vi.mock(
  '@mui/x-data-grid',
  () => ({
    DataGrid: (
      props: {
        rows?: Array<{
          caseId: string
        }>
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
        onRowClick?: (
          params: {
            row: {
              caseId: string
            }
          },
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
                field: 'createdAt',
                sort: 'asc',
              },
            ])
          }
        >
          Ordenar por creación
        </button>

        {props.rows?.[0] && (
          <button
            type="button"
            onClick={() =>
              props.onRowClick?.({
                row:
                  props.rows[0],
              })
            }
          >
            Abrir caso
          </button>
        )}
      </div>
    ),
  }),
)

const useCasesQueryMock =
  vi.mocked(
    useCasesQuery,
  )

const emptyResponse = {
  content: [],
  page: 0,
  size: 25,
  totalElements: 0,
  totalPages: 0,
  hasNext: false,
  hasPrevious: false,
}

beforeEach(() => {
  useCasesQueryMock
    .mockReset()

  useCasesQueryMock
    .mockReturnValue(
      {
        data: emptyResponse,
        isError: false,
        isFetching: false,
      } as unknown as ReturnType<
        typeof useCasesQuery
      >,
    )
})

afterEach(() => {
  cleanup()
})

describe('CasesPage', () => {
  it(
    'loads cases using the canonical server defaults',
    () => {
      render(
        <CasesPage />,
      )

      expect(
        useCasesQueryMock,
      ).toHaveBeenCalledWith({
        page: 0,
        size: 25,
        sort: 'createdAt',
        direction: 'DESC',
      })
    },
  )

  it(
    'updates server pagination and sorting parameters',
    async () => {
      render(
        <CasesPage />,
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
            useCasesQueryMock,
          ).toHaveBeenLastCalledWith({
            page: 1,
            size: 50,
            sort: 'createdAt',
            direction: 'DESC',
          })
        },
      )

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name: 'Ordenar por creación',
          },
        ),
      )

      await waitFor(
        () => {
          expect(
            useCasesQueryMock,
          ).toHaveBeenLastCalledWith({
            page: 0,
            size: 50,
            sort: 'createdAt',
            direction: 'ASC',
          })
        },
      )
    },
  )

  it(
    'applies the canonical case filters',
    async () => {
      render(
        <CasesPage />,
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
            value:
              'HIGH',
          },
        },
      )

      fireEvent.change(
        screen.getByLabelText(
          'Usuario asignado',
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
          'Equipo asignado',
        ),
        {
          target: {
            value:
              'FRAUD_INVESTIGATION',
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
            useCasesQueryMock,
          ).toHaveBeenLastCalledWith({
            status:
              'IN_PROGRESS',
            priority:
              'HIGH',
            assignedUser:
              '11111111-1111-1111-1111-111111111111',
            assignedTeam:
              'FRAUD_INVESTIGATION',
            page: 0,
            size: 25,
            sort: 'createdAt',
            direction: 'DESC',
          })
        },
      )
    },
  )

  it(
    'opens Case Detail from the selected Case and returns to the preserved list',
    () => {
      const caseId =
        '33333333-3333-3333-3333-333333333333'

      useCasesQueryMock
        .mockReturnValue(
          {
            data: {
              ...emptyResponse,
              content: [
                {
                  caseId,
                  caseNumber:
                    'CASE-003',
                },
              ],
            },
            isError: false,
            isFetching: false,
          } as unknown as ReturnType<
            typeof useCasesQuery
          >,
        )

      render(
        <CasesPage />,
      )

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name: 'Abrir caso',
          },
        ),
      )

      expect(
        screen.getByTestId(
          'case-detail',
        ),
      ).toBeTruthy()

      expect(
        screen.getByText(
          caseId,
        ),
      ).toBeTruthy()

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name:
              'Volver desde detalle',
          },
        ),
      )

      expect(
        screen.queryByTestId(
          'case-detail',
        ),
      ).toBeNull()

      expect(
        screen.getByRole(
          'button',
          {
            name: 'Abrir caso',
          },
        ),
      ).toBeTruthy()
    },
  )
})

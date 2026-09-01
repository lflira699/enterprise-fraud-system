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
import { useRiskAssessmentsQuery } from '../src/modules/risk/hooks/useRiskAssessmentsQuery'
import RiskPage from '../src/modules/risk/pages/RiskPage'

vi.mock(
  '../src/modules/risk/hooks/useRiskAssessmentsQuery',
  () => ({
    useRiskAssessmentsQuery: vi.fn(),
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
                field: 'assessmentTimestamp',
                sort: 'asc',
              },
            ])
          }
        >
          Ordenar por evaluación
        </button>
      </div>
    ),
  }),
)

const useRiskAssessmentsQueryMock =
  vi.mocked(
    useRiskAssessmentsQuery,
  )

beforeEach(() => {
  useRiskAssessmentsQueryMock
    .mockReset()

  useRiskAssessmentsQueryMock
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
        typeof useRiskAssessmentsQuery
      >,
    )
})

afterEach(() => {
  cleanup()
})

describe('RiskPage', () => {
  it(
    'loads risk assessments using the canonical server defaults',
    () => {
      render(
        <RiskPage />,
      )

      expect(
        useRiskAssessmentsQueryMock,
      ).toHaveBeenCalledWith({
        page: 0,
        size: 25,
        sort: 'assessmentTimestamp',
        direction: 'DESC',
      })
    },
  )

  it(
    'updates server pagination and sorting parameters',
    async () => {
      render(
        <RiskPage />,
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
            useRiskAssessmentsQueryMock,
          ).toHaveBeenLastCalledWith({
            page: 1,
            size: 50,
            sort: 'assessmentTimestamp',
            direction: 'DESC',
          })
        },
      )

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name: 'Ordenar por evaluación',
          },
        ),
      )

      await waitFor(
        () => {
          expect(
            useRiskAssessmentsQueryMock,
          ).toHaveBeenLastCalledWith({
            page: 0,
            size: 50,
            sort: 'assessmentTimestamp',
            direction: 'ASC',
          })
        },
      )
    },
  )

  it(
    'applies the canonical risk assessment filters',
    async () => {
      render(
        <RiskPage />,
      )

      fireEvent.change(
        screen.getByLabelText(
          'Nivel de riesgo',
        ),
        {
          target: {
            value: 'HIGH',
          },
        },
      )

      fireEvent.change(
        screen.getByLabelText(
          'Resultado de evaluación',
        ),
        {
          target: {
            value: 'REVIEW',
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
            useRiskAssessmentsQueryMock,
          ).toHaveBeenLastCalledWith({
            riskLevel: 'HIGH',
            assessmentResult: 'REVIEW',
            page: 0,
            size: 25,
            sort: 'assessmentTimestamp',
            direction: 'DESC',
          })
        },
      )
    },
  )
})
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
import { useDetectionScenariosQuery } from '../src/modules/detection/hooks/useDetectionScenariosQuery'
import DetectionPage from '../src/modules/detection/pages/DetectionPage'

vi.mock(
  '../src/modules/detection/hooks/useDetectionScenariosQuery',
  () => ({
    useDetectionScenariosQuery: vi.fn(),
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
                field: 'scenarioName',
                sort: 'desc',
              },
            ])
          }
        >
          Ordenar por nombre
        </button>
      </div>
    ),
  }),
)

const useDetectionScenariosQueryMock =
  vi.mocked(
    useDetectionScenariosQuery,
  )

beforeEach(() => {
  useDetectionScenariosQueryMock
    .mockReset()

  useDetectionScenariosQueryMock
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
        typeof useDetectionScenariosQuery
      >,
    )
})

afterEach(() => {
  cleanup()
})

describe('DetectionPage', () => {
  it(
    'loads detection scenarios using the canonical server defaults',
    () => {
      render(
        <DetectionPage />,
      )

      expect(
        useDetectionScenariosQueryMock,
      ).toHaveBeenCalledWith({
        page: 0,
        size: 25,
        sort: 'scenarioName',
        direction: 'ASC',
      })
    },
  )

  it(
    'updates server pagination and sorting parameters',
    async () => {
      render(
        <DetectionPage />,
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
            useDetectionScenariosQueryMock,
          ).toHaveBeenLastCalledWith({
            page: 1,
            size: 50,
            sort: 'scenarioName',
            direction: 'ASC',
          })
        },
      )

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name: 'Ordenar por nombre',
          },
        ),
      )

      await waitFor(
        () => {
          expect(
            useDetectionScenariosQueryMock,
          ).toHaveBeenLastCalledWith({
            page: 0,
            size: 50,
            sort: 'scenarioName',
            direction: 'DESC',
          })
        },
      )
    },
  )

  it(
    'applies the canonical detection scenario filters',
    async () => {
      render(
        <DetectionPage />,
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
          'Categoría',
        ),
        {
          target: {
            value: 'ATO',
          },
        },
      )

      fireEvent.change(
        screen.getByLabelText(
          'Estado',
        ),
        {
          target: {
            value: 'ACTIVE',
          },
        },
      )

      fireEvent.change(
        screen.getByLabelText(
          'Criticidad',
        ),
        {
          target: {
            value: 'HIGH',
          },
        },
      )

      fireEvent.change(
        screen.getByLabelText(
          'Responsable',
        ),
        {
          target: {
            value: 'DetectionTeam',
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
            useDetectionScenariosQueryMock,
          ).toHaveBeenLastCalledWith({
            scenarioCode: 'ATO-001',
            category: 'ATO',
            status: 'ACTIVE',
            criticality: 'HIGH',
            owner: 'DetectionTeam',
            page: 0,
            size: 25,
            sort: 'scenarioName',
            direction: 'ASC',
          })
        },
      )
    },
  )
})
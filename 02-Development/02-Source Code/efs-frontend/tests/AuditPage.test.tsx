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
import { useAuditEventsQuery } from '../src/modules/audit/hooks/useAuditEventsQuery'
import AuditPage from '../src/modules/audit/pages/AuditPage'

vi.mock(
  '../src/modules/audit/hooks/useAuditEventsQuery',
  () => ({
    useAuditEventsQuery:
      vi.fn(),
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
                field: 'eventTimestamp',
                sort: 'asc',
              },
            ])
          }
        >
          Ordenar por fecha
        </button>
      </div>
    ),
  }),
)

const useAuditEventsQueryMock =
  vi.mocked(
    useAuditEventsQuery,
  )

beforeEach(() => {
  useAuditEventsQueryMock
    .mockReset()

  useAuditEventsQueryMock
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
        typeof useAuditEventsQuery
      >,
    )
})

afterEach(() => {
  cleanup()
})

describe('AuditPage', () => {
  it(
    'loads audit events using canonical defaults',
    () => {
      render(
        <AuditPage />,
      )

      expect(
        useAuditEventsQueryMock,
      ).toHaveBeenCalledWith({
        page: 0,
        size: 25,
        sort: 'eventTimestamp',
        direction: 'DESC',
      })
    },
  )

  it(
    'applies canonical audit review filters',
    async () => {
      render(
        <AuditPage />,
      )

      fireEvent.change(
        screen.getByLabelText(
          'Usuario',
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
          'Desde',
        ),
        {
          target: {
            value:
              '2026-09-24T08:00',
          },
        },
      )

      fireEvent.change(
        screen.getByLabelText(
          'Hasta',
        ),
        {
          target: {
            value:
              '2026-09-24T18:00',
          },
        },
      )

      fireEvent.change(
        screen.getByLabelText(
          'Tipo de entidad',
        ),
        {
          target: {
            value:
              'CASE',
          },
        },
      )

      fireEvent.change(
        screen.getByLabelText(
          'ID de entidad',
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
          'Acción',
        ),
        {
          target: {
            value:
              'SEARCH',
          },
        },
      )

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name: 'Buscar',
          },
        ),
      )

      await waitFor(
        () => {
          expect(
            useAuditEventsQueryMock,
          ).toHaveBeenLastCalledWith({
            userId:
              '11111111-1111-1111-1111-111111111111',
            from:
              '2026-09-24T08:00',
            to:
              '2026-09-24T18:00',
            entityType:
              'CASE',
            entityId:
              '22222222-2222-2222-2222-222222222222',
            action:
              'SEARCH',
            page: 0,
            size: 25,
            sort:
              'eventTimestamp',
            direction:
              'DESC',
          })
        },
      )
    },
  )

  it(
    'updates server pagination and timestamp sorting',
    async () => {
      render(
        <AuditPage />,
      )

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name:
              'Cambiar página',
          },
        ),
      )

      await waitFor(
        () => {
          expect(
            useAuditEventsQueryMock,
          ).toHaveBeenLastCalledWith({
            page: 1,
            size: 50,
            sort:
              'eventTimestamp',
            direction:
              'DESC',
          })
        },
      )

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name:
              'Ordenar por fecha',
          },
        ),
      )

      await waitFor(
        () => {
          expect(
            useAuditEventsQueryMock,
          ).toHaveBeenLastCalledWith({
            page: 1,
            size: 50,
            sort:
              'eventTimestamp',
            direction:
              'ASC',
          })
        },
      )
    },
  )
})
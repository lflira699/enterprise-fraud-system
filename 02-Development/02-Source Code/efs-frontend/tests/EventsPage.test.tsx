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
import { useEventsQuery } from '../src/modules/events/hooks/useEventsQuery'
import EventsPage from '../src/modules/events/pages/EventsPage'

vi.mock(
  '../src/modules/events/hooks/useEventsQuery',
  () => ({
    useEventsQuery: vi.fn(),
  }),
)

const useEventsQueryMock =
  vi.mocked(
    useEventsQuery,
  )

beforeEach(() => {
  useEventsQueryMock.mockReturnValue(
    {
      data: {
        content: [
          {
            fraudEventId: 'event-1',
            organizationId: 'organization-1',
            tenantId: 'tenant-1',
            transactionId: 'transaction-1',
            eventType: 'ACCOUNT_TAKEOVER',
            sourceType: 'DIGITAL_CHANNEL',
            sourceReference: 'SRC-001',
            idempotencyKey: 'IDEMP-001',
            correlationId: 'correlation-1',
            normalizedPayload: {},
            occurredAt: '2026-10-01T08:00:00',
            receivedAt: '2026-10-01T08:00:01',
            createdAt: '2026-10-01T08:00:02',
          },
        ],
        page: 0,
        size: 25,
        totalElements: 1,
        totalPages: 1,
      },
      isLoading: false,
      isError: false,
    } as ReturnType<typeof useEventsQuery>,
  )
})

afterEach(() => {
  cleanup()
  vi.clearAllMocks()
})

describe('EventsPage', () => {
  it(
    'loads FraudEvent results using canonical server defaults',
    () => {
      render(
        <EventsPage />,
      )

      expect(
        screen.getByText(
          'ACCOUNT_TAKEOVER',
        ),
      ).toBeTruthy()

      expect(
        useEventsQueryMock,
      ).toHaveBeenCalledWith(
        expect.objectContaining({
          page: 0,
          size: 25,
          sort: 'occurredAt',
          direction: 'DESC',
        }),
      )
    },
  )

  it(
    'applies the canonical FraudEvent filters',
    async () => {
      render(
        <EventsPage />,
      )

      fireEvent.change(
        screen.getByLabelText(
          'Tipo de evento',
        ),
        {
          target: {
            value: 'ACCOUNT_TAKEOVER',
          },
        },
      )

      fireEvent.change(
        screen.getByLabelText(
          'Tipo de origen',
        ),
        {
          target: {
            value: 'DIGITAL_CHANNEL',
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
            useEventsQueryMock,
          ).toHaveBeenLastCalledWith(
            expect.objectContaining({
              eventType:
                'ACCOUNT_TAKEOVER',
              sourceType:
                'DIGITAL_CHANNEL',
              page: 0,
            }),
          )
        },
      )
    },
  )
})

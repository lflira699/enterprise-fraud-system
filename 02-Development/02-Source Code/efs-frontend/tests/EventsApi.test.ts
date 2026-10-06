import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import { getEvents } from '../src/modules/events/api/eventsApi'
import { httpClient } from '../src/services/httpClient'

afterEach(() => {
  vi.restoreAllMocks()
})

describe('eventsApi', () => {
  it(
    'uses the canonical pagination and sorting defaults',
    async () => {
      const getMock =
        vi.spyOn(
          httpClient,
          'get',
        )
          .mockResolvedValue(
            {} as never,
          )

      await getEvents({})

      expect(getMock)
        .toHaveBeenCalledWith(
          '/events',
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
    'passes the canonical event search filters',
    async () => {
      const getMock =
        vi.spyOn(
          httpClient,
          'get',
        )
          .mockResolvedValue(
            {} as never,
          )

      await getEvents({
        transactionId: 'transaction-1',
        eventType: 'ACCOUNT_TAKEOVER',
        sourceType: 'DIGITAL_CHANNEL',
        sourceReference: 'SRC-001',
        correlationId: 'correlation-1',
        occurredFrom: '2026-10-01T08:00:00',
        occurredTo: '2026-10-01T09:00:00',
        receivedFrom: '2026-10-01T08:01:00',
        receivedTo: '2026-10-01T09:01:00',
        page: 2,
        size: 50,
        sort: 'occurredAt',
        direction: 'ASC',
      })

      expect(getMock)
        .toHaveBeenCalledWith(
          '/events',
          {
            transactionId: 'transaction-1',
            eventType: 'ACCOUNT_TAKEOVER',
            sourceType: 'DIGITAL_CHANNEL',
            sourceReference: 'SRC-001',
            correlationId: 'correlation-1',
            occurredFrom: '2026-10-01T08:00:00',
            occurredTo: '2026-10-01T09:00:00',
            receivedFrom: '2026-10-01T08:01:00',
            receivedTo: '2026-10-01T09:01:00',
            page: 2,
            size: 50,
            sort: 'occurredAt',
            direction: 'ASC',
          },
        )
    },
  )
})

import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import {
  getAuditEvents,
} from '../src/modules/audit/api/auditEventsApi'
import { httpClient } from '../src/services/httpClient'

afterEach(() => {
  vi.restoreAllMocks()
})

describe('Audit API', () => {
  it(
    'uses canonical audit search defaults',
    async () => {
      const getMock =
        vi.spyOn(
          httpClient,
          'get',
        )
          .mockResolvedValue({
            content: [],
            page: 0,
            size: 25,
            totalElements: 0,
            totalPages: 0,
            hasNext: false,
            hasPrevious: false,
          })

      await getAuditEvents({})

      expect(
        getMock,
      ).toHaveBeenCalledWith(
        '/audit/events',
        {
          userId: undefined,
          from: undefined,
          to: undefined,
          entityType: undefined,
          entityId: undefined,
          action: undefined,
          page: 0,
          size: 25,
          sort: 'eventTimestamp',
          direction: 'DESC',
        },
      )
    },
  )

  it(
    'sends canonical audit search criteria unchanged',
    async () => {
      const getMock =
        vi.spyOn(
          httpClient,
          'get',
        )
          .mockResolvedValue({
            content: [],
            page: 2,
            size: 50,
            totalElements: 0,
            totalPages: 0,
            hasNext: false,
            hasPrevious: true,
          })

      await getAuditEvents({
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
        page: 2,
        size: 50,
        sort:
          'eventTimestamp',
        direction:
          'ASC',
      })

      expect(
        getMock,
      ).toHaveBeenCalledWith(
        '/audit/events',
        {
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
          page: 2,
          size: 50,
          sort:
            'eventTimestamp',
          direction:
            'ASC',
        },
      )
    },
  )
})
import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import { getAlerts } from '../src/modules/alerts/api/alertsApi'
import { httpClient } from '../src/services/httpClient'

afterEach(() => {
  vi.restoreAllMocks()
})

describe('alertsApi', () => {
  it(
    'uses the canonical pagination and sorting defaults',
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

      await getAlerts({})

      expect(
        getMock,
      ).toHaveBeenCalledTimes(1)

      expect(
        getMock,
      ).toHaveBeenCalledWith(
        '/alerts',
        {
          status: undefined,
          priority: undefined,
          riskLevel: undefined,
          assignedTo: undefined,
          createdFrom: undefined,
          createdTo: undefined,
          customerId: undefined,
          scenarioCode: undefined,
          caseId: undefined,
          page: 0,
          size: 25,
          sort: 'generatedAt',
          direction: 'DESC',
        },
      )
    },
  )

  it(
    'sends the canonical alert search parameters unchanged',
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

      await getAlerts({
        status: 'IN_PROGRESS',
        priority: 'HIGH',
        riskLevel: 'ALTO',
        assignedTo:
          '11111111-1111-1111-1111-111111111111',
        createdFrom:
          '2026-09-01T00:00:00',
        createdTo:
          '2026-09-01T23:59:59',
        customerId:
          '22222222-2222-2222-2222-222222222222',
        scenarioCode:
          'ATO-001',
        caseId:
          '33333333-3333-3333-3333-333333333333',
        page: 2,
        size: 50,
        sort: 'riskScore',
        direction: 'ASC',
      })

      expect(
        getMock,
      ).toHaveBeenCalledTimes(1)

      expect(
        getMock,
      ).toHaveBeenCalledWith(
        '/alerts',
        {
          status: 'IN_PROGRESS',
          priority: 'HIGH',
          riskLevel: 'ALTO',
          assignedTo:
            '11111111-1111-1111-1111-111111111111',
          createdFrom:
            '2026-09-01T00:00:00',
          createdTo:
            '2026-09-01T23:59:59',
          customerId:
            '22222222-2222-2222-2222-222222222222',
          scenarioCode:
            'ATO-001',
          caseId:
            '33333333-3333-3333-3333-333333333333',
          page: 2,
          size: 50,
          sort: 'riskScore',
          direction: 'ASC',
        },
      )
    },
  )
})
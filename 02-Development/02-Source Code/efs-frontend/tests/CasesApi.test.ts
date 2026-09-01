import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import { getCases } from '../src/modules/cases/api/casesApi'
import { httpClient } from '../src/services/httpClient'

afterEach(() => {
  vi.restoreAllMocks()
})

describe('casesApi', () => {
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

      await getCases({})

      expect(
        getMock,
      ).toHaveBeenCalledTimes(1)

      expect(
        getMock,
      ).toHaveBeenCalledWith(
        '/cases',
        {
          status: undefined,
          priority: undefined,
          assignedUser: undefined,
          assignedTeam: undefined,
          page: 0,
          size: 25,
          sort: 'createdAt',
          direction: 'DESC',
        },
      )
    },
  )

  it(
    'sends the canonical case search parameters unchanged',
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

      await getCases({
        status: 'IN_PROGRESS',
        priority: 'HIGH',
        assignedUser:
          '11111111-1111-1111-1111-111111111111',
        assignedTeam:
          'FRAUD_INVESTIGATION',
        page: 2,
        size: 50,
        sort: 'createdAt',
        direction: 'ASC',
      })

      expect(
        getMock,
      ).toHaveBeenCalledTimes(1)

      expect(
        getMock,
      ).toHaveBeenCalledWith(
        '/cases',
        {
          status: 'IN_PROGRESS',
          priority: 'HIGH',
          assignedUser:
            '11111111-1111-1111-1111-111111111111',
          assignedTeam:
            'FRAUD_INVESTIGATION',
          page: 2,
          size: 50,
          sort: 'createdAt',
          direction: 'ASC',
        },
      )
    },
  )
})
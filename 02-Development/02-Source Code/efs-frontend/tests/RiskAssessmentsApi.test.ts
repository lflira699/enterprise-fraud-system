import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import { getRiskAssessments } from '../src/modules/risk/api/riskAssessmentsApi'
import { httpClient } from '../src/services/httpClient'

afterEach(() => {
  vi.restoreAllMocks()
})

describe('riskAssessmentsApi', () => {
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

      await getRiskAssessments({})

      expect(
        getMock,
      ).toHaveBeenCalledTimes(1)

      expect(
        getMock,
      ).toHaveBeenCalledWith(
        '/risk-assessments',
        {
          riskLevel: undefined,
          assessmentResult: undefined,
          page: 0,
          size: 25,
          sort: 'assessmentTimestamp',
          direction: 'DESC',
        },
      )
    },
  )

  it(
    'sends the canonical risk assessment search parameters unchanged',
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

      await getRiskAssessments({
        riskLevel: 'HIGH',
        assessmentResult: 'REVIEW',
        page: 2,
        size: 50,
        sort: 'assessmentTimestamp',
        direction: 'ASC',
      })

      expect(
        getMock,
      ).toHaveBeenCalledTimes(1)

      expect(
        getMock,
      ).toHaveBeenCalledWith(
        '/risk-assessments',
        {
          riskLevel: 'HIGH',
          assessmentResult: 'REVIEW',
          page: 2,
          size: 50,
          sort: 'assessmentTimestamp',
          direction: 'ASC',
        },
      )
    },
  )
})
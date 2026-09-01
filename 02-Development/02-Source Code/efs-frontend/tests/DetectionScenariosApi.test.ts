import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import { getDetectionScenarios } from '../src/modules/detection/api/detectionScenariosApi'
import { httpClient } from '../src/services/httpClient'

afterEach(() => {
  vi.restoreAllMocks()
})

describe('detectionScenariosApi', () => {
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

      await getDetectionScenarios({})

      expect(
        getMock,
      ).toHaveBeenCalledTimes(1)

      expect(
        getMock,
      ).toHaveBeenCalledWith(
        '/detection/scenarios',
        {
          scenarioCode: undefined,
          category: undefined,
          status: undefined,
          criticality: undefined,
          owner: undefined,
          page: 0,
          size: 25,
          sort: 'scenarioName',
          direction: 'ASC',
        },
      )
    },
  )

  it(
    'sends the canonical detection scenario search parameters unchanged',
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

      await getDetectionScenarios({
        scenarioCode: 'ATO-001',
        category: 'ATO',
        status: 'ACTIVE',
        criticality: 'HIGH',
        owner: 'DetectionTeam',
        page: 2,
        size: 50,
        sort: 'scenarioName',
        direction: 'DESC',
      })

      expect(
        getMock,
      ).toHaveBeenCalledTimes(1)

      expect(
        getMock,
      ).toHaveBeenCalledWith(
        '/detection/scenarios',
        {
          scenarioCode: 'ATO-001',
          category: 'ATO',
          status: 'ACTIVE',
          criticality: 'HIGH',
          owner: 'DetectionTeam',
          page: 2,
          size: 50,
          sort: 'scenarioName',
          direction: 'DESC',
        },
      )
    },
  )
})
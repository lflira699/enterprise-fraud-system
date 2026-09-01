import {
  renderHook,
  waitFor,
} from '@testing-library/react'
import {
  QueryClient,
  QueryClientProvider,
} from '@tanstack/react-query'
import type { ReactNode } from 'react'
import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import * as detectionScenariosApi from '../src/modules/detection/api/detectionScenariosApi'
import { useDetectionScenariosQuery } from '../src/modules/detection/hooks/useDetectionScenariosQuery'
import type { DetectionScenarioSearchParams } from '../src/modules/detection/types/detectionScenario'

afterEach(() => {
  vi.restoreAllMocks()
})

describe('useDetectionScenariosQuery', () => {
  it(
    'loads detection scenarios using the supplied search parameters',
    async () => {
      const params: DetectionScenarioSearchParams = {
        scenarioCode: 'ATO-001',
        category: 'ATO',
        status: 'ACTIVE',
        criticality: 'HIGH',
        owner: 'DetectionTeam',
        page: 1,
        size: 25,
        sort: 'scenarioName',
        direction: 'ASC',
      }

      const response = {
        content: [],
        page: 1,
        size: 25,
        totalElements: 0,
        totalPages: 0,
        hasNext: false,
        hasPrevious: true,
      }

      const getDetectionScenariosMock =
        vi.spyOn(
          detectionScenariosApi,
          'getDetectionScenarios',
        )
          .mockResolvedValue(
            response,
          )

      const queryClient =
        new QueryClient({
          defaultOptions: {
            queries: {
              retry: false,
            },
          },
        })

      function Wrapper({
        children,
      }: {
        children: ReactNode
      }) {
        return (
          <QueryClientProvider
            client={queryClient}
          >
            {children}
          </QueryClientProvider>
        )
      }

      const { result } =
        renderHook(
          () => useDetectionScenariosQuery(
            params,
          ),
          {
            wrapper: Wrapper,
          },
        )

      await waitFor(
        () => {
          expect(
            result.current.isSuccess,
          ).toBe(true)
        },
      )

      expect(
        getDetectionScenariosMock,
      ).toHaveBeenCalledTimes(1)

      expect(
        getDetectionScenariosMock,
      ).toHaveBeenCalledWith(
        params,
      )

      expect(
        result.current.data,
      ).toEqual(
        response,
      )

      queryClient.clear()
    },
  )
})
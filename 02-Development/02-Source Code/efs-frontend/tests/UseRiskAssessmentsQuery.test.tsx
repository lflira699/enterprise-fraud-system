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

import * as riskAssessmentsApi from '../src/modules/risk/api/riskAssessmentsApi'
import { useRiskAssessmentsQuery } from '../src/modules/risk/hooks/useRiskAssessmentsQuery'
import type { RiskAssessmentSearchParams } from '../src/modules/risk/types/riskAssessment'

afterEach(() => {
  vi.restoreAllMocks()
})

describe('useRiskAssessmentsQuery', () => {
  it(
    'loads risk assessments using the supplied search parameters',
    async () => {
      const params: RiskAssessmentSearchParams = {
        riskLevel: 'HIGH',
        assessmentResult: 'REVIEW',
        page: 1,
        size: 25,
        sort: 'assessmentTimestamp',
        direction: 'DESC',
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

      const getRiskAssessmentsMock =
        vi.spyOn(
          riskAssessmentsApi,
          'getRiskAssessments',
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
          () => useRiskAssessmentsQuery(
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
        getRiskAssessmentsMock,
      ).toHaveBeenCalledTimes(1)

      expect(
        getRiskAssessmentsMock,
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
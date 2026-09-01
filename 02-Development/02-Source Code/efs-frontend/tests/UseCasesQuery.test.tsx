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

import * as casesApi from '../src/modules/cases/api/casesApi'
import { useCasesQuery } from '../src/modules/cases/hooks/useCasesQuery'
import type { CaseSearchParams } from '../src/modules/cases/types/case'

afterEach(() => {
  vi.restoreAllMocks()
})

describe('useCasesQuery', () => {
  it(
    'loads cases using the supplied search parameters',
    async () => {
      const params: CaseSearchParams = {
        status: 'OPEN',
        priority: 'HIGH',
        assignedUser:
          '11111111-1111-1111-1111-111111111111',
        assignedTeam:
          'FRAUD_INVESTIGATION',
        page: 1,
        size: 25,
        sort: 'createdAt',
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

      const getCasesMock =
        vi.spyOn(
          casesApi,
          'getCases',
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
          () => useCasesQuery(
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
        getCasesMock,
      ).toHaveBeenCalledTimes(1)

      expect(
        getCasesMock,
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
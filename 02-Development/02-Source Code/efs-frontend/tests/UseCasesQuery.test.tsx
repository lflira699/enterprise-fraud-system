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
import {
  useCaseQuery,
  useCasesQuery,
} from '../src/modules/cases/hooks/useCasesQuery'
import type { CaseSearchParams } from '../src/modules/cases/types/case'

afterEach(() => {
  vi.restoreAllMocks()
})

function createWrapper() {
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

  return {
    Wrapper,
    queryClient,
  }
}

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

      const {
        Wrapper,
        queryClient,
      } = createWrapper()

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

describe('useCaseQuery', () => {
  it(
    'loads the selected Case by identifier',
    async () => {
      const caseId =
        '22222222-2222-2222-2222-222222222222'

      const response = {
        caseId,
        caseNumber: 'CASE-002',
        organizationId: null,
        transactionId: null,
        customerId: null,
        caseType: 'FRAUD_INVESTIGATION',
        category: null,
        severity: null,
        priority: 'HIGH',
        currentStatus: 'OPEN',
        assignedTeam: null,
        assignedUser: null,
        createdAt: null,
        updatedAt: null,
        dueDate: null,
        closedAt: null,
        tenantId: null,
      }

      const getCaseByIdMock =
        vi.spyOn(
          casesApi,
          'getCaseById',
        )
          .mockResolvedValue(
            response,
          )

      const {
        Wrapper,
        queryClient,
      } = createWrapper()

      const { result } =
        renderHook(
          () => useCaseQuery(
            caseId,
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
        getCaseByIdMock,
      ).toHaveBeenCalledWith(
        caseId,
      )

      expect(
        result.current.data,
      ).toEqual(
        response,
      )

      queryClient.clear()
    },
  )

  it(
    'does not load Case Detail without a Case identifier',
    () => {
      const getCaseByIdMock =
        vi.spyOn(
          casesApi,
          'getCaseById',
        )

      const {
        Wrapper,
        queryClient,
      } = createWrapper()

      const { result } =
        renderHook(
          () => useCaseQuery(
            null,
          ),
          {
            wrapper: Wrapper,
          },
        )

      expect(
        result.current.fetchStatus,
      ).toBe('idle')

      expect(
        getCaseByIdMock,
      ).not.toHaveBeenCalled()

      queryClient.clear()
    },
  )
})

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

import * as alertsApi from '../src/modules/alerts/api/alertsApi'
import { useAlertsQuery } from '../src/modules/alerts/hooks/useAlertsQuery'
import type { AlertSearchParams } from '../src/modules/alerts/types/alert'

afterEach(() => {
  vi.restoreAllMocks()
})

describe('useAlertsQuery', () => {
  it(
    'loads alerts using the supplied search parameters',
    async () => {
      const params: AlertSearchParams = {
        status: 'NEW',
        page: 1,
        size: 25,
        sort: 'generatedAt',
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

      const getAlertsMock =
        vi.spyOn(
          alertsApi,
          'getAlerts',
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
          () => useAlertsQuery(
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
        getAlertsMock,
      ).toHaveBeenCalledTimes(1)

      expect(
        getAlertsMock,
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
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
import {
  useAlertQuery,
  useAlertsQuery,
} from '../src/modules/alerts/hooks/useAlertsQuery'
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
describe('useAlertQuery', () => {
  it(
    'loads an alert using its stable alertId',
    async () => {
      const alertId =
        '11111111-1111-1111-1111-111111111111'

      const response = {
        alertId,
      }

      const getAlertByIdMock =
        vi.spyOn(
          alertsApi,
          'getAlertById',
        )
          .mockResolvedValue(
            response as never,
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
          () => useAlertQuery(
            alertId,
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
        getAlertByIdMock,
      ).toHaveBeenCalledTimes(1)

      expect(
        getAlertByIdMock,
      ).toHaveBeenCalledWith(
        alertId,
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
    'does not load when alertId is absent',
    () => {
      const getAlertByIdMock =
        vi.spyOn(
          alertsApi,
          'getAlertById',
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

      renderHook(
        () => useAlertQuery(
          null,
        ),
        {
          wrapper: Wrapper,
        },
      )

      expect(
        getAlertByIdMock,
      ).not.toHaveBeenCalled()

      queryClient.clear()
    },
  )
})

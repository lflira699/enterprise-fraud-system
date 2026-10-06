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

import * as eventsApi from '../src/modules/events/api/eventsApi'
import { useEventsQuery } from '../src/modules/events/hooks/useEventsQuery'
import type { FraudEventSearchParams } from '../src/modules/events/types/fraudEvent'

afterEach(() => {
  vi.restoreAllMocks()
})

describe('useEventsQuery', () => {
  it(
    'loads FraudEvent results using the supplied search parameters',
    async () => {
      const params: FraudEventSearchParams = {
        eventType: 'ACCOUNT_TAKEOVER',
        page: 0,
        size: 25,
        sort: 'occurredAt',
        direction: 'DESC',
      }

      const getEventsMock =
        vi.spyOn(
          eventsApi,
          'getEvents',
        )
          .mockResolvedValue(
            {
              content: [],
              page: 0,
              size: 25,
              totalElements: 0,
              totalPages: 0,
            },
          )

      const queryClient =
        new QueryClient({
          defaultOptions: {
            queries: {
              retry: false,
            },
          },
        })

      const wrapper =
        ({
          children,
        }: {
          children: ReactNode
        }) => (
          <QueryClientProvider
            client={queryClient}
          >
            {children}
          </QueryClientProvider>
        )

      const { result } =
        renderHook(
          () =>
            useEventsQuery(
              params,
            ),
          {
            wrapper,
          },
        )

      await waitFor(
        () => {
          expect(
            result.current.isSuccess,
          ).toBe(true)
        },
      )

      expect(getEventsMock)
        .toHaveBeenCalledWith(
          params,
        )
    },
  )
})

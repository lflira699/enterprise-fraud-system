import type { PropsWithChildren } from 'react'
import {
  QueryClient,
  QueryClientProvider,
} from '@tanstack/react-query'
import {
  renderHook,
  waitFor,
} from '@testing-library/react'
import {
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import { getSystemHealth } from '../src/modules/administration/api/systemHealthApi'
import { useSystemHealthQuery } from '../src/modules/administration/hooks/useSystemHealthQuery'

vi.mock(
  '../src/modules/administration/api/systemHealthApi',
  () => ({
    getSystemHealth: vi.fn(),
  }),
)

describe('useSystemHealthQuery', () => {
  it('loads system health through the canonical query', async () => {
    const payload = {
      status: 'UP',
      informationStatus: 'COMPLETE',
      components: [],
      checkedAt: '2026-09-17T07:00:00',
    }

    vi.mocked(
      getSystemHealth,
    ).mockResolvedValue(payload)

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
    }: PropsWithChildren) {
      return (
        <QueryClientProvider client={queryClient}>
          {children}
        </QueryClientProvider>
      )
    }

    const { result } =
      renderHook(
        () => useSystemHealthQuery(),
        {
          wrapper: Wrapper,
        },
      )

    await waitFor(() => {
      expect(
        result.current.isSuccess,
      ).toBe(true)
    })

    expect(
      result.current.data,
    ).toEqual(payload)

    expect(
      getSystemHealth,
    ).toHaveBeenCalledTimes(1)
  })
})

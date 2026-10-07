import {
  cleanup,
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
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import {
  usePlaybookExecutionsByAlertQuery,
  usePlaybookExecutionStepsQuery,
} from '../src/modules/playbook/hooks/usePlaybookQueries'
import {
  getPlaybookExecutionsByAlertId,
  getPlaybookExecutionSteps,
} from '../src/modules/playbook/api/playbookApi'

vi.mock(
  '../src/modules/playbook/api/playbookApi',
  () => ({
    getPlaybookExecutionsByAlertId:
      vi.fn(),
    getPlaybookExecutionSteps:
      vi.fn(),
  }),
)

const executionsMock =
  vi.mocked(
    getPlaybookExecutionsByAlertId,
  )

const stepsMock =
  vi.mocked(
    getPlaybookExecutionSteps,
  )

let queryClient: QueryClient

function wrapper({
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

beforeEach(() => {
  queryClient =
    new QueryClient({
      defaultOptions: {
        queries: {
          retry: false,
        },
      },
    })

  executionsMock.mockReset()
  stepsMock.mockReset()
})

afterEach(() => {
  cleanup()
  queryClient.clear()
  vi.restoreAllMocks()
})

describe(
  'usePlaybookQueries',
  () => {
    it(
      'loads the execution collection for a non-empty alertId',
      async () => {
        executionsMock.mockResolvedValue([])

        const { result } =
          renderHook(
            () =>
              usePlaybookExecutionsByAlertQuery(
                '11111111-1111-1111-1111-111111111111',
              ),
            {
              wrapper,
            },
          )

        await waitFor(() => {
          expect(
            result.current.isSuccess,
          ).toBe(true)
        })

        expect(
          executionsMock,
        ).toHaveBeenCalledWith(
          '11111111-1111-1111-1111-111111111111',
        )
      },
    )

    it(
      'does not load executions when alertId is null',
      () => {
        renderHook(
          () =>
            usePlaybookExecutionsByAlertQuery(
              null,
            ),
          {
            wrapper,
          },
        )

        expect(
          executionsMock,
        ).not.toHaveBeenCalled()
      },
    )

    it(
      'loads execution steps for a non-empty execution id',
      async () => {
        stepsMock.mockResolvedValue([])

        const { result } =
          renderHook(
            () =>
              usePlaybookExecutionStepsQuery(
                '22222222-2222-2222-2222-222222222222',
              ),
            {
              wrapper,
            },
          )

        await waitFor(() => {
          expect(
            result.current.isSuccess,
          ).toBe(true)
        })

        expect(
          stepsMock,
        ).toHaveBeenCalledWith(
          '22222222-2222-2222-2222-222222222222',
        )
      },
    )

    it(
      'does not load steps when execution id is null',
      () => {
        renderHook(
          () =>
            usePlaybookExecutionStepsQuery(
              null,
            ),
          {
            wrapper,
          },
        )

        expect(
          stepsMock,
        ).not.toHaveBeenCalled()
      },
    )
  },
)

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

import * as rulesApi from '../src/modules/rules/api/rulesApi'
import { useRulesQuery } from '../src/modules/rules/hooks/useRulesQuery'

afterEach(() => {
  vi.restoreAllMocks()
})

describe('useRulesQuery', () => {
  it(
    'loads the rule catalog',
    async () => {
      const response = [
        {
          ruleId:
            '00000000-0000-0000-0000-000000000001',
          ruleCode:
            'RULE-001',
          ruleName:
            'High Value Transaction Rule',
          description:
            'Rule query test',
          category:
            'TRANSACTION',
          severity:
            'HIGH',
          priority:
            1,
          ownerTeam:
            'FRAUD_RULES',
          currentVersion:
            1,
          status:
            'ACTIVE',
          createdAt:
            '2026-09-02T08:00:00',
          updatedAt:
            '2026-09-02T08:00:00',
        },
      ]

      const getRulesMock =
        vi.spyOn(
          rulesApi,
          'getRules',
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
          () => useRulesQuery(),
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
        getRulesMock,
      ).toHaveBeenCalledTimes(1)

      expect(
        result.current.data,
      ).toEqual(
        response,
      )

      queryClient.clear()
    },
  )
})
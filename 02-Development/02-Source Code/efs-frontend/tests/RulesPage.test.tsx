import {
  cleanup,
  render,
  screen,
} from '@testing-library/react'
import {
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import '../src/configuration/i18n'
import { useRulesQuery } from '../src/modules/rules/hooks/useRulesQuery'
import RulesPage from '../src/modules/rules/pages/RulesPage'

vi.mock(
  '../src/modules/rules/hooks/useRulesQuery',
  () => ({
    useRulesQuery: vi.fn(),
  }),
)

vi.mock(
  '@mui/x-data-grid',
  () => ({
    DataGrid: (
      props: {
        rows?: unknown[]
      },
    ) => (
      <div>
        Reglas cargadas: {
          props.rows?.length
          ?? 0
        }
      </div>
    ),
  }),
)

const useRulesQueryMock =
  vi.mocked(
    useRulesQuery,
  )

beforeEach(() => {
  useRulesQueryMock
    .mockReset()

  useRulesQueryMock
    .mockReturnValue(
      {
        data: [
          {
            ruleId:
              '00000000-0000-0000-0000-000000000001',
            ruleCode:
              'RULE-001',
            ruleName:
              'High Value Transaction Rule',
            description:
              'Rule page test',
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
        ],
        isError: false,
        isFetching: false,
      } as unknown as ReturnType<
        typeof useRulesQuery
      >,
    )
})

afterEach(() => {
  cleanup()
})

describe('RulesPage', () => {
  it(
    'loads and presents the rule catalog',
    () => {
      render(
        <RulesPage />,
      )

      expect(
        useRulesQueryMock,
      ).toHaveBeenCalledTimes(1)

      expect(
        screen.getByRole(
          'heading',
          {
            name: 'Reglas',
          },
        ),
      ).toBeTruthy()

      expect(
        screen.getByText(
          'Catálogo de reglas',
        ),
      ).toBeTruthy()

      expect(
        screen.getByText(
          'Reglas cargadas: 1',
        ),
      ).toBeTruthy()
    },
  )
})
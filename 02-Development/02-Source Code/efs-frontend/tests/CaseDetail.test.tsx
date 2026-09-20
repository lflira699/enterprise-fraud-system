import {
  cleanup,
  fireEvent,
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
import CaseDetail from '../src/modules/cases/components/CaseDetail'
import { useCaseQuery } from '../src/modules/cases/hooks/useCasesQuery'

vi.mock(
  '../src/modules/cases/hooks/useCasesQuery',
  () => ({
    useCaseQuery: vi.fn(),
  }),
)

vi.mock(
  '../src/modules/evidence/components/CaseEvidenceSection',
  () => ({
    default: ({
      caseId,
    }: {
      caseId: string
    }) => (
      <div data-testid="case-evidence-section">
        {caseId}
      </div>
    ),
  }),
)

const useCaseQueryMock =
  vi.mocked(
    useCaseQuery,
  )

const caseId =
  '44444444-4444-4444-4444-444444444444'

beforeEach(() => {
  useCaseQueryMock
    .mockReset()

  useCaseQueryMock
    .mockReturnValue(
      {
        data: {
          caseId,
          caseNumber:
            'CASE-004',
          organizationId:
            '55555555-5555-5555-5555-555555555555',
          transactionId: null,
          customerId: null,
          caseType:
            'FRAUD_INVESTIGATION',
          category:
            'ACCOUNT_TAKEOVER',
          severity: 'HIGH',
          priority: 'HIGH',
          currentStatus: 'OPEN',
          assignedTeam:
            'FRAUD_INVESTIGATION',
          assignedUser: null,
          createdAt:
            '2026-09-20T10:00:00',
          updatedAt:
            '2026-09-20T11:00:00',
          dueDate: null,
          closedAt: null,
          tenantId: null,
        },
        isError: false,
        isFetching: false,
      } as unknown as ReturnType<
        typeof useCaseQuery
      >,
    )
})

afterEach(() => {
  cleanup()
})

describe('CaseDetail', () => {
  it(
    'loads Case Review and exposes Evidence in the selected Case context',
    () => {
      render(
        <CaseDetail
          caseId={caseId}
          onBack={vi.fn()}
        />,
      )

      expect(
        useCaseQueryMock,
      ).toHaveBeenCalledWith(
        caseId,
      )

      expect(
        screen.getByRole(
          'heading',
          {
            name:
              'Detalle del caso',
          },
        ),
      ).toBeTruthy()

      expect(
        screen.getByText(
          'CASE-004',
        ),
      ).toBeTruthy()

      expect(
        screen.getByTestId(
          'case-evidence-section',
        ).textContent,
      ).toBe(
        caseId,
      )
    },
  )

  it(
    'returns to the Case list through the supplied callback',
    () => {
      const onBack =
        vi.fn()

      render(
        <CaseDetail
          caseId={caseId}
          onBack={onBack}
        />,
      )

      fireEvent.click(
        screen.getByRole(
          'button',
          {
            name:
              'Volver al listado',
          },
        ),
      )

      expect(
        onBack,
      ).toHaveBeenCalledTimes(1)
    },
  )
})

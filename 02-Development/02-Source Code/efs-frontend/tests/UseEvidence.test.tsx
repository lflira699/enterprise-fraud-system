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

import * as evidenceApi from '../src/modules/evidence/api/evidenceApi'
import { useCaseEvidenceQuery } from '../src/modules/evidence/hooks/useEvidence'

afterEach(() => {
  vi.restoreAllMocks()
})

describe('useCaseEvidenceQuery', () => {
  it(
    'loads Evidence for the supplied Case identifier',
    async () => {
      const caseId =
        '11111111-1111-1111-1111-111111111111'

      const response = [
        {
          evidenceId:
            '22222222-2222-2222-2222-222222222222',
          caseId,
          transactionId: null,
          evidenceType:
            'DOCUMENT',
          sourceSystem:
            'CASE_MANAGEMENT',
          storageUri: null,
          checksumSha256: null,
          uploadedBy: null,
          uploadedAt: null,
          evidenceCategory: null,
          evidenceName: null,
          evidenceDescription: null,
          validationStatus: null,
          confidentialityLevel: null,
          updatedAt: null,
          updatedBy: null,
        },
      ]

      const getCaseEvidenceMock =
        vi.spyOn(
          evidenceApi,
          'getCaseEvidence',
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
          () =>
            useCaseEvidenceQuery(
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
        getCaseEvidenceMock,
      ).toHaveBeenCalledTimes(1)

      expect(
        getCaseEvidenceMock,
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
    'does not query Evidence without a Case identifier',
    () => {
      const getCaseEvidenceMock =
        vi.spyOn(
          evidenceApi,
          'getCaseEvidence',
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
        () =>
          useCaseEvidenceQuery(
            null,
          ),
        {
          wrapper: Wrapper,
        },
      )

      expect(
        getCaseEvidenceMock,
      ).not.toHaveBeenCalled()

      queryClient.clear()
    },
  )
})

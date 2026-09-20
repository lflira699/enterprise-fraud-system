import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import {
  createCaseEvidence,
  deleteCaseEvidence,
  getCaseEvidence,
  getCaseEvidenceById,
  updateCaseEvidence,
} from '../src/modules/evidence/api/evidenceApi'
import { httpClient } from '../src/services/httpClient'

afterEach(() => {
  vi.restoreAllMocks()
})

describe('evidenceApi', () => {
  const caseId =
    '11111111-1111-1111-1111-111111111111'

  const evidenceId =
    '22222222-2222-2222-2222-222222222222'

  it(
    'uses the nested Case Evidence collection endpoint',
    async () => {
      const getMock =
        vi.spyOn(
          httpClient,
          'get',
        )
          .mockResolvedValue([])

      await getCaseEvidence(caseId)

      expect(getMock)
        .toHaveBeenCalledWith(
          `/cases/${caseId}/evidence`,
        )
    },
  )

  it(
    'uses the nested Case Evidence detail endpoint',
    async () => {
      const getMock =
        vi.spyOn(
          httpClient,
          'get',
        )
          .mockResolvedValue({})

      await getCaseEvidenceById(
        caseId,
        evidenceId,
      )

      expect(getMock)
        .toHaveBeenCalledWith(
          `/cases/${caseId}/evidence/${evidenceId}`,
        )
    },
  )

  it(
    'creates Evidence without client-controlled actor fields',
    async () => {
      const postMock =
        vi.spyOn(
          httpClient,
          'post',
        )
          .mockResolvedValue({})

      const request = {
        transactionId:
          '33333333-3333-3333-3333-333333333333',
        evidenceType:
          'DOCUMENT',
        sourceSystem:
          'CASE_MANAGEMENT',
        storageUri:
          'reference://evidence/1',
        checksumSha256:
          'abc123',
      }

      await createCaseEvidence(
        caseId,
        request,
      )

      expect(postMock)
        .toHaveBeenCalledWith(
          `/cases/${caseId}/evidence`,
          request,
        )

      expect(request)
        .not.toHaveProperty(
          'uploadedBy',
        )
    },
  )

  it(
    'updates Evidence through PATCH without client-controlled actor fields',
    async () => {
      const patchMock =
        vi.spyOn(
          httpClient,
          'patch',
        )
          .mockResolvedValue({})

      const request = {
        evidenceType:
          'DOCUMENT',
        evidenceCategory:
          'ACCOUNT',
        evidenceName:
          'Evidence name',
        evidenceDescription:
          'Evidence description',
        validationStatus:
          'VALIDATED',
        confidentialityLevel:
          'INTERNAL',
      }

      await updateCaseEvidence(
        caseId,
        evidenceId,
        request,
      )

      expect(patchMock)
        .toHaveBeenCalledWith(
          `/cases/${caseId}/evidence/${evidenceId}`,
          request,
        )

      expect(request)
        .not.toHaveProperty(
          'updatedBy',
        )
    },
  )

  it(
    'deletes Evidence through the nested endpoint without a request body',
    async () => {
      const deleteMock =
        vi.spyOn(
          httpClient,
          'delete',
        )
          .mockResolvedValue(
            undefined,
          )

      await deleteCaseEvidence(
        caseId,
        evidenceId,
      )

      expect(deleteMock)
        .toHaveBeenCalledWith(
          `/cases/${caseId}/evidence/${evidenceId}`,
        )
    },
  )
})

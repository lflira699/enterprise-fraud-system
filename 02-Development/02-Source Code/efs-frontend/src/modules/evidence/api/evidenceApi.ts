import { httpClient } from '../../../services/httpClient'
import type {
  CaseEvidence,
  CaseEvidenceCreateRequest,
  CaseEvidenceUpdateRequest,
} from '../types/evidence'

function caseEvidencePath(
  caseId: string,
) {
  return `/cases/${caseId}/evidence`
}

export function getCaseEvidence(
  caseId: string,
): Promise<CaseEvidence[]> {
  return httpClient.get<CaseEvidence[]>(
    caseEvidencePath(caseId),
  )
}

export function getCaseEvidenceById(
  caseId: string,
  evidenceId: string,
): Promise<CaseEvidence> {
  return httpClient.get<CaseEvidence>(
    `${caseEvidencePath(caseId)}/${evidenceId}`,
  )
}

export function createCaseEvidence(
  caseId: string,
  request: CaseEvidenceCreateRequest,
): Promise<CaseEvidence> {
  return httpClient.post<CaseEvidence>(
    caseEvidencePath(caseId),
    request,
  )
}

export function updateCaseEvidence(
  caseId: string,
  evidenceId: string,
  request: CaseEvidenceUpdateRequest,
): Promise<CaseEvidence> {
  return httpClient.patch<CaseEvidence>(
    `${caseEvidencePath(caseId)}/${evidenceId}`,
    request,
  )
}

export function deleteCaseEvidence(
  caseId: string,
  evidenceId: string,
): Promise<void> {
  return httpClient.delete<void>(
    `${caseEvidencePath(caseId)}/${evidenceId}`,
  )
}

export type CaseEvidence = {
  evidenceId: string
  caseId: string
  transactionId: string | null
  evidenceType: string
  sourceSystem: string
  storageUri: string | null
  checksumSha256: string | null
  uploadedBy: string | null
  uploadedAt: string | null
  evidenceCategory: string | null
  evidenceName: string | null
  evidenceDescription: string | null
  validationStatus: string | null
  confidentialityLevel: string | null
  updatedAt: string | null
  updatedBy: string | null
}

export type CaseEvidenceCreateRequest = {
  transactionId?: string | null
  evidenceType: string
  sourceSystem: string
  storageUri?: string | null
  checksumSha256?: string | null
}

export type CaseEvidenceUpdateRequest = {
  evidenceType?: string
  evidenceCategory?: string
  evidenceName?: string
  evidenceDescription?: string
  validationStatus?: string
  confidentialityLevel?: string
}

export type CreateCaseEvidenceMutation = {
  caseId: string
  request: CaseEvidenceCreateRequest
}

export type UpdateCaseEvidenceMutation = {
  caseId: string
  evidenceId: string
  request: CaseEvidenceUpdateRequest
}

export type DeleteCaseEvidenceMutation = {
  caseId: string
  evidenceId: string
}

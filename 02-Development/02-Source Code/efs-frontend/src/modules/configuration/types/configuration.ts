export type ConfigurationEffective = {
  configurationKey: string
  configurationValue: string | null
  configurationType: string
  effectiveScope: string
  organizationId: string | null
  tenantId: string | null
  encrypted: boolean
  critical: boolean
  updatedBy: string | null
  updatedAt: string | null
}

export type ConfigurationChangeItemRequest = {
  configurationKey: string
  proposedValue: string
}

export type ConfigurationChangeRequestCreateRequest = {
  tenantId?: string
  justification: string
  affectedEnvironment: string
  riskAssessment: string
  expectedResult: string
  rollbackPlan: string
  items: ConfigurationChangeItemRequest[]
}

export type ConfigurationChangeRequestItem = {
  changeItemId: string
  configurationKey: string
  previousValue: string | null
  proposedValue: string
  configurationType: string
  encrypted: boolean
  versionNumber: number
  createdAt: string
}

export type ConfigurationChangeRequestStatus =
  | 'PENDING_APPROVAL'
  | 'APPROVED'
  | 'APPLIED'
  | 'REJECTED'
  | 'FAILED'

export type ConfigurationChangeRequest = {
  changeRequestId: string
  organizationId: string
  tenantId: string | null
  requestedBy: string
  status: ConfigurationChangeRequestStatus
  justification: string
  affectedEnvironment: string
  riskAssessment: string
  expectedResult: string
  rollbackPlan: string
  requestedAt: string
  approvedBy: string | null
  approvedAt: string | null
  rejectedBy: string | null
  rejectedAt: string | null
  rejectionReason: string | null
  appliedBy: string | null
  appliedAt: string | null
  failureReason: string | null
  updatedAt: string
  items: ConfigurationChangeRequestItem[]
}

export type ConfigurationRejectRequest = {
  rejectionReason: string
}

export type ConfigurationVersion = {
  changeRequestId: string
  configurationKey: string
  configurationValue: string | null
  configurationType: string
  versionNumber: number
  organizationId: string
  tenantId: string | null
  encrypted: boolean
  appliedBy: string
  appliedAt: string
}

export type RejectConfigurationChangeRequestCommand = {
  changeRequestId: string
  request: ConfigurationRejectRequest
}
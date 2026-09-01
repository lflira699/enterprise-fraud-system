export type RiskAssessment = {
  riskAssessmentId: string
  transactionId: string
  assessmentType: string
  assessmentStage: string
  overallRiskScore: number
  riskLevel: string
  riskCategory: string | null
  assessmentResult: string
  rulesScore: number | null
  machineLearningScore: number | null
  behavioralScore: number | null
  customerScore: number | null
  geographicScore: number | null
  deviceScore: number | null
  confidenceScore: number | null
  modelName: string | null
  modelVersion: string | null
  assessmentTimestamp: string | null
  processingTimeMs: number | null
  assessmentDetails: Record<string, unknown> | null
  createdAt: string | null
  createdBy: string | null
  updatedAt: string | null
  updatedBy: string | null
  deletedAt: string | null
  deletedBy: string | null
  recordVersion: number | null
}

export type RiskAssessmentSortField =
  | 'assessmentTimestamp'

export type RiskAssessmentSortDirection =
  | 'ASC'
  | 'DESC'

export type RiskAssessmentSearchParams = {
  riskLevel?: string
  assessmentResult?: string
  page?: number
  size?: number
  sort?: RiskAssessmentSortField
  direction?: RiskAssessmentSortDirection
}
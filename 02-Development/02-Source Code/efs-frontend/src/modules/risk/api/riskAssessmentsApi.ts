import {
  httpClient,
  type QueryParameters,
} from '../../../services/httpClient'
import type { PageResponse } from '../../../types/pageResponse'
import type {
  RiskAssessment,
  RiskAssessmentSearchParams,
} from '../types/riskAssessment'

const RISK_ASSESSMENTS_PATH =
  '/risk-assessments'

export function getRiskAssessments(
  params: RiskAssessmentSearchParams,
): Promise<PageResponse<RiskAssessment>> {
  const query: QueryParameters = {
    riskLevel: params.riskLevel,
    assessmentResult:
      params.assessmentResult,
    page: params.page ?? 0,
    size: params.size ?? 25,
    sort:
      params.sort
      ?? 'assessmentTimestamp',
    direction:
      params.direction
      ?? 'DESC',
  }

  return httpClient.get<
    PageResponse<RiskAssessment>
  >(
    RISK_ASSESSMENTS_PATH,
    query,
  )
}
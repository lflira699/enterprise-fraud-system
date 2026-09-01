import { useQuery } from '@tanstack/react-query'

import { getRiskAssessments } from '../api/riskAssessmentsApi'
import type { RiskAssessmentSearchParams } from '../types/riskAssessment'

export const riskAssessmentsQueryKeys = {
  all: ['riskAssessments'] as const,

  list(
    params: RiskAssessmentSearchParams,
  ) {
    return [
      ...riskAssessmentsQueryKeys.all,
      'list',
      params,
    ] as const
  },
}

export function useRiskAssessmentsQuery(
  params: RiskAssessmentSearchParams,
) {
  return useQuery({
    queryKey:
      riskAssessmentsQueryKeys.list(
        params,
      ),

    queryFn:
      () => getRiskAssessments(
        params,
      ),
  })
}
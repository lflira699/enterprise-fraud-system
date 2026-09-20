import { useQuery } from '@tanstack/react-query'

import {
  getCaseById,
  getCases,
} from '../api/casesApi'
import type { CaseSearchParams } from '../types/case'

export const casesQueryKeys = {
  all: ['cases'] as const,

  list(
    params: CaseSearchParams,
  ) {
    return [
      ...casesQueryKeys.all,
      'list',
      params,
    ] as const
  },

  detail(
    caseId: string,
  ) {
    return [
      ...casesQueryKeys.all,
      'detail',
      caseId,
    ] as const
  },
}

export function useCasesQuery(
  params: CaseSearchParams,
) {
  return useQuery({
    queryKey:
      casesQueryKeys.list(
        params,
      ),

    queryFn:
      () => getCases(
        params,
      ),
  })
}

export function useCaseQuery(
  caseId: string | null,
) {
  return useQuery({
    queryKey:
      casesQueryKeys.detail(
        caseId ?? '',
      ),

    queryFn:
      () => getCaseById(
        caseId ?? '',
      ),

    enabled:
      caseId !== null
      && caseId !== '',
  })
}

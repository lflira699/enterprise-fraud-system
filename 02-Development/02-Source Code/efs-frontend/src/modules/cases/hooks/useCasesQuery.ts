import { useQuery } from '@tanstack/react-query'

import { getCases } from '../api/casesApi'
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
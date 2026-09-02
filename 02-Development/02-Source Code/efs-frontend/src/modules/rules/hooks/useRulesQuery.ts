import { useQuery } from '@tanstack/react-query'

import { getRules } from '../api/rulesApi'

export const rulesQueryKeys = {
  all: ['rules'] as const,

  list: [
    'rules',
    'list',
  ] as const,
}

export function useRulesQuery() {
  return useQuery({
    queryKey:
      rulesQueryKeys.list,

    queryFn:
      getRules,
  })
}
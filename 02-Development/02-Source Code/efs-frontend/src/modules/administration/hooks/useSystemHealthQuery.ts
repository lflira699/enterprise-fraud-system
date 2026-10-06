import { useQuery } from '@tanstack/react-query'

import { getSystemHealth } from '../api/systemHealthApi'

export const systemHealthQueryKeys = {
  all: ['system-health'] as const,
}

export function useSystemHealthQuery() {
  return useQuery({
    queryKey: systemHealthQueryKeys.all,
    queryFn: getSystemHealth,
  })
}

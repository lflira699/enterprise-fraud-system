import { httpClient } from '../../../services/httpClient'
import type { SystemHealth } from '../types/systemHealth'

const SYSTEM_HEALTH_PATH =
  '/health'

export function getSystemHealth(): Promise<SystemHealth> {
  return httpClient.get<SystemHealth>(
    SYSTEM_HEALTH_PATH,
  )
}

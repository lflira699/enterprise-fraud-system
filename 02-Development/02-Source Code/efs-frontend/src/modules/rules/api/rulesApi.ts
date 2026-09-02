import { httpClient } from '../../../services/httpClient'
import type { Rule } from '../types/rule'

const RULES_PATH =
  '/rules'

export function getRules(): Promise<Rule[]> {
  return httpClient.get<Rule[]>(
    RULES_PATH,
  )
}
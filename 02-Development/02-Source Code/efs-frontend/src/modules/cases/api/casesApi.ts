import {
  httpClient,
  type QueryParameters,
} from '../../../services/httpClient'
import type { PageResponse } from '../../../types/pageResponse'
import type {
  Case,
  CaseSearchParams,
} from '../types/case'

const CASES_PATH =
  '/cases'

export function getCases(
  params: CaseSearchParams,
): Promise<PageResponse<Case>> {
  const query: QueryParameters = {
    status: params.status,
    priority: params.priority,
    assignedUser: params.assignedUser,
    assignedTeam: params.assignedTeam,
    page: params.page ?? 0,
    size: params.size ?? 25,
    sort: params.sort ?? 'createdAt',
    direction: params.direction ?? 'DESC',
  }

  return httpClient.get<PageResponse<Case>>(
    CASES_PATH,
    query,
  )
}
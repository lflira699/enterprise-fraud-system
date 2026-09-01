import {
  httpClient,
  type QueryParameters,
} from '../../../services/httpClient'
import type { PageResponse } from '../../../types/pageResponse'
import type {
  Alert,
  AlertSearchParams,
} from '../types/alert'

const ALERTS_PATH =
  '/alerts'

export function getAlerts(
  params: AlertSearchParams,
): Promise<PageResponse<Alert>> {
  const query: QueryParameters = {
    status: params.status,
    priority: params.priority,
    riskLevel: params.riskLevel,
    assignedTo: params.assignedTo,
    createdFrom: params.createdFrom,
    createdTo: params.createdTo,
    customerId: params.customerId,
    scenarioCode: params.scenarioCode,
    caseId: params.caseId,
    page: params.page ?? 0,
    size: params.size ?? 25,
    sort: params.sort ?? 'generatedAt',
    direction: params.direction ?? 'DESC',
  }

  return httpClient.get<PageResponse<Alert>>(
    ALERTS_PATH,
    query,
  )
}
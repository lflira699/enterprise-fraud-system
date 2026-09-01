import {
  httpClient,
  type QueryParameters,
} from '../../../services/httpClient'
import type { PageResponse } from '../../../types/pageResponse'
import type {
  DetectionScenario,
  DetectionScenarioSearchParams,
} from '../types/detectionScenario'

const DETECTION_SCENARIOS_PATH =
  '/detection/scenarios'

export function getDetectionScenarios(
  params: DetectionScenarioSearchParams,
): Promise<PageResponse<DetectionScenario>> {
  const query: QueryParameters = {
    scenarioCode: params.scenarioCode,
    category: params.category,
    status: params.status,
    criticality: params.criticality,
    owner: params.owner,
    page: params.page ?? 0,
    size: params.size ?? 25,
    sort:
      params.sort
      ?? 'scenarioName',
    direction:
      params.direction
      ?? 'ASC',
  }

  return httpClient.get<
    PageResponse<DetectionScenario>
  >(
    DETECTION_SCENARIOS_PATH,
    query,
  )
}
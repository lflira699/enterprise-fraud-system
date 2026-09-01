export type DetectionScenario = {
  scenarioId: string
  scenarioCode: string
  scenarioName: string
  objective: string
  description: string | null
  category: string
  criticality: string | null
  status: string
  owner: string | null
  version: number
  correlationWindowMinutes: number | null
  maximumExecutionTimeSeconds: number | null
  minimumEvents: number | null
  minimumConfidence: number | null
  minimumEvidence: number | null
  requiredRules: Record<string, unknown> | null
  requiredVariables: Record<string, unknown> | null
  evidenceRequirements: Record<string, unknown> | null
  exclusions: Record<string, unknown> | null
  exceptions: Record<string, unknown> | null
  suggestedActions: Record<string, unknown> | null
  relatedScenarios: Record<string, unknown> | null
  configurationContext: Record<string, unknown> | null
  createdAt: string | null
  updatedAt: string | null
}

export type DetectionScenarioSortField =
  | 'scenarioName'

export type DetectionScenarioSortDirection =
  | 'ASC'
  | 'DESC'

export type DetectionScenarioSearchParams = {
  scenarioCode?: string
  category?: string
  status?: string
  criticality?: string
  owner?: string
  page?: number
  size?: number
  sort?: DetectionScenarioSortField
  direction?: DetectionScenarioSortDirection
}
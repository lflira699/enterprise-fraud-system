export type PlaybookExecution = {
  playbookExecutionId: string
  playbookVersionId: string
  alertId: string
  scenarioId: string
  status: string
  startedAt: string | null
  completedAt: string | null
  createdAt: string
  updatedAt: string
}

export type PlaybookExecutionStep = {
  playbookExecutionStepId: string
  playbookExecutionId: string
  playbookStepId: string
  status: string
  result: string | null
  startedAt: string | null
  completedAt: string | null
  createdAt: string
  updatedAt: string
}

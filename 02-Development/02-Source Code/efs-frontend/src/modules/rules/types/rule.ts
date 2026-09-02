export type Rule = {
  ruleId: string
  ruleCode: string
  ruleName: string
  description: string | null
  category: string
  severity: string
  priority: number | null
  ownerTeam: string | null
  currentVersion: number
  status: string
  createdAt: string | null
  updatedAt: string | null
}
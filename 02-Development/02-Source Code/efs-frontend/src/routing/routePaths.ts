export const ROUTE_PATHS = {
  dashboard: '/dashboard',
  events: '/events',
  rules: '/rules',
  detection: '/detection',
  risk: '/risk',
  alerts: '/alerts',
  alertDetail: '/alerts/:alertId',
  cases: '/cases',
  evidence: '/evidence',
  reports: '/reports',
  administration: '/administration',
  configuration: '/configuration',
  audit: '/audit',
} as const

export type RoutePath =
  (typeof ROUTE_PATHS)[keyof typeof ROUTE_PATHS]

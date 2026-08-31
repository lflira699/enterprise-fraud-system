import i18n from 'i18next'
import { initReactI18next } from 'react-i18next'

const resources = {
  es: {
    translation: {
      app: {
        title: 'Enterprise Fraud System',
      },
      navigation: {
        primary: 'Navegación principal',
        dashboard: 'Dashboard',
        events: 'Eventos',
        rules: 'Reglas',
        detection: 'Detección',
        risk: 'Riesgo',
        alerts: 'Alertas',
        cases: 'Casos',
        evidence: 'Evidencia',
        reports: 'Reportes',
        administration: 'Administración',
        configuration: 'Configuración',
        audit: 'Auditoría',
      },
    },
  },
} as const

void i18n
  .use(initReactI18next)
  .init({
    resources,
    lng: 'es',
    fallbackLng: 'es',
    interpolation: {
      escapeValue: false,
    },
  })

export default i18n
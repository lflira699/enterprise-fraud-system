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

      dashboard: {
        metrics: {
          criticalAlerts: 'Alertas críticas',
          openAlerts: 'Alertas abiertas',
          openCases: 'Casos abiertos',
          closedCases: 'Casos cerrados',
          averageRisk: 'Riesgo promedio',
          activatedDetectionScenarios:
            'Detection Scenarios activados',
        },
      },

      detection: {
        filters: {
          title: 'Filtros',
          scenarioCode:
            'Código de escenario',
          category:
            'Categoría',
          status:
            'Estado',
          criticality:
            'Criticidad',
          owner:
            'Responsable',
          apply:
            'Aplicar filtros',
          reset:
            'Limpiar filtros',
        },

        list: {
          title:
            'Detection Scenarios',
          error:
            'No fue posible cargar los Detection Scenarios.',
          noRows:
            'No hay Detection Scenarios para los filtros seleccionados.',
        },

        columns: {
          scenarioCode:
            'Código',
          scenarioName:
            'Nombre del escenario',
          category:
            'Categoría',
          criticality:
            'Criticidad',
          status:
            'Estado',
          owner:
            'Responsable',
          version:
            'Versión',
          minimumConfidence:
            'Confianza mínima',
          minimumEvents:
            'Eventos mínimos',
          minimumEvidence:
            'Evidencia mínima',
          updatedAt:
            'Actualizado',
        },
      },

      alerts: {
        filters: {
          title: 'Filtros',
          status: 'Estado',
          priority: 'Prioridad',
          riskLevel: 'Nivel de riesgo',
          assignedTo: 'Asignado a',
          createdFrom: 'Creado desde',
          createdTo: 'Creado hasta',
          customerId: 'ID de cliente',
          scenarioCode: 'Código de escenario',
          caseId: 'ID de caso',
          apply: 'Aplicar filtros',
          reset: 'Limpiar filtros',
        },

        list: {
          title: 'Listado de alertas',
          error:
            'No fue posible cargar las alertas.',
          noRows:
            'No hay alertas para los filtros seleccionados.',
        },

        columns: {
          reference: 'Referencia',
          title: 'Título',
          status: 'Estado',
          priority: 'Prioridad',
          priorityScore:
            'Puntaje de prioridad',
          severity: 'Severidad',
          riskScore: 'Puntaje de riesgo',
          assignedTeam: 'Equipo asignado',
          generatedAt: 'Generada',
          dueAt: 'Vencimiento',
        },
      },

      cases: {
        filters: {
          title: 'Filtros',
          status: 'Estado',
          priority: 'Prioridad',
          assignedUser:
            'Usuario asignado',
          assignedTeam:
            'Equipo asignado',
          apply: 'Aplicar filtros',
          reset: 'Limpiar filtros',
        },

        list: {
          title: 'Listado de casos',
          error:
            'No fue posible cargar los casos.',
          noRows:
            'No hay casos para los filtros seleccionados.',
        },

        columns: {
          caseNumber:
            'Número de caso',
          caseType:
            'Tipo de caso',
          status: 'Estado',
          priority: 'Prioridad',
          severity: 'Severidad',
          assignedTeam:
            'Equipo asignado',
          assignedUser:
            'Usuario asignado',
          createdAt:
            'Creado',
          dueDate:
            'Vencimiento',
        },
      },

      risk: {
        filters: {
          title: 'Filtros',
          riskLevel:
            'Nivel de riesgo',
          assessmentResult:
            'Resultado de evaluación',
          apply:
            'Aplicar filtros',
          reset:
            'Limpiar filtros',
        },

        list: {
          title:
            'Evaluaciones de riesgo',
          error:
            'No fue posible cargar las evaluaciones de riesgo.',
          noRows:
            'No hay evaluaciones de riesgo para los filtros seleccionados.',
        },

        columns: {
          transactionId:
            'ID de transacción',
          assessmentType:
            'Tipo de evaluación',
          assessmentStage:
            'Etapa de evaluación',
          overallRiskScore:
            'Puntaje de riesgo',
          riskLevel:
            'Nivel de riesgo',
          riskCategory:
            'Categoría de riesgo',
          assessmentResult:
            'Resultado',
          confidenceScore:
            'Nivel de confianza',
          modelName:
            'Modelo',
          assessmentTimestamp:
            'Evaluada',
        },
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
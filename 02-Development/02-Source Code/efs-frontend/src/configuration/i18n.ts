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

      reports: {
        generation: {
          title:
            'Generación de reportes',
          definition:
            'Tipo de reporte',
          definitionsError:
            'No fue posible cargar las definiciones de reportes.',
          generate:
            'Generar reporte',
          error:
            'No fue posible generar el reporte.',
        },

        criteria: {
          tenantId:
            'ID de tenant',
          components:
            'Componentes',
          componentsHelp:
            'Separar múltiples componentes con comas.',
          status:
            'Estado',
          priority:
            'Prioridad',
          assignedUser:
            'Usuario asignado',
          assignedTeam:
            'Equipo asignado',
        },

        lookup: {
          title:
            'Consultar reporte generado',
          reportId:
            'ID de reporte',
          load:
            'Cargar reporte',
          error:
            'No fue posible cargar el reporte.',
        },

        result: {
          title:
            'Reporte generado',
          reportId:
            'ID de reporte',
          reportCode:
            'Código de reporte',
          generatedAt:
            'Generado',
          content:
            'Contenido',
        },

        export: {
          title:
            'Exportación',
          format:
            'Formato',
          download:
            'Descargar',
          optionsError:
            'No fue posible cargar las opciones de exportación.',
          error:
            'No fue posible exportar el reporte.',
        },
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

      rules: {
        list: {
          title:
            'Catálogo de reglas',
          error:
            'No fue posible cargar las reglas.',
          noRows:
            'No hay reglas disponibles.',
        },

        columns: {
          ruleCode:
            'Código',
          ruleName:
            'Nombre de la regla',
          category:
            'Categoría',
          severity:
            'Severidad',
          priority:
            'Prioridad',
          ownerTeam:
            'Equipo responsable',
          currentVersion:
            'Versión',
          status:
            'Estado',
          updatedAt:
            'Actualizada',
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

        detail: {
          title:
            'Detalle del caso',
          back:
            'Volver al listado',
          loading:
            'Cargando detalle del caso.',
          error:
            'No fue posible cargar el detalle del caso.',

          fields: {
            caseId:
              'ID de caso',
            caseNumber:
              'Número de caso',
            caseType:
              'Tipo de caso',
            category:
              'Categoría',
            status:
              'Estado',
            priority:
              'Prioridad',
            severity:
              'Severidad',
            assignedTeam:
              'Equipo asignado',
            assignedUser:
              'Usuario asignado',
            transactionId:
              'ID de transacción',
            customerId:
              'ID de cliente',
            organizationId:
              'ID de organización',
            tenantId:
              'ID de tenant',
            createdAt:
              'Creado',
            updatedAt:
              'Actualizado',
            dueDate:
              'Vencimiento',
            closedAt:
              'Cerrado',
          },
        },
      },

      evidence: {
        create: {
          open:
            'Registrar evidencia',
          title:
            'Registrar evidencia',
          submit:
            'Registrar evidencia',
          cancel:
            'Cancelar',
          error:
            'No fue posible registrar la evidencia.',
        },

        edit: {
          title:
            'Actualizar evidencia',
          save:
            'Guardar cambios',
          cancel:
            'Cancelar',
          error:
            'No fue posible actualizar la evidencia.',
        },

        delete: {
          title:
            'Eliminar evidencia',
          message:
            'Confirme la eliminación de la evidencia seleccionada.',
          confirm:
            'Confirmar eliminación',
          cancel:
            'Cancelar',
          error:
            'No fue posible eliminar la evidencia.',
        },

        list: {
          title:
            'Evidencia del caso',
          error:
            'No fue posible cargar la evidencia del caso.',
          noRows:
            'No hay evidencia disponible para el caso.',
        },

        fields: {
          transactionId:
            'ID de transacción',
          evidenceType:
            'Tipo de evidencia',
          sourceSystem:
            'Sistema de origen',
          storageUri:
            'Referencia de almacenamiento',
          checksumSha256:
            'Checksum SHA-256',
          evidenceCategory:
            'Categoría',
          evidenceName:
            'Nombre',
          evidenceDescription:
            'Descripción',
          validationStatus:
            'Estado de validación',
          confidentialityLevel:
            'Nivel de confidencialidad',
        },

        columns: {
          evidenceType:
            'Tipo',
          evidenceCategory:
            'Categoría',
          evidenceName:
            'Nombre',
          sourceSystem:
            'Sistema de origen',
          validationStatus:
            'Estado de validación',
          uploadedAt:
            'Registrada',
          actions:
            'Acciones',
        },

        actions: {
          edit:
            'Editar',
          delete:
            'Eliminar',
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

import {
  cleanup,
  fireEvent,
  render,
  screen,
} from '@testing-library/react'
import {
  afterEach,
  beforeEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import '../src/configuration/i18n'
import {
  useApproveConfigurationChangeRequestMutation,
  useConfigurationChangeRequestQuery,
  useConfigurationVersionsQuery,
  useCreateConfigurationChangeRequestMutation,
  useEffectiveConfigurationsQuery,
  usePublishConfigurationChangeRequestMutation,
  useRejectConfigurationChangeRequestMutation,
} from '../src/modules/configuration/hooks/useConfiguration'
import ConfigurationPage from '../src/modules/configuration/pages/ConfigurationPage'

vi.mock(
  '../src/modules/configuration/hooks/useConfiguration',
  () => ({
    useEffectiveConfigurationsQuery:
      vi.fn(),
    useConfigurationChangeRequestQuery:
      vi.fn(),
    useConfigurationVersionsQuery:
      vi.fn(),
    useCreateConfigurationChangeRequestMutation:
      vi.fn(),
    useApproveConfigurationChangeRequestMutation:
      vi.fn(),
    useRejectConfigurationChangeRequestMutation:
      vi.fn(),
    usePublishConfigurationChangeRequestMutation:
      vi.fn(),
  }),
)

const createMutate =
  vi.fn()

const approveMutate =
  vi.fn()

const rejectMutate =
  vi.fn()

const publishMutate =
  vi.fn()

function configureDefaultMocks() {
  vi.mocked(
    useEffectiveConfigurationsQuery,
  ).mockReturnValue({
    data: [],
    isLoading: false,
    isError: false,
  } as never)

  vi.mocked(
    useConfigurationChangeRequestQuery,
  ).mockReturnValue({
    data: undefined,
    isLoading: false,
    isError: false,
  } as never)

  vi.mocked(
    useConfigurationVersionsQuery,
  ).mockReturnValue({
    data: [],
    isLoading: false,
    isError: false,
  } as never)

  vi.mocked(
    useCreateConfigurationChangeRequestMutation,
  ).mockReturnValue({
    mutate: createMutate,
    isPending: false,
    isError: false,
  } as never)

  vi.mocked(
    useApproveConfigurationChangeRequestMutation,
  ).mockReturnValue({
    mutate: approveMutate,
    isPending: false,
    isError: false,
  } as never)

  vi.mocked(
    useRejectConfigurationChangeRequestMutation,
  ).mockReturnValue({
    mutate: rejectMutate,
    isPending: false,
    isError: false,
  } as never)

  vi.mocked(
    usePublishConfigurationChangeRequestMutation,
  ).mockReturnValue({
    mutate: publishMutate,
    isPending: false,
    isError: false,
  } as never)
}

beforeEach(() => {
  vi.clearAllMocks()
  configureDefaultMocks()
})

afterEach(() => {
  cleanup()
})

describe(
  'ConfigurationPage',
  () => {
    it(
      'renders effective configuration values',
      () => {
        vi.mocked(
          useEffectiveConfigurationsQuery,
        ).mockReturnValue({
          data: [
            {
              configurationKey:
                'sample.key',
              configurationValue:
                'sample-value',
              configurationType:
                'STRING',
              effectiveScope:
                'ORGANIZATION',
              organizationId:
                'organization-1',
              tenantId:
                null,
              encrypted:
                false,
              critical:
                true,
              updatedBy:
                null,
              updatedAt:
                null,
            },
          ],
          isLoading: false,
          isError: false,
        } as never)

        render(
          <ConfigurationPage />,
        )

        expect(
          screen.getByRole(
            'heading',
            {
              name:
                'Configuración del sistema',
            },
          ),
        ).toBeTruthy()

        expect(
          screen.getByText(
            'sample.key',
          ),
        ).toBeTruthy()

        expect(
          screen.getByText(
            'sample-value',
          ),
        ).toBeTruthy()
      },
    )

    it(
      'applies tenant scope to effective configuration query',
      () => {
        render(
          <ConfigurationPage />,
        )

        fireEvent.change(
          screen.getByLabelText(
            'ID de tenant',
          ),
          {
            target: {
              value:
                'tenant-123',
            },
          },
        )

        expect(
          vi.mocked(
            useEffectiveConfigurationsQuery,
          ),
        ).toHaveBeenLastCalledWith(
          'tenant-123',
        )
      },
    )

    it(
      'submits a governed configuration change request',
      () => {
        render(
          <ConfigurationPage />,
        )

        const values:
          Array<[string, string]> = [
            [
              'Justificación',
              'Operational requirement',
            ],
            [
              'Ambiente afectado',
              'DEV',
            ],
            [
              'Evaluación de riesgo',
              'Low',
            ],
            [
              'Resultado esperado',
              'Updated behavior',
            ],
            [
              'Plan de reversión',
              'Restore previous value',
            ],
            [
              'Clave de configuración',
              'sample.key',
            ],
            [
              'Valor propuesto',
              'new-value',
            ],
          ]

        for (
          const [
            label,
            value,
          ] of values
        ) {
          fireEvent.change(
            screen.getAllByLabelText(
              label,
              { exact: false },
            )[0],
            {
              target: {
                value,
              },
            },
          )
        }

        fireEvent.click(
          screen.getByRole(
            'button',
            {
              name:
                'Crear solicitud',
            },
          ),
        )

        expect(
          createMutate,
        ).toHaveBeenCalledWith(
          {
            tenantId:
              undefined,
            justification:
              'Operational requirement',
            affectedEnvironment:
              'DEV',
            riskAssessment:
              'Low',
            expectedResult:
              'Updated behavior',
            rollbackPlan:
              'Restore previous value',
            items: [
              {
                configurationKey:
                  'sample.key',
                proposedValue:
                  'new-value',
              },
            ],
          },
          expect.objectContaining({
            onSuccess:
              expect.any(Function),
          }),
        )
      },
    )

    it(
      'exposes approve reject and publish actions for a retrieved request',
      () => {
        vi.mocked(
          useConfigurationChangeRequestQuery,
        ).mockReturnValue({
          data: {
            changeRequestId:
              'request-44',
            organizationId:
              'organization-1',
            tenantId:
              null,
            requestedBy:
              'user-1',
            status:
              'PENDING_APPROVAL',
            justification:
              'Required change',
            affectedEnvironment:
              'DEV',
            riskAssessment:
              'Low',
            expectedResult:
              'Updated behavior',
            rollbackPlan:
              'Restore prior value',
            requestedAt:
              '2026-09-24T12:00:00',
            approvedBy:
              null,
            approvedAt:
              null,
            rejectedBy:
              null,
            rejectedAt:
              null,
            rejectionReason:
              null,
            appliedBy:
              null,
            appliedAt:
              null,
            failureReason:
              null,
            updatedAt:
              '2026-09-24T12:00:00',
            items: [],
          },
          isLoading: false,
          isError: false,
        } as never)

        render(
          <ConfigurationPage />,
        )

        fireEvent.click(
          screen.getByRole(
            'button',
            {
              name: 'Aprobar',
            },
          ),
        )

        fireEvent.click(
          screen.getByRole(
            'button',
            {
              name: 'Publicar',
            },
          ),
        )

        fireEvent.change(
          screen.getByLabelText(
            'Motivo de rechazo',
          ),
          {
            target: {
              value:
                'Not authorized',
            },
          },
        )

        fireEvent.click(
          screen.getByRole(
            'button',
            {
              name: 'Rechazar',
            },
          ),
        )

        expect(
          approveMutate,
        ).toHaveBeenCalledWith(
          'request-44',
        )

        expect(
          publishMutate,
        ).toHaveBeenCalledWith(
          'request-44',
        )

        expect(
          rejectMutate,
        ).toHaveBeenCalledWith({
          changeRequestId:
            'request-44',
          request: {
            rejectionReason:
              'Not authorized',
          },
        })
      },
    )
  },
)
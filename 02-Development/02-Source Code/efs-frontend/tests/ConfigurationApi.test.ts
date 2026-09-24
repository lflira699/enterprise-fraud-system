import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import {
  approveConfigurationChangeRequest,
  createConfigurationChangeRequest,
  getConfigurationChangeRequest,
  getConfigurationVersions,
  getEffectiveConfiguration,
  getEffectiveConfigurations,
  publishConfigurationChangeRequest,
  rejectConfigurationChangeRequest,
} from '../src/modules/configuration/api/configurationApi'
import { httpClient } from '../src/services/httpClient'

afterEach(() => {
  vi.restoreAllMocks()
})

describe(
  'configurationApi',
  () => {
    it(
      'loads effective configuration list with tenant scope',
      async () => {
        const getMock =
          vi.spyOn(
            httpClient,
            'get',
          )
            .mockResolvedValue([])

        await getEffectiveConfigurations(
          'tenant-1',
        )

        expect(
          getMock,
        ).toHaveBeenCalledWith(
          '/admin/configuration/effective',
          {
            tenantId: 'tenant-1',
          },
        )
      },
    )

    it(
      'loads one effective configuration using encoded key',
      async () => {
        const getMock =
          vi.spyOn(
            httpClient,
            'get',
          )
            .mockResolvedValue({})

        await getEffectiveConfiguration(
          'risk threshold',
          'tenant-2',
        )

        expect(
          getMock,
        ).toHaveBeenCalledWith(
          '/admin/configuration/effective/risk%20threshold',
          {
            tenantId: 'tenant-2',
          },
        )
      },
    )

    it(
      'creates a configuration change request',
      async () => {
        const postMock =
          vi.spyOn(
            httpClient,
            'post',
          )
            .mockResolvedValue({})

        const request = {
          tenantId: 'tenant-3',
          justification: 'Required change',
          affectedEnvironment: 'DEV',
          riskAssessment: 'Low',
          expectedResult: 'Updated value',
          rollbackPlan: 'Restore prior value',
          items: [
            {
              configurationKey:
                'sample.key',
              proposedValue:
                'sample-value',
            },
          ],
        }

        await createConfigurationChangeRequest(
          request,
        )

        expect(
          postMock,
        ).toHaveBeenCalledWith(
          '/admin/configuration/change-requests',
          request,
        )
      },
    )

    it(
      'retrieves a configuration change request',
      async () => {
        const getMock =
          vi.spyOn(
            httpClient,
            'get',
          )
            .mockResolvedValue({})

        await getConfigurationChangeRequest(
          'request-1',
        )

        expect(
          getMock,
        ).toHaveBeenCalledWith(
          '/admin/configuration/change-requests/request-1',
        )
      },
    )

    it(
      'approves and publishes a configuration change request',
      async () => {
        const postMock =
          vi.spyOn(
            httpClient,
            'post',
          )
            .mockResolvedValue({})

        await approveConfigurationChangeRequest(
          'request-2',
        )

        await publishConfigurationChangeRequest(
          'request-2',
        )

        expect(
          postMock,
        ).toHaveBeenNthCalledWith(
          1,
          '/admin/configuration/change-requests/request-2/approve',
        )

        expect(
          postMock,
        ).toHaveBeenNthCalledWith(
          2,
          '/admin/configuration/change-requests/request-2/publish',
        )
      },
    )

    it(
      'rejects a configuration change request with reason',
      async () => {
        const postMock =
          vi.spyOn(
            httpClient,
            'post',
          )
            .mockResolvedValue({})

        await rejectConfigurationChangeRequest({
          changeRequestId:
            'request-3',
          request: {
            rejectionReason:
              'Insufficient evidence',
          },
        })

        expect(
          postMock,
        ).toHaveBeenCalledWith(
          '/admin/configuration/change-requests/request-3/reject',
          {
            rejectionReason:
              'Insufficient evidence',
          },
        )
      },
    )

    it(
      'loads applied versions with tenant scope',
      async () => {
        const getMock =
          vi.spyOn(
            httpClient,
            'get',
          )
            .mockResolvedValue([])

        await getConfigurationVersions(
          'sample.key',
          'tenant-4',
        )

        expect(
          getMock,
        ).toHaveBeenCalledWith(
          '/admin/configuration/sample.key/versions',
          {
            tenantId: 'tenant-4',
          },
        )
      },
    )
  },
)
import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import {
  getPlaybookExecutionsByAlertId,
  getPlaybookExecutionSteps,
} from '../src/modules/playbook/api/playbookApi'
import { httpClient } from '../src/services/httpClient'

vi.mock(
  '../src/services/httpClient',
  () => ({
    httpClient: {
      get: vi.fn(),
    },
  }),
)

const httpGetMock =
  vi.mocked(
    httpClient.get,
  )

afterEach(() => {
  vi.clearAllMocks()
})

describe(
  'playbookApi',
  () => {
    it(
      'retrieves playbook executions for an alert using the approved GET binding',
      async () => {
        httpGetMock.mockResolvedValue([])

        await getPlaybookExecutionsByAlertId(
          '11111111-1111-1111-1111-111111111111',
        )

        expect(
          httpGetMock,
        ).toHaveBeenCalledWith(
          '/playbook-executions/alert/11111111-1111-1111-1111-111111111111',
        )
      },
    )

    it(
      'retrieves execution steps using the approved GET binding',
      async () => {
        httpGetMock.mockResolvedValue([])

        await getPlaybookExecutionSteps(
          '22222222-2222-2222-2222-222222222222',
        )

        expect(
          httpGetMock,
        ).toHaveBeenCalledWith(
          '/playbook-execution-steps/execution/22222222-2222-2222-2222-222222222222',
        )
      },
    )
  },
)

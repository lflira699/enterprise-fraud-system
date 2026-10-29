import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

const getValidAccessTokenMock = vi.hoisted(
  () => vi.fn(),
)

vi.mock('../src/security/keycloak', () => ({
  getValidAccessToken: getValidAccessTokenMock,
}))

import { getSystemHealth } from '../src/modules/administration/api/systemHealthApi'

describe('System Health API', () => {
  afterEach(() => {
    getValidAccessTokenMock.mockReset()
    vi.unstubAllGlobals()
    vi.restoreAllMocks()
  })

  it('retrieves system health from the canonical endpoint', async () => {
    getValidAccessTokenMock.mockResolvedValue(
      'test-access-token',
    )

    const payload = {
      status: 'UP',
      informationStatus: 'COMPLETE',
      components: [
        {
          name: 'database',
          status: 'UP',
        },
      ],
      checkedAt: '2026-09-17T07:00:00',
    }

    const fetchMock =
      vi.fn().mockResolvedValue(
        new Response(
          JSON.stringify(payload),
          {
            status: 200,
            headers: {
              'Content-Type': 'application/json',
            },
          },
        ),
      )

    vi.stubGlobal(
      'fetch',
      fetchMock,
    )

    const response =
      await getSystemHealth()

    expect(response).toEqual(payload)

    expect(fetchMock).toHaveBeenCalledTimes(1)

    expect(
      fetchMock.mock.calls[0]?.[0],
    ).toBe('/api/v1/health')

    const requestInit =
      fetchMock.mock.calls[0]?.[1] as RequestInit

    expect(
      requestInit.method,
    ).toBe('GET')

    expect(
      new Headers(requestInit.headers).get(
        'Authorization',
      ),
    ).toBe('Bearer test-access-token')
  })
})

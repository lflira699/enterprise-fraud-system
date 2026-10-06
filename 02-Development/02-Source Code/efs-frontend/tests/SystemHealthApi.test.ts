import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import { getSystemHealth } from '../src/modules/administration/api/systemHealthApi'

describe('System Health API', () => {
  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('retrieves system health from the canonical endpoint', async () => {
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
  })
})

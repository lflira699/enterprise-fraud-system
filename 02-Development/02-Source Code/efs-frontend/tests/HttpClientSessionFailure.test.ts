import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

const { getValidAccessTokenMock } = vi.hoisted(
  () => ({
    getValidAccessTokenMock: vi.fn(),
  }),
)

vi.mock('../src/security/keycloak', () => ({
  getValidAccessToken: getValidAccessTokenMock,
}))

import {
  httpClient,
  subscribeToAuthenticationFailure,
} from '../src/services/httpClient'

const subscriptions: Array<() => void> = []

afterEach(() => {
  for (const unsubscribe of subscriptions.splice(0)) {
    unsubscribe()
  }

  getValidAccessTokenMock.mockReset()
  vi.unstubAllGlobals()
  vi.restoreAllMocks()
})

describe('HTTP authentication failure notification', () => {
  it('notifies on backend 401 while preserving HttpError', async () => {
    getValidAccessTokenMock.mockResolvedValue('valid-token')

    const listener = vi.fn()
    subscriptions.push(
      subscribeToAuthenticationFailure(listener),
    )

    const fetchMock = vi.fn().mockResolvedValue(
      new Response(null, {
        status: 401,
        statusText: 'Unauthorized',
      }),
    )

    vi.stubGlobal('fetch', fetchMock)

    await expect(
      httpClient.get('/events'),
    ).rejects.toMatchObject({
      name: 'HttpError',
      status: 401,
    })

    expect(fetchMock).toHaveBeenCalledTimes(1)
    expect(listener).toHaveBeenCalledTimes(1)
  })

  it('preserves authorization failure without notifying on 403', async () => {
    getValidAccessTokenMock.mockResolvedValue('valid-token')

    const listener = vi.fn()
    subscriptions.push(
      subscribeToAuthenticationFailure(listener),
    )

    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(
        new Response(null, {
          status: 403,
          statusText: 'Forbidden',
        }),
      ),
    )

    await expect(
      httpClient.get('/events'),
    ).rejects.toMatchObject({
      name: 'HttpError',
      status: 403,
    })

    expect(listener).not.toHaveBeenCalled()
  })

  it('notifies when no valid access token exists without fetching', async () => {
    getValidAccessTokenMock.mockResolvedValue(null)

    const listener = vi.fn()
    subscriptions.push(
      subscribeToAuthenticationFailure(listener),
    )

    const fetchMock = vi.fn()
    vi.stubGlobal('fetch', fetchMock)

    await expect(
      httpClient.get('/events'),
    ).rejects.toMatchObject({
      name: 'HttpError',
      status: 401,
      message: 'Authentication required',
    })

    expect(fetchMock).not.toHaveBeenCalled()
    expect(listener).toHaveBeenCalledTimes(1)
  })

  it('does not notify on successful HTTP responses', async () => {
    getValidAccessTokenMock.mockResolvedValue('valid-token')

    const listener = vi.fn()
    subscriptions.push(
      subscribeToAuthenticationFailure(listener),
    )

    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(
        new Response('{}', {
          status: 200,
          headers: {
            'Content-Type': 'application/json',
          },
        }),
      ),
    )

    await expect(
      httpClient.get('/events'),
    ).resolves.toEqual({})

    expect(listener).not.toHaveBeenCalled()
  })

  it('stops notifying after the subscription is removed', async () => {
    getValidAccessTokenMock.mockResolvedValue('valid-token')

    const listener = vi.fn()
    const unsubscribe =
      subscribeToAuthenticationFailure(listener)

    unsubscribe()

    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(
        new Response(null, {
          status: 401,
          statusText: 'Unauthorized',
        }),
      ),
    )

    await expect(
      httpClient.get('/events'),
    ).rejects.toMatchObject({
      status: 401,
    })

    expect(listener).not.toHaveBeenCalled()
  })
})

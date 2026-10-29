import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

const {
  getValidAccessTokenMock,
} = vi.hoisted(() => ({
  getValidAccessTokenMock: vi.fn(),
}))

vi.mock(
  '../src/security/keycloak',
  () => ({
    getValidAccessToken:
      getValidAccessTokenMock,
  }),
)

import {
  httpClient,
} from '../src/services/httpClient'

afterEach(() => {
  getValidAccessTokenMock.mockReset()
  vi.unstubAllGlobals()
  vi.restoreAllMocks()
})

describe(
  'httpClient authentication binding',
  () => {
    it(
      'adds the Keycloak Access Token as Bearer authorization',
      async () => {
        getValidAccessTokenMock
          .mockResolvedValue(
            'access-token',
          )

        const fetchMock =
          vi.fn()
            .mockResolvedValue(
              new Response(
                JSON.stringify({
                  content: [],
                }),
                {
                  status: 200,
                  headers: {
                    'Content-Type':
                      'application/json',
                  },
                },
              ),
            )

        vi.stubGlobal(
          'fetch',
          fetchMock,
        )

        await httpClient.get(
          '/events',
        )

        expect(
          fetchMock,
        ).toHaveBeenCalledTimes(1)

        const [, init] =
          fetchMock.mock.calls[0] as [
            string,
            RequestInit,
          ]

        const headers =
          new Headers(
            init.headers,
          )

        expect(
          headers.get(
            'Authorization',
          ),
        ).toBe(
          'Bearer access-token',
        )

        expect(
          headers.get(
            'X-Correlation-ID',
          ),
        ).toBeTruthy()
      },
    )

    it(
      'rejects a protected request when no valid Access Token exists',
      async () => {
        getValidAccessTokenMock
          .mockResolvedValue(null)

        const fetchMock = vi.fn()

        vi.stubGlobal(
          'fetch',
          fetchMock,
        )

        await expect(
          httpClient.get('/events'),
        ).rejects.toMatchObject({
          name: 'HttpError',
          status: 401,
          message: 'Authentication required',
          payload: null,
        })

        expect(fetchMock)
          .not.toHaveBeenCalled()
      },
    )
  },
)
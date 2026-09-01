import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import {
  HttpError,
  httpClient,
} from '../src/services/httpClient'

type MockResponseOptions = {
  ok: boolean
  status: number
  statusText: string
  body: string
}

function createMockResponse(
  options: MockResponseOptions,
): Response {
  return {
    ok: options.ok,
    status: options.status,
    statusText: options.statusText,
    text: vi.fn()
      .mockResolvedValue(
        options.body,
      ),
  } as unknown as Response
}

afterEach(() => {
  vi.restoreAllMocks()
})

describe('httpClient', () => {
  it(
    'builds the API URL and excludes empty query parameters',
    async () => {
      const fetchMock =
        vi.spyOn(
          globalThis,
          'fetch',
        )
          .mockResolvedValue(
            createMockResponse({
              ok: true,
              status: 200,
              statusText: 'OK',
              body: JSON.stringify({
                content: [],
              }),
            }),
          )

      await httpClient.get(
        '/alerts',
        {
          page: 0,
          size: 25,
          status: 'NEW',
          empty: '',
          nullValue: null,
          undefinedValue: undefined,
        },
      )

      expect(
        fetchMock,
      ).toHaveBeenCalledTimes(1)

      const [
        requestUrl,
        requestInit,
      ] = fetchMock.mock.calls[0]

      expect(
        requestUrl,
      ).toBe(
        '/api/v1/alerts?page=0&size=25&status=NEW',
      )

      expect(
        requestInit.method,
      ).toBe('GET')

      const headers =
        new Headers(
          requestInit.headers,
        )

      expect(
        headers.get('Accept'),
      ).toBe('application/json')

      expect(
        headers.get(
          'X-Correlation-ID',
        ),
      ).toBeTruthy()
    },
  )

  it(
    'throws HttpError using the API error payload',
    async () => {
      vi.spyOn(
        globalThis,
        'fetch',
      )
        .mockResolvedValue(
          createMockResponse({
            ok: false,
            status: 400,
            statusText: 'Bad Request',
            body: JSON.stringify({
              status: 400,
              errorCode:
                'VALIDATION_ERROR',
              message:
                'Invalid alert status',
              path:
                '/api/v1/alerts',
            }),
          }),
        )

      try {
        await httpClient.get(
          '/alerts',
        )

        throw new Error(
          'Expected HTTP request to fail',
        )
      } catch (error) {
        expect(
          error,
        ).toBeInstanceOf(
          HttpError,
        )

        const httpError =
          error as HttpError

        expect(
          httpError.status,
        ).toBe(400)

        expect(
          httpError.message,
        ).toBe(
          'Invalid alert status',
        )

        expect(
          httpError.payload,
        ).toMatchObject({
          status: 400,
          errorCode:
            'VALIDATION_ERROR',
          message:
            'Invalid alert status',
          path:
            '/api/v1/alerts',
        })
      }
    },
  )
})
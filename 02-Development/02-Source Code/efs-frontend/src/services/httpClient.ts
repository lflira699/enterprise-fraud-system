const API_BASE_PATH = '/api/v1'

export type ApiErrorPayload = {
  timestamp?: string
  status?: number
  errorCode?: string
  message?: string
  correlationId?: string | null
  path?: string
  validationErrors?: Record<string, string>
}

export class HttpError extends Error {
  readonly status: number
  readonly payload: ApiErrorPayload | null

  constructor(
    status: number,
    message: string,
    payload: ApiErrorPayload | null,
  ) {
    super(message)

    this.name = 'HttpError'
    this.status = status
    this.payload = payload
  }
}

type QueryParameter =
  | string
  | number
  | boolean
  | null
  | undefined

export type QueryParameters =
  Record<string, QueryParameter>

function buildUrl(
  path: string,
  query?: QueryParameters,
) {
  const searchParams =
    new URLSearchParams()

  if (query) {
    Object.entries(query)
      .forEach(([key, value]) => {
        if (
          value !== undefined
          && value !== null
          && value !== ''
        ) {
          searchParams.set(
            key,
            String(value),
          )
        }
      })
  }

  const queryString =
    searchParams.toString()

  const url =
    `${API_BASE_PATH}${path}`

  if (!queryString) {
    return url
  }

  return `${url}?${queryString}`
}

async function parseResponseBody(
  response: Response,
): Promise<unknown> {
  const text =
    await response.text()

  if (!text) {
    return null
  }

  try {
    return JSON.parse(text)
  } catch {
    return text
  }
}

async function request<T>(
  path: string,
  init: RequestInit,
  query?: QueryParameters,
): Promise<T> {
  const headers =
    new Headers(init.headers)

  headers.set(
    'Accept',
    'application/json',
  )

  if (
    init.body !== undefined
    && init.body !== null
    && !headers.has('Content-Type')
  ) {
    headers.set(
      'Content-Type',
      'application/json',
    )
  }

  if (!headers.has('X-Correlation-ID')) {
    headers.set(
      'X-Correlation-ID',
      crypto.randomUUID(),
    )
  }

  const response =
    await fetch(
      buildUrl(
        path,
        query,
      ),
      {
        ...init,
        headers,
      },
    )

  const responseBody =
    await parseResponseBody(
      response,
    )

  if (!response.ok) {
    const payload =
      typeof responseBody === 'object'
      && responseBody !== null
        ? responseBody as ApiErrorPayload
        : null

    const message =
      payload?.message
      ?? response.statusText
      ?? 'HTTP request failed'

    throw new HttpError(
      response.status,
      message,
      payload,
    )
  }

  return responseBody as T
}

export const httpClient = {
  get<T>(
    path: string,
    query?: QueryParameters,
  ): Promise<T> {
    return request<T>(
      path,
      {
        method: 'GET',
      },
      query,
    )
  },
}
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

export type BinaryHttpResponse = {
  blob: Blob
  mediaType: string | null
  fileName: string | null
}

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

function buildHeaders(
  init: RequestInit,
) {
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

  return headers
}

async function executeRequest(
  path: string,
  init: RequestInit,
  query?: QueryParameters,
): Promise<Response> {
  const response =
    await fetch(
      buildUrl(
        path,
        query,
      ),
      {
        ...init,
        headers:
          buildHeaders(init),
      },
    )

  if (!response.ok) {
    const responseBody =
      await parseResponseBody(
        response,
      )

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

  return response
}

async function request<T>(
  path: string,
  init: RequestInit,
  query?: QueryParameters,
): Promise<T> {
  const response =
    await executeRequest(
      path,
      init,
      query,
    )

  const responseBody =
    await parseResponseBody(
      response,
    )

  return responseBody as T
}

function serializeBody(
  body: unknown,
): BodyInit | undefined {
  if (body === undefined) {
    return undefined
  }

  return JSON.stringify(body)
}

function extractFileName(
  contentDisposition: string | null,
): string | null {
  if (!contentDisposition) {
    return null
  }

  const match =
    /filename="?([^";]+)"?/i.exec(
      contentDisposition,
    )

  return match?.[1] ?? null
}

async function requestBlob(
  path: string,
  init: RequestInit,
  query?: QueryParameters,
): Promise<BinaryHttpResponse> {
  const response =
    await executeRequest(
      path,
      init,
      query,
    )

  const blob =
    await response.blob()

  return {
    blob,
    mediaType:
      response.headers.get(
        'Content-Type',
      ),
    fileName:
      extractFileName(
        response.headers.get(
          'Content-Disposition',
        ),
      ),
  }
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

  post<T>(
    path: string,
    body?: unknown,
    query?: QueryParameters,
  ): Promise<T> {
    return request<T>(
      path,
      {
        method: 'POST',
        body:
          serializeBody(body),
      },
      query,
    )
  },

  postBlob(
    path: string,
    body?: unknown,
    query?: QueryParameters,
  ): Promise<BinaryHttpResponse> {
    return requestBlob(
      path,
      {
        method: 'POST',
        body:
          serializeBody(body),
      },
      query,
    )
  },
}

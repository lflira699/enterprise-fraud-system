import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

import { getRules } from '../src/modules/rules/api/rulesApi'
import { httpClient } from '../src/services/httpClient'

afterEach(() => {
  vi.restoreAllMocks()
})

describe('rulesApi', () => {
  it(
    'loads the rule catalog from the canonical endpoint',
    async () => {
      const getMock =
        vi.spyOn(
          httpClient,
          'get',
        )
          .mockResolvedValue([])

      await getRules()

      expect(
        getMock,
      ).toHaveBeenCalledTimes(1)

      expect(
        getMock,
      ).toHaveBeenCalledWith(
        '/rules',
      )
    },
  )
})
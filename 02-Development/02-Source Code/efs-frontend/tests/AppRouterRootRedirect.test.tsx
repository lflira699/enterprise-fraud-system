import { cleanup, render, screen, waitFor } from '@testing-library/react'
import type { ReactNode } from 'react'
import {
  MemoryRouter,
  useLocation,
} from 'react-router-dom'
import {
  afterEach,
  describe,
  expect,
  it,
  vi,
} from 'vitest'

vi.mock('../src/shared/layouts/AppLayout', () => ({
  default: ({ children }: { children: ReactNode }) => (
    <>{children}</>
  ),
}))

vi.mock('../src/modules/dashboard/pages/DashboardPage', () => ({
  default: () => (
    <div data-testid="dashboard-content">
      Dashboard
    </div>
  ),
}))

import AppRouter from '../src/routing/AppRouter'

afterEach(cleanup)

function LocationProbe() {
  const location = useLocation()

  return (
    <span data-testid="route-location">
      {location.pathname + location.search}
    </span>
  )
}

describe('AppRouter root redirect', () => {
  it.each([
    '/',
    '/?state=callback-state&code=callback-code',
  ])(
    'redirects root %s to canonical Dashboard',
    async (initialLocation) => {
      render(
        <MemoryRouter initialEntries={[initialLocation]}>
          <AppRouter />
          <LocationProbe />
        </MemoryRouter>,
      )

      await waitFor(() => {
        expect(
          screen.getByTestId('route-location').textContent,
        ).toBe('/dashboard')
      })

      expect(
        screen.getByTestId('dashboard-content'),
      ).toBeTruthy()
    },
  )
})

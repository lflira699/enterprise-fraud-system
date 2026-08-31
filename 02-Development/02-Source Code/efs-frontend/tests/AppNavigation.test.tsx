import {
  fireEvent,
  render,
  screen,
} from '@testing-library/react'
import {
  MemoryRouter,
  useLocation,
} from 'react-router-dom'
import {
  describe,
  expect,
  it,
} from 'vitest'

import '../src/configuration/i18n'
import AppNavigation from '../src/routing/AppNavigation'

function LocationProbe() {
  const location = useLocation()

  return (
    <span data-testid="current-location">
      {location.pathname}
    </span>
  )
}

describe('AppNavigation', () => {
  it('navigates using the route contract', async () => {
    render(
      <MemoryRouter
        initialEntries={['/dashboard']}
      >
        <AppNavigation />
        <LocationProbe />
      </MemoryRouter>,
    )

    expect(
      screen
        .getByTestId('current-location')
        .textContent,
    ).toBe('/dashboard')

    const alertsNavigation =
      await screen.findByRole(
        'button',
        {
          name: 'Alertas',
        },
      )

    fireEvent.click(alertsNavigation)

    expect(
      screen
        .getByTestId('current-location')
        .textContent,
    ).toBe('/alerts')
  })
})
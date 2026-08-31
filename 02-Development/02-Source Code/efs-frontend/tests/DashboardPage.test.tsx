import {
  render,
  screen,
} from '@testing-library/react'
import {
  describe,
  expect,
  it,
} from 'vitest'

import '../src/configuration/i18n'
import DashboardPage from '../src/modules/dashboard/pages/DashboardPage'

describe('DashboardPage', () => {
  it('renders the MVP dashboard metrics', () => {
    render(<DashboardPage />)

    expect(
      screen.getByText('Alertas críticas'),
    ).toBeTruthy()

    expect(
      screen.getByText('Alertas abiertas'),
    ).toBeTruthy()

    expect(
      screen.getByText('Casos abiertos'),
    ).toBeTruthy()

    expect(
      screen.getByText('Casos cerrados'),
    ).toBeTruthy()

    expect(
      screen.getByText('Riesgo promedio'),
    ).toBeTruthy()

    expect(
      screen.getByText(
        'Detection Scenarios activados',
      ),
    ).toBeTruthy()
  })
})
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import {
  QueryClient,
  QueryClientProvider,
} from '@tanstack/react-query'
import { BrowserRouter } from 'react-router-dom'

import App from './app/App'
import './configuration/i18n'
import {
  AuthenticationProvider,
} from './security/AuthenticationProvider'

const queryClient =
  new QueryClient()

const rootElement =
  document.getElementById('root')

if (!rootElement) {
  throw new Error('Root element not found')
}

createRoot(rootElement).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <AuthenticationProvider>
          <App />
        </AuthenticationProvider>
      </BrowserRouter>
    </QueryClientProvider>
  </StrictMode>,
)
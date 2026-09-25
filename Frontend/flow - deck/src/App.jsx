import ErrorBoundary from './components/common/ErrorBoundary'
import ToastProvider from './components/common/ToastProvider'
import AppRoutes from './routes/AppRoutes'

function App() {
  return (
    <ErrorBoundary>
      <ToastProvider>
        <AppRoutes />
      </ToastProvider>
    </ErrorBoundary>
  )
}

export default App

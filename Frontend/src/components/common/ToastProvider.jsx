import { Toaster } from 'react-hot-toast'

function ToastProvider({ children }) {
  return (
    <>
      {children}
      <Toaster
        position="top-right"
        toastOptions={{
          duration: 4000,
          style: {
            background: '#ffffff',
            color: '#0f172a',
            border: '1px solid #e2e8f0',
            borderRadius: '8px',
            boxShadow: '0 10px 15px -3px rgba(15, 23, 42, 0.08)',
            fontSize: '0.875rem',
            fontWeight: '500',
            padding: '0.75rem 1rem',
          },
          success: {
            iconTheme: {
              primary: '#0f766e',
              secondary: '#ffffff',
            },
          },
          error: {
            iconTheme: {
              primary: '#dc2626',
              secondary: '#ffffff',
            },
          },
        }}
      />
    </>
  )
}

export default ToastProvider


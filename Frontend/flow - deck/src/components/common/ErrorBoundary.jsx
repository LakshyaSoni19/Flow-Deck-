import { Component } from 'react'
import ErrorPage from '../../pages/ErrorPage'

class ErrorBoundary extends Component {
  state = { hasError: false }

  static getDerivedStateFromError() { return { hasError: true } }

  render() { return this.state.hasError ? <ErrorPage /> : this.props.children }
}

export default ErrorBoundary

import { API_ENDPOINTS } from '../api/apiEndpoints'
import { httpClient } from '../api/httpClient'

const get = async (endpoint) => (await httpClient.get(endpoint)).data

export const dashboardService = Object.freeze({
  getEmployeeDashboard: () => get(API_ENDPOINTS.EMPLOYEE.DASHBOARD),
  getProjectManagerOverview: (projectId) => get(API_ENDPOINTS.PROJECT_MANAGER.PROJECT_OVERVIEW(projectId)),
})

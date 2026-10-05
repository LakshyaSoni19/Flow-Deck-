import { API_ENDPOINTS } from '../api/apiEndpoints'
import { httpClient } from '../api/httpClient'

const get = (endpoint, params) => httpClient.get(endpoint, { params }).then((response) => response.data)

export const lookupService = Object.freeze({
  getDepartments: () => get(API_ENDPOINTS.LOOKUPS.DEPARTMENTS),
  getDesignations: () => get(API_ENDPOINTS.LOOKUPS.DESIGNATIONS),
  getCities: () => get(API_ENDPOINTS.LOOKUPS.CITIES),
  getTaskStatuses: () => get(API_ENDPOINTS.LOOKUPS.TASK_STATUSES),
  getTaskPriorities: () => get(API_ENDPOINTS.LOOKUPS.TASK_PRIORITIES),
  getTaskTypes: () => get(API_ENDPOINTS.LOOKUPS.TASK_TYPES),
  searchUsers: (query) => get(API_ENDPOINTS.LOOKUPS.USERS, { query }),
})

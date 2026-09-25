import { API_ENDPOINTS } from '../api/apiEndpoints'
import { httpClient } from '../api/httpClient'

export const employeeService = Object.freeze({
  getDashboard: () => httpClient.get(API_ENDPOINTS.EMPLOYEE.DASHBOARD).then((response) => response.data),
  getAssignedProjects: (params) => httpClient.get(API_ENDPOINTS.EMPLOYEE.PROJECTS, { params }).then((response) => response.data),
  getProjectDetails: (projectId) => httpClient.get(API_ENDPOINTS.EMPLOYEE.PROJECT_BY_ID(projectId)).then((response) => response.data),
  getProjectProgress: (projectId) => httpClient.get(API_ENDPOINTS.EMPLOYEE.PROJECT_PROGRESS(projectId)).then((response) => response.data),
  getProjectMembers: (projectId) => httpClient.get(API_ENDPOINTS.EMPLOYEE.PROJECT_MEMBERS(projectId)).then((response) => response.data),
  getAssignedTasks: (params) => httpClient.get(API_ENDPOINTS.EMPLOYEE.TASKS, { params }).then((response) => response.data),
  getTaskById: (taskId) => httpClient.get(API_ENDPOINTS.EMPLOYEE.TASK_BY_ID(taskId)).then((response) => response.data),
  getPendingTasks: () => httpClient.get(API_ENDPOINTS.EMPLOYEE.PENDING_TASKS).then((response) => response.data),
  getCompletedTasks: () => httpClient.get(API_ENDPOINTS.EMPLOYEE.COMPLETED_TASKS).then((response) => response.data),
  getOverdueTasks: () => httpClient.get(API_ENDPOINTS.EMPLOYEE.OVERDUE_TASKS).then((response) => response.data),
  updateTaskStatus: (taskId, data) => httpClient.put(API_ENDPOINTS.EMPLOYEE.UPDATE_TASK_STATUS(taskId), data).then((response) => response.data),
  getTaskComments: (taskId) => httpClient.get(API_ENDPOINTS.EMPLOYEE.TASK_COMMENTS(taskId)).then((response) => response.data),
  addTaskComment: (taskId, data) => httpClient.post(API_ENDPOINTS.EMPLOYEE.TASK_COMMENTS(taskId), data).then((response) => response.data),
  updateTaskComment: (commentId, data) => httpClient.put(API_ENDPOINTS.EMPLOYEE.COMMENT_BY_ID(commentId), data).then((response) => response.data),
  deleteTaskComment: (commentId) => httpClient.delete(API_ENDPOINTS.EMPLOYEE.COMMENT_BY_ID(commentId)).then((response) => response.data),
  getProfile: () => httpClient.get(API_ENDPOINTS.EMPLOYEE.PROFILE).then((response) => response.data),
  updateProfile: (data) => httpClient.put(API_ENDPOINTS.EMPLOYEE.PROFILE, data).then((response) => response.data),
  changePassword: (data) => httpClient.put(API_ENDPOINTS.EMPLOYEE.CHANGE_PASSWORD, data).then((response) => response.data),
})

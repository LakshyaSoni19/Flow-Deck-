import { API_ENDPOINTS } from '../api/apiEndpoints'
import { httpClient } from '../api/httpClient'
import { normalizeTaskResponse } from '../utils/taskNormalization'

const taskResponse = (response) => normalizeTaskResponse(response.data)

export const pmService = Object.freeze({
  getAssignedProjects: (params) => httpClient.get(API_ENDPOINTS.PROJECT_MANAGER.PROJECTS, { params }).then((response) => response.data),
  getProjectDetails: (projectId) => httpClient.get(API_ENDPOINTS.PROJECT_MANAGER.PROJECT_BY_ID(projectId)).then((response) => response.data),
  getProjectOverview: (projectId) => httpClient.get(API_ENDPOINTS.PROJECT_MANAGER.PROJECT_OVERVIEW(projectId)).then((response) => response.data),
  getProjectProgress: (projectId) => httpClient.get(API_ENDPOINTS.PROJECT_MANAGER.PROJECT_PROGRESS(projectId)).then((response) => response.data),
  getProjectStats: (projectId) => httpClient.get(API_ENDPOINTS.PROJECT_MANAGER.PROJECT_STATS(projectId)).then((response) => response.data),
  getProjectMembers: (projectId) => httpClient.get(API_ENDPOINTS.PROJECT_MANAGER.PROJECT_MEMBERS(projectId)).then((response) => response.data),
  addProjectMember: (projectId, payload) => httpClient.post(API_ENDPOINTS.PROJECT_MANAGER.PROJECT_MEMBERS(projectId), payload).then((response) => response.data),
  removeProjectMember: (projectId, userId) => httpClient.delete(API_ENDPOINTS.PROJECT_MANAGER.PROJECT_MEMBER_BY_USER_ID(projectId, userId)).then((response) => response.data),
  getProjectTasks: (projectId, params) => httpClient.get(API_ENDPOINTS.PROJECT_MANAGER.PROJECT_TASKS(projectId), { params }).then(taskResponse),
  getTaskById: (taskId) => httpClient.get(API_ENDPOINTS.PROJECT_MANAGER.TASK_BY_ID(taskId)).then(taskResponse),
  createTask: (projectId, payload) => httpClient.post(API_ENDPOINTS.PROJECT_MANAGER.PROJECT_TASKS(projectId), payload).then(taskResponse),
  updateTask: (taskId, payload) => httpClient.put(API_ENDPOINTS.PROJECT_MANAGER.TASK_BY_ID(taskId), payload).then(taskResponse),
  deleteTask: (taskId) => httpClient.delete(API_ENDPOINTS.PROJECT_MANAGER.TASK_BY_ID(taskId)).then((response) => response.data),
  assignTask: (taskId, payload) => httpClient.put(API_ENDPOINTS.PROJECT_MANAGER.ASSIGN_TASK(taskId), payload).then(taskResponse),
  changeTaskPriority: (taskId, payload) => httpClient.put(API_ENDPOINTS.PROJECT_MANAGER.TASK_PRIORITY(taskId), payload).then(taskResponse),
  changeTaskStatus: (taskId, payload) => httpClient.put(API_ENDPOINTS.PROJECT_MANAGER.TASK_STATUS(taskId), payload).then(taskResponse),
  setTaskDueDate: (taskId, payload) => httpClient.put(API_ENDPOINTS.PROJECT_MANAGER.TASK_DUE_DATE(taskId), payload).then(taskResponse),
  getEmployeeSummary: (projectId) => httpClient.get(API_ENDPOINTS.PROJECT_MANAGER.EMPLOYEE_SUMMARY(projectId)).then((response) => response.data),
  getPendingTasks: (projectId) => httpClient.get(API_ENDPOINTS.PROJECT_MANAGER.PROJECT_TASKS_PENDING(projectId)).then(taskResponse),
  getCompletedTasks: (projectId) => httpClient.get(API_ENDPOINTS.PROJECT_MANAGER.PROJECT_TASKS_COMPLETED(projectId)).then(taskResponse),
  getInProgressTasks: (projectId) => httpClient.get(API_ENDPOINTS.PROJECT_MANAGER.PROJECT_TASKS_IN_PROGRESS(projectId)).then(taskResponse),
  getOverdueTasks: (projectId) => httpClient.get(API_ENDPOINTS.PROJECT_MANAGER.PROJECT_TASKS_OVERDUE(projectId)).then(taskResponse),
  getHighPriorityTasks: (projectId) => httpClient.get(API_ENDPOINTS.PROJECT_MANAGER.PROJECT_TASKS_HIGH_PRIORITY(projectId)).then(taskResponse),
  getTaskCompletionPercentage: (projectId) => httpClient.get(API_ENDPOINTS.PROJECT_MANAGER.PROJECT_TASK_COMPLETION_PERCENTAGE(projectId)).then((response) => response.data),
})

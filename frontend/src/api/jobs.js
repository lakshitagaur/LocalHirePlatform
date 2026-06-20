import apiClient from './client'

export const getJobs = (filters = {}) => apiClient.get('/jobs', { params: filters })
export const getJobById = (id) => apiClient.get(`/jobs/${id}`)
export const createJob = (data) => apiClient.post('/jobs', data)
export const getMyJobs = () => apiClient.get('/jobs/my-jobs')
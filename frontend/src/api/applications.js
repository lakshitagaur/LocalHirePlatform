import apiClient from './client'

export const applyToJob = (data) => apiClient.post('/applications', data)
export const getMyApplications = () => apiClient.get('/applications/my-applications')
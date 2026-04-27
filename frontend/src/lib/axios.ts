import axios from 'axios'
import { getDeviceId } from './device'

const isServer = typeof window === 'undefined'
const api = axios.create({
  baseURL: isServer ? (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1') : '/api/v1',
  withCredentials: true, // For cookies (Refresh Token)
})

api.interceptors.request.use(async (config) => {
  const deviceId = await getDeviceId()
  config.headers['X-Device-Id'] = deviceId.trim()
  
  const token = localStorage.getItem('access_token')
  if (token && token !== 'undefined' && token !== 'null') {
    config.headers.Authorization = `Bearer ${token.trim()}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config
    if (error.response?.status === 401 && !originalRequest._retry && typeof window !== 'undefined') {
      originalRequest._retry = true
      try {
        const response = await api.post('/auth/refresh')
        const { accessToken } = response.data.data
        if (accessToken && accessToken !== 'undefined') {
          const trimmedToken = accessToken.trim()
          localStorage.setItem('access_token', trimmedToken)
          originalRequest.headers.Authorization = `Bearer ${trimmedToken}`
          return api(originalRequest)
        }
        throw new Error('No valid access token received from refresh')
      } catch (refreshError) {
        console.error('[Axios] Refresh token failed, removing access token')
        localStorage.removeItem('access_token')
        return Promise.reject(refreshError)
      }
    }
    return Promise.reject(error)
  }
)

export default api

import { Service, Incident, ApiResponse } from '@/types';
import { apiClient } from './api-client';

export const servicesApi = {
    getAll: () => apiClient.get<Service[]>('/services'),
    getById: (id: string) => apiClient.get<Service>(`/services/${id}`),
};

export const incidentsApi = {
    getRecent: () => apiClient.get<any>('/incidents'), // Using any as the backend returns Page object
    getById: (id: string) => apiClient.get<Incident>(`/incidents/${id}`),
    getOpen: () => apiClient.get<Incident[]>('/incidents/open'),
};

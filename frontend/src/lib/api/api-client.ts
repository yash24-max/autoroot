import { ApiResponse } from '@/types';

const BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

async function request<T>(path: string, options: RequestInit = {}): Promise<ApiResponse<T>> {
    const url = `${BASE_URL}${path}`;
    const headers = {
        'Content-Type': 'application/json',
        'X-Tenant-Id': process.env.NEXT_PUBLIC_DEFAULT_TENANT_ID || '00000000-0000-0000-0000-000000000001',
        ...options.headers,
    };

    const response = await fetch(url, { ...options, headers });

    if (!response.ok) {
        const errorBody = await response.json().catch(() => ({}));
        throw new Error(errorBody.message || `API error: ${response.status}`);
    }

    return response.json();
}

export const apiClient = {
    get: <T>(path: string, options?: RequestInit) => request<T>(path, { ...options, method: 'GET' }),
    post: <T>(path: string, body: any, options?: RequestInit) => request<T>(path, { ...options, method: 'POST', body: JSON.stringify(body) }),
    put: <T>(path: string, body: any, options?: RequestInit) => request<T>(path, { ...options, method: 'PUT', body: JSON.stringify(body) }),
    delete: <T>(path: string, options?: RequestInit) => request<T>(path, { ...options, method: 'DELETE' }),
};

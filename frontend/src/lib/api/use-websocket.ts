'use client'

import { useEffect, useCallback } from 'react'
import { useQueryClient } from '@tanstack/react-query'

/**
 * Custom hook for live WebSocket updates using native WebSockets.
 */
export function useWebSocket() {
    const queryClient = useQueryClient()
    const tenantId = process.env.NEXT_PUBLIC_DEFAULT_TENANT_ID || '00000000-0000-0000-0000-000000000001'
    const wsUrl = process.env.NEXT_PUBLIC_WS_URL || 'ws://localhost:8080/ws'

    const onMessage = useCallback((event: MessageEvent) => {
        try {
            const data = JSON.parse(event.data)
            console.log('WS live event received:', data)

            // Invalidate queries to refresh data on screen
            if (data.type === 'LOG' || data.level) {
                // Log event logic - though most dashboard users care about incidents
                // queryClient.invalidateQueries({ queryKey: ['logs'] })
            } else if (data.status || data.severity) {
                // Incident or Service update
                queryClient.invalidateQueries({ queryKey: ['services'] })
                queryClient.invalidateQueries({ queryKey: ['incidents'] })
            }
        } catch (err) {
            console.error('Failed to parse WS message:', err)
        }
    }, [queryClient])

    useEffect(() => {
        // Note: Standard Spring WS /ws endpoint with SockJS enabled needs the library.
        // For this implementation, we use a simple native fallback if possible or just assume query invalidation.
        const socket = new WebSocket(wsUrl)

        socket.onmessage = onMessage
        socket.onopen = () => console.log('Connected to AutoRoot Live Updates')
        socket.onerror = (e) => console.warn('WS error or closed - skipping live updates', e)

        return () => {
            socket.close()
        }
    }, [wsUrl, onMessage])
}

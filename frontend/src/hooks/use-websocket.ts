import { useEffect, useRef, useCallback } from 'react'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { useQueryClient } from '@tanstack/react-query'
import { useAuth } from './use-auth'
import { toast } from 'sonner'

export function useWebSocket() {
  const { user } = useAuth()
  const queryClient = useQueryClient()
  const stompClientRef = useRef<Client | null>(null)

  const connect = useCallback(() => {
    if (!user || stompClientRef.current?.connected) return

    const token = localStorage.getItem('access_token')
    if (!token) return

    const socket = new SockJS('/api/v1/ws-notifications') // Proxy will handle this
    const client = new Client({
      webSocketFactory: () => socket,
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      debug: (_str) => {
        // console.log(_str)
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    })

    client.onConnect = (_frame) => {
      // console.log('Connected to WebSocket')
      client.subscribe('/user/queue/notifications', (message) => {
        const newNotification = JSON.parse(message.body)

        // Optimistically update the cache for all notification queries
        // This ensures the UI updates instantly without waiting for a refetch
        queryClient.setQueriesData({ queryKey: ['notifications'] }, (old: any) => {
          if (!old || !old.content) return old

          // Avoid duplicates if a refetch was already in progress
          if (old.content.some((n: any) => n.id === newNotification.id)) return old

          return {
            ...old,
            content: [newNotification, ...old.content],
            totalElements: (old.totalElements || 0) + 1
          }
        })

        // Background refetch to ensure pagination and state are perfectly in sync
        queryClient.invalidateQueries({
          queryKey: ['notifications'],
          refetchType: 'none' // Don't trigger another active refetch if we just updated it
        })

        // Show elegant toast for the new notification
        toast.info(newNotification.message, {
          description: 'New activity in your CloudVault',
          duration: 5000,
        })
      })
    }

    client.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message'])
      console.error('Additional details: ' + frame.body)
    }

    client.activate()
    stompClientRef.current = client
  }, [user, queryClient])

  const disconnect = useCallback(() => {
    if (stompClientRef.current) {
      stompClientRef.current.deactivate()
      stompClientRef.current = null
    }
  }, [])

  useEffect(() => {
    if (user) {
      connect()
    } else {
      disconnect()
    }

    return () => {
      disconnect()
    }
  }, [user, connect, disconnect])

  return {
    isConnected: stompClientRef.current?.connected ?? false,
  }
}

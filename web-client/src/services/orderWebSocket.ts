import {Client, type IMessage} from "@stomp/stompjs";
import {notificationService} from "@/services/notificationService.ts";

type OrderReadyNotification = {
    orderId: number
    status: string
    message: string
}

let client: Client | null = null

export const orderWebSocket = {
    connect(): void {
        const token = sessionStorage.getItem('accessToken')
        if (!token) return
        if (client?.activate()) return
        client = new Client({
            brokerURL: 'ws://localhost:8080/ws',
            connectHeaders: {
                Authorization: `Bearer ${token}`
            },
            reconnectDelay: 5000,
            heartbeatIncoming: 10000,
            heartbeatOutgoing: 10000,

            onConnect: () => {
                console.log('Order WebSocket connected')

                client?.subscribe('/user/queue/orders', (frame: IMessage) => {
                    try {
                        const notification = JSON.parse(frame.body) as OrderReadyNotification
                        if (notification.status !== 'READY') return

                        notificationService.success({
                            title: 'Заказ готов!',
                            message: notification.message, duration: 8000
                        })
                    } catch (err) {
                        console.error('Failed to parse order notification: ', err)
                    }
                })
            },

            onStompError: (frame) => {
                console.error(
                    'STOMP error: ',
                    frame.headers['message'],
                    frame.body
                )
            },

            onWebSocketError: (event) => {
                console.error('WebSocket error: ', event)
            },

            onDisconnect: () => {
                console.log('Order WebSocket disconnected')
            }
        })

        client.activate()
    },

    disconnect(): void {
        if (!client) return

        const currentClient = client
        client = null

        void currentClient.deactivate()
    }
}
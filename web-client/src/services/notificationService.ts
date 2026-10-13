import {ElNotification} from "element-plus";

export type NotificationOptions = {
    title: string,
    message: string,
    duration?: number
}

const DEFAULT_DURATION = 4000

function notify(
    type: 'info' | 'warning' | 'error' | 'success',
    options: NotificationOptions
): void {
    ElNotification({
        type,
        title: options.title,
        message: options.message,
        duration: options.duration ?? DEFAULT_DURATION,
        position: 'top-right',
        showClose: true,
        customClass: 'coffee-notification'
    })
}

export const notificationService = {
    info: (options: NotificationOptions): void => notify('info', options),

    warning: (options: NotificationOptions): void => notify('warning', options),

    error: (options: NotificationOptions): void => notify('error', options),

    success: (options: NotificationOptions): void => notify('success', options),
}
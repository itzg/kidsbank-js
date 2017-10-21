export const CREATE_NOTIFICATION = 'CREATE_NOTIFICATION';
export const REMOVE_PENDING_NOTIFICATIONS = 'FLUSH_PENDING_NOTIFICATIONS';

let nextId = 0;

export function createNotification(message, level = 'success', title = null) {
  return {
    type: CREATE_NOTIFICATION,
    notification: {
      uid: `managed-${nextId++}`,
      message,
      level,
      title
    }
  };
}

export function removePendingNotifications(notifications) {
  return {
    type: REMOVE_PENDING_NOTIFICATIONS,
    notifications
  }
}
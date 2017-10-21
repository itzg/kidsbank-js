import {CREATE_NOTIFICATION, REMOVE_PENDING_NOTIFICATIONS} from "../actions/notifications";

export default function session(state = {
                                  pending: [],
                                },
                                action) {
  switch (action.type) {
    case CREATE_NOTIFICATION:
      return {
        pending: [...state.pending, action.notification]
      };

    case REMOVE_PENDING_NOTIFICATIONS:
      let uidsToRemove = action.notifications.map(n => n.uid);
      return {
        pending: state.pending.filter(n => uidsToRemove.indexOf(n.uid) === -1)
      };

    default:
      return state;
  }
}

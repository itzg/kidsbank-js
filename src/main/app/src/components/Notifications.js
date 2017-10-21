import React, {Component} from 'react';
import {connect} from 'react-redux';
import {logoutUser} from '../actions/user';
import NotificationSystem from 'react-notification-system';
import {removePendingNotifications} from "../actions/notifications";

class Notifications extends Component {
  _notificationSystem = null;

  render() {
    return <NotificationSystem ref={(val) => this._notificationSystem = val}/>
  }


  componentWillReceiveProps(nextProps) {
    if (nextProps.session.timeout && !this.props.session.timeout) {
      this._notificationSystem.addNotification({
        title: 'Session timeout',
        message: 'The current login session has timed out. Please log out and try again.',
        autoDismiss: 0,
        position: 'tc',
        level: 'error',
        action: {
          label: 'Logout',
          callback: this.props.onLogout
        }
      });
    }

    if (nextProps.notifications.pending.length > 0) {
      for (let notification of nextProps.notifications.pending) {
        this._notificationSystem.addNotification(notification);
      }

      this.props.removeNotifications(nextProps.notifications.pending);
    }
  }

}

function mapStateToProps(state) {
  const {session, notifications} = state;

  return {
    session,
    notifications
  }
}

function mapDispatchToProps(dispatch) {
  return {
    onLogout() {
      dispatch(logoutUser());
    },

    removeNotifications(notifications) {
      dispatch(removePendingNotifications(notifications));
    }
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Notifications);
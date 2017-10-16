import React, {Component} from 'react';
import {connect} from 'react-redux';
import {logoutUser} from '../actions/user';
import NotificationSystem from 'react-notification-system';

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
  }

}

function mapStateToProps(state) {
  const {session} = state;

  return {
    session
  }
}

function mapDispatchToProps(dispatch) {
  return {
    onLogout: () => {
      dispatch(logoutUser());
    }
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Notifications);
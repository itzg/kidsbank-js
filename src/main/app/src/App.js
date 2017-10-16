import React, {Component} from 'react';
import {Route, Switch, withRouter} from 'react-router-dom';
import {connect} from 'react-redux';
import NotificationSystem from 'react-notification-system';
import './App.css';
import Welcome from './scenes/Welcome';
import Parents from './scenes/Parents';
import Kids from './scenes/Kids';
import {logoutUser} from "./actions/user";

class App extends Component {
  _notificationSystem = null;

  render() {

    return (
      <div className="App">
        <NotificationSystem ref={(val) => this._notificationSystem = val}/>
        <Switch>
          <Route path="/parent" component={Parents}/>
          <Route path="/kid" component={Kids}/>
          <Route component={Welcome}/>
        </Switch>
      </div>
    );
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

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(App));

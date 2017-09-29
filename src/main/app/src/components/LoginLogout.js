import React, {Component} from 'react';
import {Button, Icon} from 'semantic-ui-react';

class LoginLogout extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    if (this.props.loading) {
      return null;
    }

    let currentUser = this.props.currentUser;
    if (currentUser) {

      return (
        <div>
          <div>Logged in as {currentUser.connection ? currentUser.connection.displayName : currentUser.name}</div>
          <div>
            <Button content="Logout" onClick={this.handleLogout}>Logout</Button>
          </div>
        </div>
      );
    } else {
      return (
        <div>
          <Button color='facebook' onClick={this.handleLogin}>
            <Icon name='facebook'/> Log in with Facebook
          </Button>
        </div>
      )
    }
  }

  handleLogin = () => {
    window.location = '/signin/facebook';
  };

  handleLogout = () => {
    fetch('/signout', {method: 'POST', credentials: 'same-origin'}).then((resp) => {
      if (resp.ok) {
        window.location = '/';
      }
    });
  }
}

export default LoginLogout;
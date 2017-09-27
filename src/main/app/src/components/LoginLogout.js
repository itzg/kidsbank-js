import React, {Component} from 'react';

class LoginLogout extends Component {
  constructor(props) {
    super(props);

    this.handleLogout = this.handleLogout.bind(this);
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
            <button onClick={this.handleLogout}>Logout</button>
          </div>
        </div>
      );
    } else {
      return (
        <div>
          <a href="/signin/facebook">Log in with Facebook</a>
        </div>
      )
    }
  }

  handleLogout() {
    fetch('/signout', {method: 'POST', credentials: 'same-origin'}).then((resp) => {
      if (resp.ok) {
        window.location.reload();
      }
    });
  }
}

export default LoginLogout;
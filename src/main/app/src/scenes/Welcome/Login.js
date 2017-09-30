import React, {Component} from 'react';
import {Button, Icon} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {loginUser} from "../../actions/user";

class Login extends Component {
  render() {
    return (
      <div>
        <Button color='facebook' onClick={this.handleLogin}>
          <Icon name='facebook'/> Log in with Facebook
        </Button>
      </div>
    )
  }

  handleLogin = () => {
    this.props.dispatch(loginUser());
  };

}

export default connect()(Login);
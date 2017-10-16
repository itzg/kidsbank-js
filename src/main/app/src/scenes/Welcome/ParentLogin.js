import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {Button, Header, Icon} from 'semantic-ui-react';

class ParentLogin extends Component {

  render() {
    return (
      <div className='ParentLogin'>
        <Header>Login with your social account
          <Header.Subheader>and avoid keeping track of yet another password</Header.Subheader>
        </Header>
        <Button color='facebook' onClick={this.props.onLogin}>
          <Icon name='facebook'/> Log in with Facebook
        </Button>
      </div>
    );
  }

  static propTypes = {
    onLogin: PropTypes.func
  }
}

export default ParentLogin;
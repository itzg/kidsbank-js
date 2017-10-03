import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {Button, Icon} from 'semantic-ui-react';

class ParentLogin extends Component {

  render() {
    return (
      <Button color='facebook' onClick={this.props.onLogin}>
        <Icon name='facebook'/> Log in with Facebook
      </Button>
    );
  }

  static propTypes = {
    onLogin: PropTypes.func
  }
}

export default ParentLogin;
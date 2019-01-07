import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {Header} from 'semantic-ui-react';
import {
  FacebookLoginAction,
  GithubLoginAction,
  LinkedInLoginAction
} from './ProviderLoginActions';
import './ParentLogin.css';

class ParentLogin extends Component {

  render() {
    return (
      <div className='ParentLogin'>
        <Header>Login with your social account
          <Header.Subheader>and avoid keeping track of yet another password</Header.Subheader>
        </Header>

        <div className='loginActions'>
          <FacebookLoginAction onLogin={this.props.onLogin} lastUsedProvider={this.props.lastUsedProvider}/>
          <GithubLoginAction onLogin={this.props.onLogin} lastUsedProvider={this.props.lastUsedProvider}/>
          <LinkedInLoginAction onLogin={this.props.onLogin} lastUsedProvider={this.props.lastUsedProvider}/>
        </div>

      </div>
    );
  }

  static propTypes = {
    onLogin: PropTypes.func,
    lastUsedProvider: PropTypes.string
  }
}

export default ParentLogin;
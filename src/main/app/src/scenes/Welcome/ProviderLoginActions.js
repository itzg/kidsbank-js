import React from 'react';
import {Button, Icon} from 'semantic-ui-react';
import PropTypes from "prop-types";

class ProviderLoginAction extends React.Component {
  _provider;
  _colorName;
  _iconName;
  _label;

  constructor(props, provider, colorName, iconName, label) {
    super(props);
    this._provider = provider;
    this._colorName = colorName;
    this._iconName = iconName;
    this._label = label;
  }

  render() {
    const lastUsedProvider = this.props.lastUsedProvider;
    let basic = lastUsedProvider && this._provider !== lastUsedProvider;

    return (
      <Button color={this._colorName} basic={basic} onClick={() => this.props.onLogin(this._provider)} size='big'>
        <Icon name={this._iconName}/> Log in with {this._label}
      </Button>
    )
  }

  static propTypes = {
    onLogin: PropTypes.func,
    lastUsedProvider: PropTypes.string
  }
}

export class FacebookLoginAction extends ProviderLoginAction {
  constructor(props) {
    super(props, 'facebook', 'facebook', 'facebook', 'Facebook');
  }
}

export class TwitterLoginAction extends ProviderLoginAction {
  constructor(props) {
    super(props, 'twitter', 'twitter', 'twitter', 'Twitter');
  }
}

export class LinkedInLoginAction extends ProviderLoginAction {
  constructor(props) {
    super(props, 'linkedin', 'linkedin', 'linkedin', 'LinkedIn');
  }
}

export class GithubLoginAction extends ProviderLoginAction {
  constructor(props) {
    super(props, 'github', 'grey', 'github', 'GitHub');
  }
}
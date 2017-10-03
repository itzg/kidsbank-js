import React, {Component} from 'react';
import PropTypes from 'prop-types';

class KidLogin extends Component {
  constructor(props) {
    super(props);

    this.state = {
      needsToRegister: false
    }
  }

  render() {
    return <div>Hi</div>
  }

  static propTypes = {
    onLogin: PropTypes.func.required,
    onRegister: PropTypes.func.required
  }
}

export default KidLogin;
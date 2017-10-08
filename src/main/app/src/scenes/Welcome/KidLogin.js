import React, {Component} from 'react';
import {connect} from 'react-redux';

import {loginKid, registerKid} from '../../actions/user';
import KidRegisterForm from "./KidRegisterForm";
import KidLoginForm from "./KidLoginForm";
import './KidLogin.css'

class KidLogin extends Component {
  constructor(props) {
    super(props);

    this.state = {
      needsToRegister: false
    }
  }

  render() {
    return this.state.needsToRegister ? (
      <KidRegisterForm onSubmit={this.props.onRegister}
                       onSwitchToLogin={this.handleSwitchToLogin}
                       className='KidLogin'
      />
    ) : (
      <KidLoginForm onSubmit={this.props.onLogin}
                    onSwitchToRegister={this.handleSwitchToRegister}
                    className='KidLogin'
      />
    );
  }

  handleSwitchToRegister = () => {
    this.setState({needsToRegister: true});
  };

  handleSwitchToLogin = () => {
    this.setState({needsToRegister: false});
  };
}

const mapStateToProps = (state) => {
}
const mapDispatchToProps = (dispatch) => {
  return {
    onLogin: (values) => {
      const {
        username,
        password
      } = values;
      return dispatch(loginKid(username, password));
    },

    onRegister: (values) => {
      const {
        username,
        password,
        kidlinkCode
      } = values;
      return dispatch(registerKid(username, password, kidlinkCode));
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(KidLogin);
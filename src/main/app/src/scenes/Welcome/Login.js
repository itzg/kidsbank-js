import React, {Component} from 'react';
import {Button, Divider, Segment} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {loginKid, loginParent, registerKid} from "../../actions/user";
import ParentLogin from './ParentLogin'
import KidLogin from "./KidLogin";
import './Login.css';

export const LOGIN_TYPE_PARENT = 'LOGIN_TYPE_PARENT';
export const LOGIN_TYPE_KID = 'LOGIN_TYPE_KID';

class Login extends Component {
  constructor(props) {
    super(props);
    console.log('login props', props);

    this.state = {
      loginType: undefined
    }
  }

  render() {

    let inner;
    switch (this.state.loginType) {

      case LOGIN_TYPE_PARENT:
        inner = <ParentLogin onLogin={this.props.handleParentLogin}/>;
        break;

      case LOGIN_TYPE_KID:
        inner = <KidLogin onLogin={this.props.handleKidLogin} onRegister={this.props.handleKidRegister}/>;
        break;

      default:
        inner = <Segment padded>

          <Button size='huge' fluid onClick={this.chooseParentLogin}>I'm a parent</Button>
          <Divider horizontal>or</Divider>
          <Button size='huge' fluid onClick={this.chooseKidLogin}>I'm a kid</Button>

        </Segment>;
    }

    return (
      <div className='TypeChooser Container'>
        <div className='TypeChooser Item'>
          {inner}
        </div>
      </div>
    )

  };

  chooseParentLogin = () => {
    this.setState({loginType: LOGIN_TYPE_PARENT});
  };

  chooseKidLogin = () => {
    this.setState({loginType: LOGIN_TYPE_KID})
  }
}

const mapStateToProps = state => {
  return {};
};

const mapDispatchToProps = dispatch => {
  return {
    handleParentLogin: () => {
      dispatch(loginParent());
    },

    handleKidLogin: (username, password) => {
      dispatch(loginKid(username, password));
    },

    handleKidRegister: (username, password, kidlinkCode) => {
      dispatch(registerKid(username, password, kidlinkCode));
    }
  }
};

export default connect(mapStateToProps, mapDispatchToProps)(Login);
import React, {Component} from 'react';
import {Button, Divider, Segment} from 'semantic-ui-react';
import {connect} from 'react-redux';
import {loginParent} from "../../actions/user";
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
        inner = <KidLogin/>;
        break;

      default:
        inner =
          <div>

            <Button size='huge' fluid onClick={this.chooseParentLogin}>I'm a parent</Button>
            <Divider horizontal>or</Divider>
            <Button size='huge' fluid onClick={this.chooseKidLogin}>I'm a kid</Button>
          </div>
        ;
    }

    return (
      <div className='TypeChooser Container'>
        <div className='TypeChooser Item'>
          <Segment padded attached>
            {inner}
          </Segment>
          {this.state.loginType &&
          <Button size='mini' attached='bottom' onClick={this.unchooseType}>Go back</Button>}
        </div>
      </div>
    )

  }

  chooseParentLogin = () => {
    this.setState({loginType: LOGIN_TYPE_PARENT});
  };

  chooseKidLogin = () => {
    this.setState({loginType: LOGIN_TYPE_KID});
  };

  unchooseType = () => {
    this.setState({loginType: null});
  }
}

const mapStateToProps = state => {
  return {};
};

const mapDispatchToProps = dispatch => {
  return {
    handleParentLogin() {
      dispatch(loginParent());
    }

  }
};

export default connect(mapStateToProps, mapDispatchToProps)(Login);

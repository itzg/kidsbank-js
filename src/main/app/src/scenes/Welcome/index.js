import React, {Component} from 'react';
import {Loader} from 'semantic-ui-react';
import './welcome.css';
import {connect} from 'react-redux';
import {Redirect} from 'react-router-dom'
import logo from './kidsbank-main.png';
import Login from './Login';
import {fetchUserProfile} from '../../actions/user';

class Welcome extends Component {
  render() {

    let loginBit;
    if (this.props.isFetchingUser) {
      loginBit = <Loader/>;
    }
    else if (this.props.loggedIn) {
      return <Redirect to={'/' + this.props.role}/>
    }
    else {
      loginBit = <Login/>
    }

    return (
      <div>
        <div className="Welcome-banner">
          <h1>Welcome to</h1>
          <img src={logo} alt="kidsbank"/>
        </div>
        {loginBit}
      </div>
    );
  }

  componentDidMount() {
    this.props.dispatch(fetchUserProfile());
  }
}

function mapStateToProps(state) {

  const {
    isFetching,
    loggedIn,
    role
  } = state.user;

  return {
    isFetchingUser: isFetching,
    role,
    loggedIn
  }
}


export default connect(mapStateToProps)(Welcome);
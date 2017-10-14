import React, {Component} from 'react';
import {Loader} from 'semantic-ui-react';
import './welcome.css';
import {connect} from 'react-redux';
import {Redirect} from 'react-router-dom'
import Login from './Login';
import {fetchUserProfile} from '../../actions/user';
import KidsbankLogo from '../../components/KidsbankLogo';

class Welcome extends Component {
  render() {

    let loginBit;
    if (this.props.isFetchingUser) {
      loginBit = <Loader active inline/>;
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
          <KidsbankLogo autosize={true} centered={true}/>
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
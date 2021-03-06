import React, {Component} from 'react';
import {Loader} from 'semantic-ui-react';
import './welcome.css';
import {connect} from 'react-redux';
import {Redirect} from 'react-router-dom'
import Login from './Login';
import {fetchUserProfile} from '../../actions/user';
import KidsbankLogo from '../../components/KidsbankLogo';
import Introduction from './Introduction';

class Welcome extends Component {
  render() {

    let loginBit;
    if (!this.props.introDismissed) {
      loginBit = <Introduction/>;
    }
    else if (this.props.isFetchingUser) {
      loginBit = <Loader active/>;
    }
    else if (this.props.loggedIn) {
      return <Redirect push to={'/' + this.props.role}/>
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
    this.props.fetchUserProfile();
  }
}

function mapStateToProps(state) {

  const {
    isFetching,
    loggedIn,
    role
  } = state.user;

  const {
    introDismissed
  } = state.persisted;

  return {
    isFetchingUser: isFetching,
    role,
    loggedIn,
    introDismissed
  }
}

function mapDispatchToProps(dispatch) {
  return {
    fetchUserProfile() {
      dispatch(fetchUserProfile());
    }
  }
}


export default connect(mapStateToProps, mapDispatchToProps)(Welcome);
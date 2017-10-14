import React, {Component} from 'react';
import {connect} from 'react-redux';
import {fetchUserProfile, logoutUser} from '../../actions/user';
import {Button, Loader} from 'semantic-ui-react';
import {Link, Redirect} from 'react-router-dom';
import KidsbankLogo from '../../components/KidsbankLogo'
import './index.css';

class Kids extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    if (this.props.isFetchingUser) {
      return <Loader active inline/>
    }
    else if (!this.props.loggedIn) {
      return <Redirect to="/"/>
    }
    else if (this.props.role !== 'kid') {
      console.warn('Observed a non-kid role', this.props.role);
      return <Redirect to="/"/>
    }
    else {
      const profileHeader = (
        <div>
          {this.props.profile.displayName}
        </div>
      );

      return (
        <div className="Kids">
          <div className="header">
            <div><Link to='/kid'><KidsbankLogo size='medium'/></Link></div>

            <div className="info">
              <span>You are logged in as {this.props.profile.displayName}</span>
              <Button onClick={() => this.props.onLogout()}>Logout</Button>
            </div>
          </div>
        </div>
      );

    }
  }

  componentDidMount() {
    this.props.fetchUserProfile();
  }
}

const
  mapStateToProps = (state) => {

    const {user} = state;

    return {
      isFetchingUser: user.isFetching,
      profile: user.profile,
      loggedIn: user.loggedIn,
      role: user.role
    }
  };

const
  mapDispatchToProps = (dispatch) => {
    return {
      onLogout: () => {
        dispatch(logoutUser());
      },
      fetchUserProfile: () => {
        dispatch(fetchUserProfile());
      }
    }
  };

export default connect(mapStateToProps, mapDispatchToProps)

(
  Kids
)
;
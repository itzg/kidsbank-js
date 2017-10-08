import React, {Component} from 'react';
import {connect} from 'react-redux';
import {fetchUserProfile, logoutUser} from '../../actions/user';
import {Dropdown, Loader, Menu} from 'semantic-ui-react';
import {Link, Redirect, Switch} from 'react-router-dom';
import KidsbankLogo from '../../components/KidsbankLogo'

class Kids extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    if (this.props.isFetchingUser) {
      return <Loader/>
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
          <Menu attached='top' pointing>
            <Menu.Item as={Link} to="/parent" header><KidsbankLogo small={true}/></Menu.Item>

            <Menu.Menu position='right'>
              <Dropdown item trigger={profileHeader}>
                <Dropdown.Menu>
                  <Dropdown.Item text="Logout" onClick={() => this.props.onLogout()}/>
                </Dropdown.Menu>
              </Dropdown>
            </Menu.Menu>
          </Menu>

          <Switch>
          </Switch>
        </div>
      )
    }

  }

  componentDidMount() {
    this.props.fetchUserProfile();
  }
}

const mapStateToProps = (state) => {

  const {user} = state;

  return {
    isFetchingUser: user.isFetching,
    profile: user.profile,
    loggedIn: user.loggedIn,
    role: user.role
  }
};

const mapDispatchToProps = (dispatch) => {
  return {
    onLogout: () => {
      dispatch(logoutUser());
    },
    fetchUserProfile: () => {
      dispatch(fetchUserProfile());
    }
  }
};

export default connect(mapStateToProps, mapDispatchToProps)(Kids);
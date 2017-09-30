import React, {Component} from 'react';
import {connect} from 'react-redux';
import {fetchUserProfile, logoutUser} from '../../actions/user';
import {Dropdown, Image, Loader, Menu} from 'semantic-ui-react';
import {Link, Redirect} from 'react-router-dom';
import KidsbankLogo from '../../components/KidsbankLogo'

class Parents extends Component {
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
    else if (this.props.role !== 'parent') {
      console.warn('Observed a non-parent role', this.props.role);
      return <Redirect to="/"/>
    }
    else {
      const profileHeader = (
        <div>
          <Image src={this.props.profile.profileImageUrl} avatar/>
          {this.props.profile.displayName}
        </div>
      );

      return (
        <div>
          <Menu attached='top'>
            <Menu.Item as={Link} to="/parent" header><KidsbankLogo small={true}/></Menu.Item>
            <Menu.Menu position='right'>
              <Dropdown item trigger={profileHeader}>
                <Dropdown.Menu>
                  <Dropdown.Item text="Logout" onClick={() => this.props.onLogout()}/>
                </Dropdown.Menu>
              </Dropdown>
            </Menu.Menu>
          </Menu>
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

export default connect(mapStateToProps, mapDispatchToProps)(Parents);
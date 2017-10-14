import React, {Component} from 'react';
import {connect} from 'react-redux';
import {fetchUserProfile, logoutUser} from '../../actions/user';
import {Dropdown, Image, Loader, Menu} from 'semantic-ui-react';
import {Link, Redirect, Route, Switch} from 'react-router-dom';
import KidsbankLogo from '../../components/KidsbankLogo'
import Accounts from './Accounts';
import AccountDetail from './AccountDetail';
import AccountSpecificTab from './AccountSpecificTab';

class Parents extends Component {
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

      const accountsTab = [
        <Route key='top' path="/parent" render={(props) =>
          <Menu.Item as={Link} to="/parent" active={props.match.isExact}>Accounts</Menu.Item>
        }/>,
        <Route key='detail' path="/parent/account/:accountId" component={AccountSpecificTab}/>
      ];


      return (
        <div className="Parents">
          <Menu attached='top' pointing>
            <Menu.Item as={Link} to="/parent" header><KidsbankLogo size='small'/></Menu.Item>
            {accountsTab}
            <Menu.Menu position='right'>
              <Dropdown item trigger={profileHeader}>
                <Dropdown.Menu>
                  <Dropdown.Item text="Logout" onClick={() => this.props.onLogout()}/>
                </Dropdown.Menu>
              </Dropdown>
            </Menu.Menu>
          </Menu>

          <Switch>
            <Route exact path={this.props.match.url} component={Accounts}/>
            <Route exact path={`${this.props.match.url}/account/:accountId`} component={AccountDetail}/>
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

  const {user, accounts} = state;

  return {
    isFetchingUser: user.isFetching,
    profile: user.profile,
    loggedIn: user.loggedIn,
    role: user.role,
    accountsById: accounts.byId
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
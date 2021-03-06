import React, {Component, Fragment} from 'react';
import {connect} from 'react-redux';
import {fetchUserProfile, logoutUser} from '../../actions/user';
import {Dropdown, Image, Loader, Menu, Responsive} from 'semantic-ui-react';
import {Link, Redirect, Route, Switch} from 'react-router-dom';
import KidsbankLogo from '../../components/KidsbankLogo'
import Accounts from './Accounts';
import AccountDetail from './AccountDetail';
import AccountSpecificTab from './AccountSpecificTab';
import Manage from './Manage';

class Parents extends Component {

  render() {
    if (this.props.isFetchingUser) {
      return <Loader active/>
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
          <Responsive as='span' {...Responsive.onlyComputer}>
            {this.props.profile.displayName}
          </Responsive>
        </div>
      );

      const accountsTab = (
        <Fragment>
          <Responsive {...Responsive.onlyComputer} as={Route} path="/parent" render={(props) =>
            <Menu.Item as={Link} to="/parent" active={props.match.isExact}>Accounts</Menu.Item>
          }/>
          <Route path="/parent/:section/:accountId" render={
            (props) => <AccountSpecificTab
              accountId={props.match.params.accountId}
              onMissingAccount={() => props.history.replace('/parent')}
              renderLabel={(account) => {
                switch (props.match.params.section) {
                  case 'account':
                    return `${account.name}'s Transactions`;
                  case 'manage':
                    return `Manage ${account.name}'s Account`;
                  default:
                    return null;
                }
              }
              }
            />
          }/>
        </Fragment>
      );

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
            <Route exact path={`${this.props.match.url}/manage/:accountId`} component={Manage}/>
            <Redirect to={this.props.match.url}/>
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
    isFetchingUser: user.loading,
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
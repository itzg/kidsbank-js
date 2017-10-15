import React, {Component} from 'react';
import {connect} from 'react-redux';
import {fetchUserProfile, logoutUser} from '../../actions/user';
import {Button, Loader} from 'semantic-ui-react';
import {Link, Redirect} from 'react-router-dom';
import KidsbankLogo from '../../components/KidsbankLogo';
import Balance from '../../components/Balance';
import './index.css';
import {fetchKidPrimaryAccountBalance} from "../../actions/accounts";

class Kids extends Component {

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
      return (
        <div className="Kids">
          <div className="header">
            <div><Link to='/kid'><KidsbankLogo size='medium'/></Link></div>

            <div className="info">
              <span>You are logged in as {this.props.profile.displayName}</span>
              <Button onClick={() => this.props.onLogout()}>Logout</Button>
            </div>
          </div>

          <div className='balanceContainer'>
            <Balance fetching={this.props.balance.fetching} balance={this.props.balance.balance} size='large'/>
          </div>
        </div>
      );

    }
  }

  componentDidMount() {
    this.props.fetchUserProfile()
      .then(() => {
        this.props.fetchPrimaryAccountBalance();
      });
  }
}

const
  mapStateToProps = (state) => {

    const {user} = state;

    return {
      isFetchingUser: user.loading,
      profile: user.profile,
      loggedIn: user.loggedIn,
      role: user.role,
      balance: state.accounts.primary.balance
    }
  };

const
  mapDispatchToProps = (dispatch) => {
    return {
      onLogout() {
        dispatch(logoutUser());
      },

      fetchUserProfile() {
        return dispatch(fetchUserProfile());
      },

      fetchPrimaryAccountBalance() {
        return dispatch(fetchKidPrimaryAccountBalance());
      }
    }
  };

export default connect(mapStateToProps, mapDispatchToProps)(Kids);
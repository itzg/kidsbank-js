import React, {Component} from 'react';
import {connect} from 'react-redux';
import {fetchAccountBalance, fetchSingleAccount} from '../../../actions/accounts';
import './index.css';
import CreateTransaction from './CreateTransaction';
import Transactions from './Transactions';
import {createTransaction, loadInitialTransactions} from "../../../actions/transactions";
import Balance from "../../../components/Balance";

/**
 * Used as a router component
 */
class AccountDetail extends Component {

  render() {
    return (
      <div className='AccountDetail'>
        <div className='balance-container'>
          <Balance fetching={this.props.balance.fetching} balance={this.props.balance.balance}/>
        </div>
        <CreateTransaction onCreate={this.props.handleCreateTransaction} sessionTimeout={this.props.sessionTimeout}/>
        <Transactions accountId={this.props.match.params.accountId}/>
      </div>
    );
  }

  componentDidMount() {
    this.props.handleLoadAccount();
  }
}

const mapStateToProps = (state, ownProps) => {
  const accountId = ownProps.match.params.accountId;
  return {
    balance: state.accounts.balances[accountId] || {fetching: true},

    sessionTimeout: state.user.session.timeout
  };
};

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    handleLoadAccount() {
      const accountId = ownProps.match.params.accountId;
      dispatch(fetchSingleAccount(accountId));
      dispatch(loadInitialTransactions(accountId));
      dispatch(fetchAccountBalance(accountId));
    },

    handleCreateTransaction(when, description, amount) {
      const accountId = ownProps.match.params.accountId;
      return dispatch(
        createTransaction(accountId,
          when,
          description,
          amount));
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(AccountDetail);
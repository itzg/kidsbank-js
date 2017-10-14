import React, {Component} from 'react';
import {connect} from 'react-redux';
import {fetchSingleAccount} from '../../../actions/accounts';
import './index.css';
import CreateTransaction from './CreateTransaction';
import Transactions from './Transactions';
import {createTransaction, loadInitialTransactions} from "../../../actions/transactions";

class AccountDetail extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return (
      <div className='AccountDetail'>
        <CreateTransaction onCreate={this.props.handleCreateTransaction}/>
        <Transactions accountId={this.props.match.params.accountId}/>
      </div>
    );
  }

  componentDidMount() {
    this.props.handleLoadAccount(this.props.match.params.accountId);
  }
}

const mapStateToProps = (state) => {
  return {};

};

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    handleLoadAccount(accountId) {
      dispatch(fetchSingleAccount(accountId));
      dispatch(loadInitialTransactions(accountId));
    },

    handleCreateTransaction(when, description, amount) {
      return dispatch(
        createTransaction(ownProps.match.params.accountId,
          when,
          description,
          amount));
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(AccountDetail);
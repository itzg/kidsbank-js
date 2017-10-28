import React from 'react';
import {Header, Segment} from 'semantic-ui-react';
import {connect} from "react-redux";

import './index.css';
import Export from './Export';
import Import from './Import';
import ScheduledTransactions from './ScheduledTranactions';
import {fetchSingleAccount} from "../../../actions/accounts";

class Manage extends React.Component {
  render() {
    return <div className='ParentsManage'>

      <Segment className='BackupRestoreSegment'>
        <Header>Export/Import transactions</Header>
        <Export accountId={this.accountId()}/>
        <Import accountId={this.accountId()}/>
      </Segment>

      <Segment className='ScheduledTransactionsSegment'>
        <Header>Scheduled Transactions</Header>
        <ScheduledTransactions accountId={this.accountId()}/>
      </Segment>
    </div>
  }

  componentDidMount() {
    this.props.handleLoadAccount();
  }

  accountId() {
    return this.props.match.params.accountId;
  }
}

function mapStateToProps(state) {
  return {}
}

function mapDispatchToProps(dispatch, ownProps) {
  return {
    handleLoadAccount() {
      const accountId = ownProps.match.params.accountId;
      dispatch(fetchSingleAccount(accountId));
    }

  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Manage);
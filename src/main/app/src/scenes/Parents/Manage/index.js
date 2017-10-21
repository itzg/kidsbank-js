import React from 'react';
import {connect} from 'react-redux';
import {Header, Segment} from 'semantic-ui-react';

import './index.css';
import {fetchSingleAccount} from "../../../actions/accounts";
import Backup from './Backup';
import Restore from './Restore';

class Manage extends React.Component {
  render() {
    return <div className='ParentsManage'>

      <Segment className='BackupRestore'>
        <Header>Backup/Restore all transactions</Header>
        <Backup accountId={this.accountId()}/>
        <Restore accountId={this.accountId()}/>
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
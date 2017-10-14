import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Loader, Menu} from 'semantic-ui-react';
import {fetchSingleAccount} from '../../actions/accounts';

/**
 * A route component.
 */
class AccountSpecificTab extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    const accountId = this.props.match.params.accountId;
    if (this.props.accountsById && this.props.accountsById[accountId]) {
      const name = this.props.accountsById[accountId].name;
      return <Menu.Item header active={true}>{name}'s Account</Menu.Item>
    }
    else {
      return <Loader active inline/>
    }
  }

  componentDidMount() {
    this.props.fetchAccount(this.props.match.params.accountId);
  }
}

const mapStateToProps = (state) => {
  return {
    accountsById: state.accounts.byId
  }
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchAccount: (accountId) => {
      dispatch(fetchSingleAccount(accountId));
    }
  }
};

export default connect(mapStateToProps, mapDispatchToProps)(AccountSpecificTab);
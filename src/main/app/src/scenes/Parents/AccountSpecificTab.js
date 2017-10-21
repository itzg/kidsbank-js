import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Loader, Menu} from 'semantic-ui-react';
import PT from 'prop-types';
import {fetchSingleAccount} from '../../actions/accounts';

class AccountSpecificTab extends Component {

  render() {
    if (this.props.account) {
      return <Menu.Item header active={true}>{this.props.renderLabel(this.props.account)}</Menu.Item>
    }
    else {
      return <Loader active inline/>
    }
  }

  componentDidMount() {
    this.props.fetchAccount();
  }

  static propTypes = {
    accountId: PT.string,
    renderLabel: PT.func,
    onMissingAccount: PT.func
  }
}

const mapStateToProps = (state, ownProps) => {
  return {
    account: state.accounts.byId && state.accounts.byId[ownProps.accountId]
  }
};

const mapDispatchToProps = (dispatch, ownProps) => {
  return {
    fetchAccount: () => {
      dispatch(fetchSingleAccount(ownProps.accountId))
        .then(() => {
          },
          (err) => {
            if (err.status === 404) {
              ownProps.onMissingAccount();
            }
          }
        );
    }
  }
};

export default connect(mapStateToProps, mapDispatchToProps)(AccountSpecificTab);
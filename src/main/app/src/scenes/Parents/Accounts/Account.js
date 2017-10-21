import React from 'react';
import {connect} from 'react-redux';
import {Button, Card, Message} from 'semantic-ui-react';
import PT from 'prop-types';
import {fetchAccountBalance} from "../../../actions/accounts";
import Balance from '../../../components/Balance';

class Account extends React.Component {
  render() {
    const {
      account,
      shareCode,
      onShare,
      onManage,
      onClick
    } = this.props;

    return <Card onClick={onClick}>
      <Card.Content>
        <Card.Header>{account.name}</Card.Header>

        <Card.Description>
          <Balance balance={this.props.balance.balance} fetching={this.props.balance.fetching} size='tiny'/>
        </Card.Description>
      </Card.Content>
      <Card.Content extra>
        {shareCode &&
        <Message info positive>Kidlink code is {shareCode}</Message>}
        <div>
          <Button onClick={onManage}>Manage</Button>
          <Button onClick={onShare}>Share</Button>
        </div>
      </Card.Content>
    </Card>
  }

  componentDidMount() {
    this.props.fetchBalance();
  }

  static propTypes = {
    account: PT.object,
    shareCode: PT.string,
    onShare: PT.func,
    onClick: PT.func,
    onManage: PT.func
  }
}

function mapStateToProps(state, ownProps) {
  return {
    balance: state.accounts.balances[ownProps.account.id] || {fetching: true}
  }
}

function mapDispatchToProps(dispatch, ownProps) {
  return {
    fetchBalance() {
      dispatch(fetchAccountBalance(ownProps.account.id));
    }
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Account);
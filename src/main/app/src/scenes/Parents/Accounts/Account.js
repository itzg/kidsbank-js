import React from 'react';
import {connect} from 'react-redux';
import {Button, Card, Message} from 'semantic-ui-react';
import PropTypes from 'prop-types';
import {fetchAccountBalance} from "../../../actions/accounts";
import Balance from '../../../components/Balance';

class Account extends React.Component {
  render() {
    const {
      account,
      shareCode,
      onShare,
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
          <Button onClick={onShare}>Share</Button>
        </div>
      </Card.Content>
    </Card>
  }

  componentDidMount() {
    this.props.fetchBalance();
  }

  static propTypes = {
    account: PropTypes.object,
    shareCode: PropTypes.string,
    onShare: PropTypes.func,
    onClick: PropTypes.func
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
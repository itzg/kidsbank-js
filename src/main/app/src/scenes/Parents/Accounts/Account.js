import React from 'react';
import {connect} from 'react-redux';
import {Button, Card, Message, Popup} from 'semantic-ui-react';
import PT from 'prop-types';
import CopyToClipboard from 'react-copy-to-clipboard';
import {fetchAccountBalance} from "../../../actions/accounts";
import Balance from '../../../components/Balance';
import {createNotification} from "../../../actions/notifications";

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
      <Card.Content extra onClick={(evt) => evt.stopPropagation()}>
        {shareCode &&
        <CopyToClipboard text={shareCode} onCopy={this.props.kidlinkCodeCopied}>
          <Message info positive>Kidlink code is {shareCode}</Message>
        </CopyToClipboard>
        }
        <div>
          <Popup trigger={<Button onClick={onManage}>Manage</Button>}
                 content='Schedule transactions, etc'
          />
          <Popup trigger={<Button onClick={onShare}>Share</Button>}
                 content='Generate a kidlink code for kid registration'/>
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
    },

    kidlinkCodeCopied() {
      dispatch(createNotification('Kidlink code copied to clipboard'));
    }
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Account);
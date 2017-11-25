import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Button, Loader, Message} from 'semantic-ui-react';
import CreateAccountModal from "./CreateAccountModal";
import {createAccount, fetchParentManagedAccounts, shareAccount} from '../../../actions/accounts';
import Account from './Account';
import './index.css';
import {dismissInstruction} from "../../../actions/persisted";

const INSTRUCTION_ID_ACCOUNTS_CARD = 'accountsCardNote';

class Accounts extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isCreating: false,
      shareCodes: {}
    }
  }

  render() {
    let listArea;

    if (this.props.loading) {
      listArea = <Loader active inline/>
    }

    const hasAccounts = this.props.accounts.length !== 0;

    if (!hasAccounts) {
      listArea = <Message>No accounts yet. Go ahead and create one.</Message>
    }
    else {
      const cards =
        this.props.accounts.map((account) => <Account
          key={account.id}
          account={account}
          shareCode={this.state.shareCodes[account.id]}
          onShare={(evt) => {
            evt.stopPropagation();
            this.handleShareClick(account);
          }}
          onClick={() => this.handleAccountClick(account)}
          onManage={(evt) => {
            evt.stopPropagation();
            this.handleManage(account);
          }}
        />);

      listArea = (
        <div className="List">
          {cards}
        </div>
      )
    }

    return <div className="Accounts">
      {hasAccounts &&
      <Message info compact hidden={this.props.dismissedCardInstruction}
               onDismiss={this.props.handleCardInstructionDismiss}
               icon='idea'
               content='Click one of the cards below to view and edit transactions.'
      />
      }
      {listArea}
      <div className="ListActions">
        <Button onClick={this.handleCreateStart} disabled={this.state.isCreating}>Create an account</Button>
      </div>
      <CreateAccountModal open={this.state.isCreating} onDone={this.handleCreateDone}
                          onSubmit={this.handleCreateSubmit}/>
    </div>
  }

  // noinspection JSUnusedGlobalSymbols
  componentDidMount = () => {
    this.props.handleMount();
  };

  handleCreateStart = () => {
    this.setState({isCreating: true});
  };

  handleCreateDone = () => {
    this.setState({isCreating: false});
  };

  handleShareClick = (account) => {
    this.props.handleShare(account)
      .then(code => {
        this.setState((prevState) => {
          return {
            shareCodes: {
              ...prevState.shareCodes,
              [account.id]: code
            }
          }
        })
      });
  };

  handleManage = (account) => {
    this.props.history.push(`/parent/manage/${account.id}`)
  };

  handleCreateSubmit = (values) => {
    return this.props.handleCreateAccount(values)
      .then(() => {
        this.handleCreateDone();
      })
  };

  handleAccountClick = (account) => {
    this.props.history.push(`/parent/account/${account.id}`);
  }
}

const mapStateToProps = (state) => {
  const {
    accounts,
    persisted
  } = state;

  return {
    loading: accounts.loading,
    accounts: accounts.byId ? Object.entries(accounts.byId).map(entry => entry[1]) : [],
    dismissedCardInstruction: (persisted.instructionsDismissed || []).includes(INSTRUCTION_ID_ACCOUNTS_CARD)
  }
};

const mapDispatchToProps = (dispatch) => {
  return {
    handleMount() {
      dispatch(fetchParentManagedAccounts());
    },

    handleShare(account) {
      return dispatch(shareAccount(account));
    },

    handleCreateAccount(fields) {
      return dispatch(createAccount(fields));
    },

    handleCardInstructionDismiss() {
      return dispatch(dismissInstruction(INSTRUCTION_ID_ACCOUNTS_CARD));
    }
  }
};

export default connect(mapStateToProps, mapDispatchToProps)(Accounts);
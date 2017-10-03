import React, {Component} from 'react';
import {connect} from 'react-redux';
import './Accounts.css';
import {Button, Card, Loader, Message} from 'semantic-ui-react';
import CreateAccountModal from "./CreateAccountModal";
import {createAccount, fetchParentManagedAccounts, shareAccount} from '../../actions/accounts';
import {submitForm} from "../../actions/Forms";

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

    if (this.props.isFetching) {
      listArea = <Loader/>
    }

    if (this.props.accounts.length === 0) {
      listArea = <div>No accounts yet. Go ahead and create one.</div>
    }
    else {
      const cards =
        this.props.accounts.map((account) => <Card key={account.id}>
          <Card.Content>
            <Card.Header>{account.name}</Card.Header>
          </Card.Content>
          <Card.Content extra>
            {this.state.shareCodes[account.id] &&
            <Message info positive>Kidlink code is {this.state.shareCodes[account.id]}</Message>}
            <div>
              <Button onClick={() => this.handleShareClick(account)}>Share</Button>
            </div>
          </Card.Content>
        </Card>);

      listArea = (
        <div className="List">
          {cards}
        </div>
      )
    }

    return <div className="Accounts">
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

  handleCreateSubmit = (values) => {
    return this.props.handleCreateAccount(values)
      .then(() => {
        this.handleCreateDone();
      })
  }
}

const mapStateToProps = (state) => {
  const {accounts} = state;

  return {
    isFetching: accounts.isFetching,
    accounts: accounts.list
  }
};

const mapDispatchToProps = (dispatch) => {
  return {
    handleMount: () => {
      dispatch(fetchParentManagedAccounts());
    },

    handleShare: (account) => {
      return dispatch(shareAccount(account));
    },

    handleCreateAccount: (fields) => {
      return dispatch(createAccount(fields));
    }
  }
};

export default connect(mapStateToProps, mapDispatchToProps)(Accounts);
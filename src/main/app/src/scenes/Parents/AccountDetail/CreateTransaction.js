import React, {Component} from 'react';
import {Button} from 'semantic-ui-react';
import CreateTransactionForm from './CreateTransactionForm';
import PropTypes from 'prop-types';

class CreateTransaction extends Component {
  constructor(props) {
    super(props);
    this.state = {
      creating: false
    }
  }

  render() {
    if (this.state.creating) {
      return <CreateTransactionForm onCreate={this.props.onCreate} onClose={this.handleStopCreate}/>
    }
    else {
      return <Button size='large' primary onClick={this.handleStartCreate}>Create Transaction</Button>
    }
  }

  handleStartCreate = () => {
    this.setState({creating: true});
  };

  handleStopCreate = () => {
    this.setState({creating: false});
  };

  static propTypes = {
    onCreate: PropTypes.func
  }
}

export default CreateTransaction;
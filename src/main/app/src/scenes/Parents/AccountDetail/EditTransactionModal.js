import React from 'react';
import {Button, Modal} from 'semantic-ui-react';
import {connect} from 'react-redux';
import EditTransactionForm from './EditTransactionForm'
import {deleteTransaction, saveTransaction} from "../../../actions/transactions";

class EditTransactionModal extends React.Component {

  state = {
    open: false
  };

  open = () => {
    this.setState({open: true})
  };
  close = () => {
    this.setState({open: false})
  };

  render() {
    return (
      <Modal open={this.state.open} onClose={this.close} trigger={
        <Button onClick={this.open} disabled={!this.props.transaction}>Edit Transaction</Button>
      }>
        <Modal.Header>Edit Transaction</Modal.Header>
        <Modal.Content>
          <EditTransactionForm initialValues={this.props.transaction}
                               onSubmit={this.props.dispatchSave} onSubmitSuccess={this.close}
                               onCancel={this.close} onDelete={this.handleDelete}/>
        </Modal.Content>
      </Modal>
    )
  }

  componentWillReceiveProps(nextProps) {
    if (!nextProps.transaction) {
      this.close();
    }
  }

  handleDelete = () => {
    this.props.dispatchDelete()
      .then(this.close)
  }
}

function mapStateToProps(state) {
  return {}
}

function mapDispatchToProps(dispatch, ownProps) {
  return {
    dispatchSave(values) {
      return dispatch(saveTransaction(values));
    },

    dispatchDelete() {
      return dispatch(deleteTransaction(ownProps.transaction.id, ownProps.transaction.accountId))
    }
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(EditTransactionModal);
import React, {Component} from 'react';
import {Button, Form, Modal} from 'semantic-ui-react';
import {Field, reduxForm} from 'redux-form';

class CreateAccountModal extends Component {
  constructor(props) {
    super(props);
  }

  render() {

    const form = (
      <Form onSubmit={this.props.handleSubmit}>
        <Field component={Form.Input} name='name' required label='Account/Child Name'/>
      </Form>
    );

    return (
      <Modal
        size='small'
        open={this.props.open}
      >
        <Modal.Header>Create a kid's account</Modal.Header>
        <Modal.Content>
          {form}
        </Modal.Content>
        <Modal.Actions>
          <Button onClick={this.handleCancel}>Cancel</Button>
          <Button positive onClick={this.handleCreate}>Create</Button>
        </Modal.Actions>
      </Modal>
    )

  }

  handleCancel = () => {
    this.props.onDone();
  };

  handleCreate = () => {
    this.props.handleSubmit()
      .then(() => this.props.onDone());
  };

}


export default reduxForm({
  form: 'createAccount'
})(CreateAccountModal);
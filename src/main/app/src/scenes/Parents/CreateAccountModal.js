import React, {Component} from 'react';
import {Button, Form, Input, Modal} from 'semantic-ui-react';
import {Field, reduxForm} from 'redux-form';
import ValidatedFormField from "../../components/ValidatedFormField";

class CreateAccountModal extends Component {
  constructor(props) {
    super(props);
  }

  render() {

    // handleSubmit is injected by reduxForm
    const form = (
      <Form onSubmit={this.props.handleSubmit}>
        <Field name='name' component={ValidatedFormField} control={Input} required label='Account/Child Name'/>
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
          <Button positive onClick={this.props.handleSubmit}>Create</Button>
        </Modal.Actions>
      </Modal>
    )

  }

  handleCancel = () => {
    this.props.onDone();
  };

}


export default reduxForm({
  form: 'createAccount'
})(CreateAccountModal);
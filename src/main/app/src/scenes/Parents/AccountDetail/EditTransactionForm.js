import React from 'react';
import {Button, Form, Input} from "semantic-ui-react";
import {Field, propTypes as formPropTypes, reduxForm} from "redux-form";
import PropTypes from 'prop-types';
import ValidatedFormField from "../../../components/ValidatedFormField";
import FormActions from "../../../components/FormActions";
import {nonEmpty, number, required} from "../../../components/validators";
import DatePickerField from "../../../components/DatePickerField";
import MoneyField from "../../../components/MoneyField";

class EditTransactionForm extends React.Component {
  render() {
    return (
      <Form onSubmit={this.props.handleSubmit}>
        <Field name='description' component={ValidatedFormField} control={Input} validate={[required, nonEmpty]}
               label='Description'/>
        <Field name='when' component={DatePickerField} validate={required} label='Date'/>
        <Field name='amount'
               component={MoneyField} validate={number}
               label='Amount'/>

        <FormActions>
          <Button type='submit' primary disabled={this.props.pristine || this.props.invalid}>Save</Button>
          <Button onClick={this.handleDelete} negative>Delete</Button>
          <Button onClick={this.props.onCancel}>Cancel</Button>
        </FormActions>
      </Form>
    )
  }

  handleDelete = (evt) => {
    // Need to prevent default or else it treats like a submit...not sure why
    evt.preventDefault();
    this.props.onDelete();
  };

  static propTypes = {
    ...formPropTypes,
    onDelete: PropTypes.func.isRequired,
    onCancel: PropTypes.func.isRequired
  }
}

export default reduxForm({
  form: 'editTransaction',
  enableReinitialize: true
})(EditTransactionForm);
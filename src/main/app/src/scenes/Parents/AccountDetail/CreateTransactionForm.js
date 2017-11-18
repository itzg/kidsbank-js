import React from 'react';
import {Field as ReduxField, reduxForm} from 'redux-form';
import {Form, Input, Segment} from 'semantic-ui-react';
import {Prompt} from 'react-router-dom';
import 'react-datepicker/dist/react-datepicker.css';
import {nonEmpty, number, required} from '../../../components/validators';
import MoneyField from "../../../components/MoneyField";
import DatePickerField from "../../../components/DatePickerField";
import ValidatedFormField from "../../../components/ValidatedFormField";

function CreateTransactionForm(props) {
  const promptMsg = props.sessionTimeout ? "Your login session has expired. Login again now?" :
    "You have unsaved changes. Navigate anyway?";

  return (
    <Segment secondary><Form onSubmit={props.handleSubmit}>
      <Prompt when={props.dirty} message={promptMsg}/>
      <Form.Group>
        <ReduxField name='when' component={DatePickerField} validate={required} label='Date' width={4}/>
        <ReduxField name='description' component={ValidatedFormField} control={Input} validate={[required, nonEmpty]}
                    label='Description'
                    width={6}/>
        <ReduxField name='income' label='Income'
                    component={MoneyField} control={MoneyField} validate={number}
                    width={2}/>
        <ReduxField name='expense' label='Expense'
                    component={MoneyField} validate={number}
                    width={2}/>
      </Form.Group>
      <Form.Group>
        <Form.Button type='submit' primary disabled={props.invalid} loading={props.submitting}>Create</Form.Button>
        <Form.Button onClick={props.onClose}>Cancel</Form.Button>
      </Form.Group>
    </Form></Segment>
  );
}

function validate(values, props) {
  let result = {};

  if (!values.income && !values.expense) {
    result.income = 'income or expense is required';
    result.expense = 'income or expense is required';
  }

  return result;
}

function onSubmit(values, dispatch, props) {
  return props.onCreate(values.when, values.description, (values.income || 0) - (values.expense || 0));
}

export default reduxForm({
  form: 'createTransaction',
  validate,
  initialValues: {
    when: new Date()
  },
  onSubmit,
  onSubmitSuccess(result, dispatch, props) {
    props.onClose();
  }
})(CreateTransactionForm)
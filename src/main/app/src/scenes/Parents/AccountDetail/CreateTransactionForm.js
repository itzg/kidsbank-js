import React from 'react';
import {Field, reduxForm} from 'redux-form';
import {Form, Input} from 'semantic-ui-react';
import DatePicker from 'react-datepicker';
import moment from 'moment';
import 'react-datepicker/dist/react-datepicker.css';
import {nonEmpty, number, required} from '../../../components/validators';

function DatePickerField(props) {
  let {input} = props;

  return <Form.Field control={DatePicker} label={props.label} selected={input.value} onChange={input.onChange}/>
}

function MoneyField(props) {
  let {input} = props;
  return (
    <Form.Field width={props.width}>
      <label>{props.label}</label>
      <Input label='$' value={input.value} onChange={input.onChange}/>
    </Form.Field>
  );
}

function CreateTransactionForm(props) {
  return (
    <Form onSubmit={props.handleSubmit}>
      <Form.Group>
        <Field name='when' component={DatePickerField} validate={required} label='Date' width={4}/>
        <Field name='description' component={Form.Input} validate={[required, nonEmpty]} label='Description' width={6}/>
        <Field name='income'
               component={MoneyField} validate={number}
               label='Income' width={2}/>
        <Field name='expense'
               component={MoneyField} validate={number}
               label='Expense' width={2}/>
      </Form.Group>
      <Form.Group>
        <Form.Button type='submit' primary disabled={props.invalid} loading={props.submitting}>Create</Form.Button>
        <Form.Button onClick={props.onClose}>Cancel</Form.Button>
      </Form.Group>
    </Form>
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
  props.onCreate(values.when.toDate(), values.description, (values.income || 0) - (values.expense || 0))
    .then(props.onClose);
}

export default reduxForm({
  form: 'createTransaction',
  validate,
  initialValues: {
    when: moment()
  },
  onSubmit
})(CreateTransactionForm)
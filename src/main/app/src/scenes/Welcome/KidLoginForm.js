import React from 'react';
import {Field, reduxForm} from 'redux-form';
import ValidatedFormField from "../../components/ValidatedFormField";
import {Button, Form, Input, Message} from "semantic-ui-react";

const KidRegisterForm = (props) => {
  const {
    onSwitchToRegister
  } = props;
  return (
    <Form className={props.className} onSubmit={props.handleSubmit}>
      <Field name='username' component={ValidatedFormField} control={Input} label='Username'/>
      <Field name='password' type='password' component={ValidatedFormField} control={Input} label='Password'/>

      {props.invalid && props.error &&
      <Message negative content={props.error}/>}

      <div className='Actions'>
        <Button disabled={props.invalid} loading={props.submitting} type='submit' primary>Login</Button>
        or <Button basic onClick={onSwitchToRegister}>Switch to Register</Button>
      </div>
    </Form>
  );
};

export default reduxForm({
  form: 'kidLogin'
})(KidRegisterForm);
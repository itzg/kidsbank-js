import React from 'react';
import {Field, reduxForm} from 'redux-form';
import ValidatedFormField from "../../components/ValidatedFormField";
import {Button, Form, Icon, Input, Message} from "semantic-ui-react";

const KidRegisterForm = (props) => {
  const {
    onSwitchToLogin
  } = props;
  return (
    <Form className={props.className} onSubmit={props.handleSubmit}>
      <Message icon>
        <Icon name='info'/>
        <Message.Content>
          <Message.Header>Before registering</Message.Header>
          Please ask your parent to enter Kidsbank, create an account for you, and click "Share".
          You will use the kidslink code that is generated to register your own username and password here.
        </Message.Content>
      </Message>
      <Field name='username' component={ValidatedFormField} control={Input} label='Username'/>
      <Field name='password' type='password' component={ValidatedFormField} control={Input} label='Password'/>
      <Field name='passwordAgain' type='password' component={ValidatedFormField} control={Input}
             label='Re-enter your password'/>
      <Field name='kidlinkCode' component={ValidatedFormField} control={Input} label='Kidlink code'/>

      {props.invalid && props.error &&
      <Message negative content={props.error}/>}

      <div className='Actions'>
        <Button disabled={props.invalid} loading={props.submitting} type='submit' primary>Register</Button>
        or <Button basic onClick={onSwitchToLogin}>Switch to Login</Button>
      </div>
    </Form>
  );
};

const validate = (values) => {
  const err = {};

  if (values.password || values.passwordAgain) {
    if (values.password !== values.passwordAgain) {
      err.passwordAgain = 'Passwords need to match';
    }
  }

  return err;
};

export default reduxForm({
  form: 'kidRegister',
  validate
})(KidRegisterForm);
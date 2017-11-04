import React from 'react';
import {Field as ReduxField, reduxForm} from 'redux-form';
import ValidatedFormField from "../../components/ValidatedFormField";
import {Button, Form, Input, Message} from "semantic-ui-react";
import {required} from "../../components/validators";
import RegisterInstructions from './RegisterInstructions';

const KidRegisterForm = (props) => {
  const {
    onSwitchToLogin
  } = props;
  return (
    <Form className={props.className} onSubmit={props.handleSubmit}>
      <RegisterInstructions/>
      <ReduxField name='username' label='Username'
                  component={ValidatedFormField} control={Input} warnWhenPristine={true}
                  validate={required}/>
      <ReduxField name='password' type='password' label='Password'
                  component={ValidatedFormField} control={Input} warnWhenPristine={true}
                  validate={required}/>
      <ReduxField name='passwordAgain' type='password' label='Re-enter your password'
                  component={ValidatedFormField} control={Input} warnWhenPristine={true}
                  validate={required}/>
      <ReduxField name='kidlinkCode' label='Kidlink code'
                  component={ValidatedFormField} control={Input} warnWhenPristine={true}
                  validate={required}/>

      {props.invalid && props.error &&
      <Message negative content={props.error}/>}

      <div className='Actions'>
        <Button disabled={props.invalid || props.pristine} loading={props.submitting} type='submit'
                primary>Register</Button>
        or already have an account? <Button basic onClick={onSwitchToLogin}>Login</Button>
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
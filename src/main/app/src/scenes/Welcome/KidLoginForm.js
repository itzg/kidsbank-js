import React from 'react';
import {Field as ReduxField, reduxForm} from 'redux-form';
import ValidatedFormField from "../../components/ValidatedFormField";
import {Button, Form, Input, Message} from "semantic-ui-react";
import {required} from "../../components/validators";

const KidRegisterForm = (props) => {
  const {
    onSwitchToRegister
  } = props;
  return (
    <Form className={props.className} onSubmit={props.handleSubmit}>
      <ReduxField name='username' label='Username'
                  component={ValidatedFormField} control={Input} warnWhenPristine={true}
                  validate={required}/>
      <ReduxField name='password' type='password' label='Password'
                  component={ValidatedFormField} control={Input} warnWhenPristine={true}
                  validate={required}/>

      {props.invalid && props.error &&
      <Message negative content={props.error}/>}

      <div className='Actions'>
        <Button disabled={props.invalid || props.pristine} loading={props.submitting} type='submit'
                primary>Login</Button>
        or need an account? <Button basic onClick={onSwitchToRegister}>Register</Button>
      </div>
    </Form>
  );
};

export default reduxForm({
  form: 'kidLogin'
})(KidRegisterForm);
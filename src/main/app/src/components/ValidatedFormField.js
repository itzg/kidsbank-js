import React from 'react';
import {Form, Message} from 'semantic-ui-react';

const ValidatedFormField = props => {
  const {meta} = props;
  return (
    <div>
      <Form.Field {...props} error={meta.invalid}/>
      {meta.warning &&
      <Message content={meta.warning}/>}
      {meta.invalid &&
      <Message content={meta.error} negative/>}
    </div>
  );
};

export default ValidatedFormField;
import React from 'react';
import {Form} from 'semantic-ui-react';
import './ValidatedFormField.css';

/**
 * This is intended to be used as the component property of a redux-form Field. The <code>control</code> prop is
 * passed into the contained Form.Field, so the shorthands such as Form.Input aren't used here.
 *
 * <code>
 *     &lt;Field name='name' component={ValidatedFormField} control={Input} required label='Account/Child Name'/&gt;
 * </code>
 *
 * @param props
 * @returns {XML}
 * @constructor
 */
const ValidatedFormField = props => {
  const {meta} = props;
  return (
    <div className='ValidatedFormField'>
      <Form.Field {...props} error={meta.invalid}/>
      {meta.warning && <div className='Message Warning'>{meta.warning}</div>}
      {meta.invalid && <div className='Message Error'>{meta.error}</div>}
    </div>
  );
};

export default ValidatedFormField;
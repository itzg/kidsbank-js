import React from 'react';
import {Form, Label} from 'semantic-ui-react';
import PropTypes from 'prop-types';
// eslint-disable-next-line
import {Field} from "redux-form";

/**
 * This is a {@link Form.Field} that is intended to be used as the <code>component</code> property of a redux-form {@link Field}.
 * The <code>control</code> property is passed into the contained {@link Form.Field} and
 * <code>controlProps</code> in turn is passed to the created <code>control</code> element as its props.
 *
 * <code>
 *     import {Field as ReduxField} from 'redux-form';
 *     ...
 *     &lt;ReduxField name='name' label='Name' component={ValidatedFormField} control={Input}/&gt;
 * </code>
 *
 * @param props
 * @returns {XML}
 * @constructor
 */
const ValidatedFormField = props => {
  const {meta, input, label, control, className} = props;
  return (
    <Form.Field className={className} error={meta.invalid && meta.dirty} width={props.width}>
      {label && <label>{label}</label>}
      {control && React.createElement(control, {...input, ...props.controlProps, type: props.type})}

      {meta.warning && (props.warnWhenPristine || meta.dirty) &&
      <Label basic color='orange' pointing>{meta.warning}</Label>}

      {meta.invalid && (props.warnWhenPristine || meta.dirty) &&
      <Label basic color='red' pointing>{meta.error}</Label>}
    </Form.Field>
  );
};

ValidatedFormField.propTypes = {
  label: Form.Field.propTypes.label,
  control: Form.Field.propTypes.control,
  controlProps: PropTypes.object,
  width: Form.Field.propTypes.width,
  warnWhenPristine: PropTypes.bool,
  type: Form.Field.propTypes.type,
  className: PropTypes.string
};

export default ValidatedFormField;
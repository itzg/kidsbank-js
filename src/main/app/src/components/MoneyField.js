import React from 'react';
import {Input} from 'semantic-ui-react';
import ValidatedFormField from './ValidatedFormField';

/**
 * MoneyField is a {@link ValidatedFormField} that visually decorates the input field itself with a currency label.
 * @param props
 * @returns {*}
 * @constructor
 */
export default function MoneyField(props) {
  return (
    <ValidatedFormField {...props} control={Input} controlProps={{label: '$'}}/>
  );
}

MoneyField.propTypes = ValidatedFormField.propTypes;

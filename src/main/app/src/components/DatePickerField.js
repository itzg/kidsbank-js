import React from 'react';
import DatePicker from "react-datepicker";
import {Form} from 'semantic-ui-react';
import moment from "moment";

export default function DatePickerField(props) {
  let {input} = props;

  return <Form.Field control={DatePicker} label={props.label}
                     selected={moment(input.value)}
                     onChange={(mDate) => input.onChange(mDate.toDate())}/>
}


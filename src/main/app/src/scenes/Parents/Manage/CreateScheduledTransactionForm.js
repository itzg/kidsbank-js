import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Field, formValueSelector, propTypes, reduxForm} from 'redux-form';
import {Button, Form, Input, Message, Segment, Select} from 'semantic-ui-react';
import PropTypes from 'prop-types';

import {dayOfMonth, nonEmpty, number} from "../../../components/validators";
import ValidatedFormField from "../../../components/ValidatedFormField";
import MoneyField from "../../../components/MoneyField";
import {dayOfWeekOptions} from "../../../components/locale";
import './CreateScheduledTransactionForm.css';

class CreateScheduledTransactionForm extends Component {

  render() {
    return <Segment secondary><Form onSubmit={this.props.handleSubmit} className='CreateScheduledTransactionForm'>
      <Field name='description' label='Description' component={ValidatedFormField} control={Input} validate={nonEmpty}/>
      <Field name='amount' label='Amount' component={MoneyField} validate={number}/>
      {!this.props.intervalType &&
      <Form.Field>
        <label>Frequency</label>
        <Button.Group>
          <Button onClick={() => this.props.change('intervalType', 'WEEKLY')}>Weekly</Button>
          <Button.Or/>
          <Button onClick={() => this.props.change('intervalType', 'MONTHLY')}>Monthly</Button>
        </Button.Group>
      </Form.Field>
      }
      {this.props.intervalType === 'WEEKLY' &&
      <Form.Field>
        <label>Day of the week</label>
        <Field name='weekly.dayOfWeek'
               component={(field) => <Select onChange={(evt, data) => field.input.onChange(data.value)}
                                             value={field.input.value}
                                             options={dayOfWeekOptions} width={2}/>}/>
      </Form.Field>
      }
      {this.props.intervalType === 'MONTHLY' &&
      <Form.Field>
        <label>Day of the month</label>
        <Field name='monthly.dayOfMonth' className='dayOfMonth'
               component={ValidatedFormField} control={Input} validate={dayOfMonth}/>
      </Form.Field>
      }

      {
        this.props.submitFailed && this.props.error &&
        <Message negative
                 content={this.props.error} header='Failed to create'/>
      }


      <div>
        <Button type='submit' disabled={this.props.pristine || this.props.invalid}
                loading={this.props.submitting} primary>Create</Button>
        <Button onClick={this.props.onCancel}>Cancel</Button>
      </div>
    </Form></Segment>
  }

  static propTypes = {
    ...propTypes,
    onCancel: PropTypes.func
  }
}

const form = 'CreateScheduledTransaction';

function mapStateToProps(state) {
  return {
    intervalType: formValueSelector(form)(state, 'intervalType'),
  }
}

export default reduxForm({
  form
})(connect(mapStateToProps)(CreateScheduledTransactionForm));
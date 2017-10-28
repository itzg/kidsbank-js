import React, {Component} from 'react';
import {Divider} from 'semantic-ui-react';
import './FormActions.css';

class FormActions extends Component {
  render() {
    return <div className='FormActions'>
      <Divider/>
      {this.props.children}
    </div>
  }
}

export default FormActions;
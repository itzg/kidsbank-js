import React, {Component} from 'react';
import {Icon, Message} from 'semantic-ui-react';

class RegisterInstructions extends Component {
  constructor(props) {
    super(props);

    this.state = {
      hidden: false
    }
  }

  render() {
    return (
      <Message icon onDismiss={this.hide} hidden={this.state.hidden}>
        <Icon name='info'/>
        <Message.Content>
          <Message.Header>Before registering</Message.Header>
          Please ask your parent to enter Kidsbank, create an account for you, and click "Share".
          You will use the kidslink code that is generated to register your own username and password here.
        </Message.Content>
      </Message>
    );
  }

  hide = () => {
    this.setState({hidden: true});
  }
}

export default RegisterInstructions;
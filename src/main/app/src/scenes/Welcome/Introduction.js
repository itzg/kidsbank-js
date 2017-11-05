import React from 'react';
import {connect} from 'react-redux';
import {Button, Icon, Message} from 'semantic-ui-react';
import './Introduction.css';
import {dismissIntro} from "../../actions/persisted";
import L from "../../components/LinkOpenNew";

const Introduction = (props) => {
  return (
    <div className='Introduction'>
      <Message floating={true} size='large' compact onDismiss={props.dismissIntro} icon>
        <Icon name='comment'/>
        <Message.Content>
          <Message.Header>
            Welcome to kids bank
          </Message.Header>
          <div className='main'>
            <div>kids bank is a totally virtual way for parents and kids to keep track of allowance, spending money,
              IOUs, etc.
            </div>

            <div><em>No real money is involved</em>. Instead parents make a purchase in the real world,
              record that transaction, and together parents and kids keep track of spending and budgeting.
            </div>
          </div>
          <div className='actions'>
            <Button primary onClick={props.dismissIntro}>Got it. Let's go.</Button>
          </div>
          <div className='githubLinks'>
            <Icon name='github'/>
            <div>
              <L href='https://github.com/itzg/kidsbank-js/issues'>Submit issues and ideas</L>,
            </div>
            <div>
              <L href='https://github.com/itzg/kidsbank-js'>check out the source code</L>, or
            </div>
            <div>
              <L href='https://github.com/itzg/kidsbank-js/pulls'>contribute changes</L>
            </div>
          </div>
        </Message.Content>
      </Message>
    </div>
  );
};

function mapStateToProps(state) {
  return {};
}

function mapDispatchToProps(dispatch) {
  return {
    dismissIntro() {
      dispatch(dismissIntro());
    }
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(Introduction);
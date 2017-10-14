import React from 'react';
import {Button, Card, Message} from 'semantic-ui-react';

const Account = (props) => {
  const {
    account,
    shareCode,
    onShare,
    onClick
  } = props;

  return <Card onClick={onClick}>
    <Card.Content>
      <Card.Header>{account.name}</Card.Header>
    </Card.Content>
    <Card.Content extra>
      {shareCode &&
      <Message info positive>Kidlink code is {shareCode}</Message>}
      <div>
        <Button onClick={onShare}>Share</Button>
      </div>
    </Card.Content>
  </Card>
};

export default Account;
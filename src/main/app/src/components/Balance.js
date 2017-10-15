import React from 'react';
import {Loader, Statistic} from 'semantic-ui-react';
import PropTypes from 'prop-types';
import {formatCurrency} from './locale';

function Balance(props) {
  if (props.fetching) {
    return <Loader inline active/>;
  }
  return <Statistic value={formatCurrency(props.balance)} label='Balance' size={props.size}/>
}

Balance.propTypes = {
  fetching: PropTypes.bool,
  balance: PropTypes.number,
  size: PropTypes.string
};

export default Balance;
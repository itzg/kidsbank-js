import React from 'react';
import moment from 'moment';
import L from './components/LinkOpenNew';
import {Icon} from 'semantic-ui-react';
import './Footer.css';

function currentYear() {
  return moment().year();
}

export default (props) => {
  return <footer className='Footer'>
    <div>
      <L href='https://github.com/itzg/kidsbank-js/blob/master/README.md'>What is kidsbank?</L>
    </div>
    <div className='fill'>
      kidsbank &copy; {currentYear()} Geoff Bourne
    </div>
    <div>
      <L href='https://github.com/itzg/kidsbank-js/issues'><Icon name='external'/>Report Issues</L>
    </div>
  </footer>
}
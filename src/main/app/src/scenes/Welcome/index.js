import React, {Component} from 'react';

import './welcome.css';
import logo from './kidsbank-main.png';

class Welcome extends Component {
  render() {
    return (
      <div className="Welcome-banner">
        <h1>Welcome to</h1>
        <img src={logo} alt="kidsbank"/>
      </div>
    );
  }
}

export default Welcome;
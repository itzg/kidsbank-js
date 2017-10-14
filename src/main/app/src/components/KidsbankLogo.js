import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {Image} from 'semantic-ui-react';

import './KidsbankLogo.css';
import logoSrc from './kidsbank-main.png';

class KidsbankLogo extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    return <Image className="KidsbankLogo" src={logoSrc} centered={this.props.centered}
                  size={this.props.size}/>
  }

  // noinspection JSUnusedGlobalSymbols
  static propsTypes = {
    size: PropTypes.bool,
    centered: PropTypes.bool
  };

}

export default KidsbankLogo;
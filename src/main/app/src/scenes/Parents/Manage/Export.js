import React, {Component} from 'react';
import {Button} from 'semantic-ui-react';
import PropTypes from 'prop-types';
import Downloader from '../../../components/Downloader';

class Backup extends Component {
  constructor(props) {
    super(props);

    this.state = {
      clicked: false,
      downloading: false,
    };
  }

  render() {
    return <div className='Backup'>
      <Button onClick={this.handleClick} loading={this.state.downloading}>Export</Button>
      <Downloader download={this.state.clicked}
                  location={`/api/parent/accounts/${this.props.accountId}/export?format=xlsx`}
                  onWillDownload={this.handleWillDownload}
                  onDidDownload={this.handleDidDownload}
      />
    </div>
  }

  handleClick = () => {
    this.setState({clicked: true});
  };

  handleWillDownload = () => {
    this.setState({downloading: true});
  };

  handleDidDownload = () => {
    this.setState({
      downloading: false,
      clicked: false
    })
  };

  static propTypes = {
    accountId: PropTypes.string.isRequired
  }
}

export default Backup;
import React, {Component} from 'react';
import {Button} from 'semantic-ui-react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import FileInput from '../../../components/FileInput';
import {importTransactions} from "../../../actions/transactions";
import {createNotification} from "../../../actions/notifications";

class Restore extends Component {

  constructor(props) {
    super(props);

    this.state = {
      fileList: []
    }
  }

  render() {
    return <div className='Restore'>
      <Button disabled={this.state.fileList.length === 0}
              onClick={this.handleImport}
      >Import</Button>
      <FileInput onChange={this.handleFileInputChange} fileList={this.state.fileList}/>
    </div>
  }

  handleFileInputChange = (fileList) => {
    this.setState({
      fileList
    });
  };

  handleImport = () => {
    this.props.dispatch(importTransactions(this.props.accountId, this.state.fileList))
      .then(results => {
        this.setState({fileList: []})
        this.props.dispatch(createNotification(`Restored ${results.processed} transactions`));
      });
  };

  static propTypes = {
    accountId: PropTypes.string.isRequired
  }
}

export default connect()(Restore);
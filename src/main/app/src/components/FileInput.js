import React, {Component} from 'react';
import {Input} from 'semantic-ui-react';
import PropTypes from 'prop-types';

class FileInput extends Component {
  _fileInput = null;

  constructor(props) {
    super(props);

    this.state = {
      displayedName: FileInput.computeDisplayName(props.fileList)
    }
  }

  static computeDisplayName(fileList) {
    return fileList && fileList.length > 0 ? fileList[0].name : '';
  }


  render() {
    return <span>
      <Input readOnly placeholder={this.props.prompt}
             value={this.state.displayedName}
             onClick={this.handleFileFieldClick}/>
      <input type='file' style={{display: 'none'}}
             onChange={this.handleFileInputChange}
             ref={elem => this._fileInput = elem}/>
    </span>
  }

  componentWillReceiveProps(nextProps) {
    this.setState({
      displayedName: FileInput.computeDisplayName(nextProps.fileList)
    });
  }

  handleFileFieldClick = () => {
    this._fileInput.click();
  };

  handleFileInputChange = (evt) => {
    const files = evt.target.files;

    if (this.props.onChange) {
      this.props.onChange(files)
    }
  };

  static propTypes = {
    prompt: PropTypes.string,
    /**
     * This callback accepts a {@link FileList}
     */
    onChange: PropTypes.func,
    fileList: PropTypes.oneOfType([
      PropTypes.instanceOf(FileList),
      PropTypes.instanceOf(Array)
    ])
  };

  static defaultProps = {
    prompt: 'Click here to choose file'
  };
}

export default FileInput;
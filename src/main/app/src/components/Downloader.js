import React, {Component} from 'react';

class Downloader extends Component {
  _form = null;

  render() {
    return <form method='get'
                 action={this.props.location}
                 ref={(value) => this._form = value}
                 style={{display: 'none'}}></form>
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.download && !this.props.download) {
      this.props.onWillDownload();
      this._form.submit();
      this.props.onDidDownload();
    }
  }
}

export default Downloader;
import React, {Component} from 'react';
import {connect} from 'react-redux';
import {fetchUserProfile} from '../../actions/user';
import {Loader} from 'semantic-ui-react';
import {Redirect} from 'react-router-dom';

class Parents extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    if (this.props.isFetchingUser) {
      return <Loader/>
    }
    else if (!this.props.loggedIn) {
      return <Redirect to="/"/>
    }
    else if (this.props.role !== 'parent') {
      console.warn('Observed a non-parent role', this.props.role);
      return <Redirect to="/"/>
    }
    else {
      return (<div>Hi {this.props.profile.displayName}</div>)
    }

  }

  componentDidMount() {
    this.props.dispatch(fetchUserProfile())
  }
}

function mapStateToProps(state) {

  const {user} = state;

  return {
    isFetchingUser: user.isFetching,
    profile: user.profile,
    loggedIn: user.loggedIn,
    role: user.role
  }
}

export default connect(mapStateToProps)(Parents);
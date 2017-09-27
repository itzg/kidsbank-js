import React, {Component} from 'react';
import './App.css';
import LoginLogout from './components/LoginLogout';
import Welcome from './scenes/Welcome';

class App extends Component {
  constructor(props) {
    super(props);

    this.state = {
      currentUser: null,
      loading: true
    };
  }

  render() {
    return (
      <div className="App">
        <Welcome/>
        <LoginLogout currentUser={this.state.currentUser} loading={this.state.loading}/>
      </div>
    );
  }

  componentDidMount() {
    fetch('/api/currentUser', {credentials: 'same-origin'}).then((resp) => {
      if (resp.ok) {
        if (resp.status === 200) {
          resp.json().then((data) => {
            console.log('currentUser is', data);
            this.setState({currentUser: data, loading: false});
          });
        }
        else {
          console.log('currentUser is not yet available');
          this.setState({loading: false})
        }
      }
    })
  }
}

export default App;

import React, {Component} from 'react';
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom';
import './App.css';
import Welcome from './scenes/Welcome';
import Parents from './scenes/Parents';

class App extends Component {

  render() {

    return (
      <Router>
        <div className="App">
          <Switch>
            <Route path="/parent" component={Parents}/>
            <Route component={Welcome}/>
          </Switch>
        </div>
      </Router>
    );
  }
}

export default App;

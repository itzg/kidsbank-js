import React, {Component} from 'react';
import {Route, Switch} from 'react-router-dom';
import './App.css';
import Notifications from './components/Notifications';
import Welcome from './scenes/Welcome';
import Parents from './scenes/Parents';
import Kids from './scenes/Kids';
import Footer from './Footer';

class App extends Component {

  render() {

    return (
      <div className="App">
        <Notifications/>
        <main>
          <Switch>
            <Route path="/parent" component={Parents}/>
            <Route path="/kid" component={Kids}/>
            <Route component={Welcome}/>
          </Switch>
        </main>
        <Footer/>
      </div>
    );
  }

}

export default App;

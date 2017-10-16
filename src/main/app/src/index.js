import React from 'react';
import ReactDOM from 'react-dom';
import {Provider} from 'react-redux';
import {applyMiddleware, createStore} from 'redux';
import thunkMiddleware from 'redux-thunk';
import {createLogger} from 'redux-logger';
import {BrowserRouter as Router} from 'react-router-dom';

import rootReducer from './reducers';

import 'semantic-ui-css/semantic.min.css';
import './index.css';
import App from './App';
import registerServiceWorker from './registerServiceWorker';
import sessionTimeoutMiddleware from './actions/sessionTimeoutMiddleware';

const loggerMiddleware = createLogger();

const store = createStore(
  rootReducer,
  applyMiddleware(
    sessionTimeoutMiddleware,
    thunkMiddleware,
    loggerMiddleware
  )
);


ReactDOM.render(
  <Provider store={store}>
    <Router>
      <App/>
    </Router>
  </Provider>,
  document.getElementById('root'));
registerServiceWorker();

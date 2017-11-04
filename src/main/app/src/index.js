import React from 'react';
import ReactDOM from 'react-dom';
import {Provider} from 'react-redux';
import {applyMiddleware, compose, createStore} from 'redux';
import thunkMiddleware from 'redux-thunk';
import {createLogger} from 'redux-logger';
import {BrowserRouter as Router} from 'react-router-dom';
import persistState from 'redux-localstorage';

import rootReducer from './reducers';

import 'semantic-ui-css/semantic.min.css';
import './index.css';
import App from './App';
import restApiMiddleware from './actions/restApiMiddleware';

const loggerMiddleware = createLogger();

const store = createStore(
  rootReducer,
  compose(
    applyMiddleware(
      restApiMiddleware,
      thunkMiddleware,
      loggerMiddleware
    ),
    persistState('persisted', {
      key: 'kidsbank'
    })
  )
);


ReactDOM.render(
  <Provider store={store}>
    <Router>
      <App/>
    </Router>
  </Provider>,
  document.getElementById('root'));

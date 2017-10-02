import {combineReducers} from 'redux';
import user from './user';
import accounts from './accounts';
import {reducer as formReducer} from 'redux-form';

const rootReducer = combineReducers({
  user,
  accounts,
  form: formReducer
});

export default rootReducer;
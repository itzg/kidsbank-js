import {combineReducers} from 'redux';
import user from './user';
import accounts from './accounts';
import transactions from './transactions';
import {reducer as formReducer} from 'redux-form';

const rootReducer = combineReducers({
  user,
  accounts,
  transactions,
  form: formReducer
});

export default rootReducer;
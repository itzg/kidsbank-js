import {combineReducers} from 'redux';
import user from './user';
import accounts from './accounts';
import transactions from './transactions';
import session from './session';
import {reducer as formReducer} from 'redux-form';

const rootReducer = combineReducers({
  user,
  accounts,
  transactions,
  session,
  form: formReducer
});

export default rootReducer;
import {combineReducers} from 'redux';
import user from './user';
import accounts from './accounts';
import transactions from './transactions';
import session from './session';
import notifications from './notifications';
import scheduled from './scheduled';
import {reducer as formReducer} from 'redux-form';

const rootReducer = combineReducers({
  user,
  accounts,
  transactions,
  session,
  notifications,
  scheduled,
  form: formReducer
});

export default rootReducer;
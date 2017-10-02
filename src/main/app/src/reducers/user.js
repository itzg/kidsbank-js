import {RECEIVE_USER_PROFILE, REQUEST_USER_PROFILE} from "../actions/user";
import _ from 'lodash';

export default function user(state = {
                               isFetching: true,
                               role: null,
                               profile: null,
                               loggedIn: false
                             },
                             action) {
  switch (action.type) {
    case REQUEST_USER_PROFILE:
      return Object.assign({}, state, {
        isFetching: true
      });

    case RECEIVE_USER_PROFILE:
      return Object.assign({}, state, {
        isFetching: false,
        profile: action.profile,
        loggedIn: action.profile && !_.isEmpty(action.profile.role),
        role: action.profile && _.toLower(action.profile.role)
      });

    default:
      return state;
  }
}

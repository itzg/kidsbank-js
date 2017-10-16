import {SESSION_TIMEOUT} from "../actions/session";
import {RECEIVE_USER_PROFILE} from "../actions/user";

export default function session(state = {
                                  timeout: false
                                },
                                action) {
  switch (action.type) {
    case SESSION_TIMEOUT:
      return {
        timeout: true
      };

    case RECEIVE_USER_PROFILE:
      return {
        timeout: false
      };

    default:
      return state;
  }
}

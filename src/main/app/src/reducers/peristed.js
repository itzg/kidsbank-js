import {DISMISS_INTRO} from "../actions/persisted";
import {LOGIN_PARENT_START} from "../actions/user";

export default function persisted(state = {
                                    introDismissed: false
                                  },
                                  action) {
  switch (action.type) {
    case DISMISS_INTRO:
      return {
        ...state,
        introDismissed: true
      };

    case LOGIN_PARENT_START:
      return {
        ...state,
        lastUsedProvider: action.provider
      };

    default:
      return state;
  }
}
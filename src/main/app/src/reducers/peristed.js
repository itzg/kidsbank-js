import {DISMISS_INTRO} from "../actions/persisted";

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

    default:
      return state;
  }
}
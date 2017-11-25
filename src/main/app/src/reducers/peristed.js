import {DISMISS_INSTRUCTION, DISMISS_INTRO} from "../actions/persisted";
import {LOGIN_PARENT_START} from "../actions/user";

export default function persisted(state = {
                                    introDismissed: false,
                                    instructionsDismissed: []
                                  },
                                  action) {
  switch (action.type) {
    case DISMISS_INTRO:
      return {
        ...state,
        introDismissed: true
      };

    case DISMISS_INSTRUCTION:
      return {
        ...state,
        instructionsDismissed: [...(state.instructionsDismissed || []), action.id]
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
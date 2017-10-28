import {LOAD_SCHEDULED_START, LOAD_SCHEDULED_SUCCESS} from "../actions/scheduled";

export default function scheduled(state = {
                                    byAccount: {},
                                    loadingByAccount: new Set()
                                  },
                                  action) {
  switch (action.type) {
    case LOAD_SCHEDULED_START: {
      let loadingByAccount = new Set(state.loadingByAccount);
      loadingByAccount.add(action.accountId);
      return {
        ...state,
        loadingByAccount
      };
    }

    case LOAD_SCHEDULED_SUCCESS: {
      let loadingByAccount = new Set(state.loadingByAccount);
      loadingByAccount.delete(action.accountId);
      return {
        ...state,
        byAccount: {
          ...state.byAccount,
          [action.accountId]: action.scheduled
        },
        loadingByAccount
      };
    }

    default:
      return state;
  }
}
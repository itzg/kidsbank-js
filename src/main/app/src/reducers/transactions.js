import {LOAD_TRANSACTIONS_START, LOAD_TRANSACTIONS_SUCCESS} from "../actions/transactions";

export default function transactions(state = {
                                       byAccount: {}
                                     },
                                     action) {
  switch (action.type) {
    case LOAD_TRANSACTIONS_START:
      return {
        byAccount: {
          ...state.byAccount,

          [action.accountId]: {
            ...state.byAccount[action.accountId],
            loading: true
          }
        }
      };

    case LOAD_TRANSACTIONS_SUCCESS:
      return {
        byAccount: {
          ...state.byAccount,

          [action.accountId]: {
            ...state.byAccount[action.accountId],
            page: action.page,
            loaded: true,
            loading: false
          }
        }
      };

    default:
      return state;
  }
}
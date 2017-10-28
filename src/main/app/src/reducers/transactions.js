import {
  DESELECT_TRANSACTION,
  LOAD_TRANSACTIONS_START,
  LOAD_TRANSACTIONS_SUCCESS,
  SELECT_TRANSACTION
} from "../actions/transactions";

export default function transactions(state = {
                                       byAccount: {},
                                       selected: null
                                     },
                                     action) {
  switch (action.type) {
    case LOAD_TRANSACTIONS_START:
      return {
        ...state,
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
        ...state,
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

    case SELECT_TRANSACTION:
      return {
        ...state,
        selected: action.transaction
      };

    case DESELECT_TRANSACTION:
      return {
        ...state,
        selected: null
      };

    default:
      return state;
  }
}
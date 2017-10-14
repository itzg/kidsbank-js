import {
  CREATE_ACCOUNT_SUCCESS,
  FETCH_ACCOUNTS_START,
  FETCH_ACCOUNTS_SUCCESS,
  FETCH_SINGLE_ACCOUNT_START,
  FETCH_SINGLE_ACCOUNT_SUCCESS
} from "../actions/accounts";

export default function accounts(state = {
                                   isFetching: false,
                                   list: [],
                                   isCreating: false
                                 },
                                 action) {
  switch (action.type) {
    case FETCH_ACCOUNTS_START:
      return Object.assign({}, state, {
        isFetching: true
      });

    case FETCH_ACCOUNTS_SUCCESS:
      let newState = Object.assign({}, state, {
        isFetching: false,
        isPartial: false,
        byId: {}
      });
      if (action.accounts) {
        let account;
        for (account of action.accounts) {
          newState.byId[account.id] = account;
        }
      }
      return newState;

    case FETCH_SINGLE_ACCOUNT_START:
      return {
        isFetching: true,
        byId: {...state.byId}
      };

    case FETCH_SINGLE_ACCOUNT_SUCCESS:
      return {
        isFetching: false,
        isPartial: true,
        byId: {
          ...state.byId,
          [action.account.id]: action.account
        }
      };

    case CREATE_ACCOUNT_SUCCESS:
      return Object.assign({}, state, {
        list: state.list.concat([action.account])
      });

    default:
      return state;
  }
}
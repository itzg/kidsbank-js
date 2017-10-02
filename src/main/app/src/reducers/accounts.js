import {CREATE_ACCOUNT_SUCCESS, FETCH_ACCOUNTS_START, FETCH_ACCOUNTS_SUCCESS} from "../actions/accounts";

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
      return Object.assign({}, state, {
        isFetching: false,
        list: action.accounts
      });

    case CREATE_ACCOUNT_SUCCESS:
      return Object.assign({}, state, {
        list: state.list.concat([action.account])
      });

    default:
      return state;
  }
}
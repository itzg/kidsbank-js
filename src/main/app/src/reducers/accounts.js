import {
  CREATE_ACCOUNT_SUCCESS,
  FETCH_ACCOUNT_BALANCE_START,
  FETCH_ACCOUNT_BALANCE_SUCCESS,
  FETCH_ACCOUNTS_START,
  FETCH_ACCOUNTS_SUCCESS,
  FETCH_PRIMARY_ACCOUNT_BALANCE_START,
  FETCH_PRIMARY_ACCOUNT_BALANCE_SUCCESS,
  FETCH_SINGLE_ACCOUNT_START,
  FETCH_SINGLE_ACCOUNT_SUCCESS
} from "../actions/accounts";

export default function accounts(state = {
                                   loading: false,
                                   loaded: false,
                                   list: [],
                                   byId: {},
                                   isCreating: false,
                                   balances: {}, // [accountId] : { fetching, balance}
                                   primary: {
                                     balance: {}
                                   }
                                 },
                                 action) {
  switch (action.type) {
    case FETCH_ACCOUNTS_START:
      return {
        ...state,
        loading: true
      };

    case FETCH_ACCOUNTS_SUCCESS:
      let newState = {
        ...state,
        loading: false,
        loaded: true,
        isPartial: false,
        byId: {},
      };

      if (action.accounts) {
        let account;
        for (account of action.accounts) {
          newState.byId[account.id] = account;
        }
      }
      return newState;

    case FETCH_SINGLE_ACCOUNT_START:
      return {
        ...state,
        loading: true,
        byId: {...state.byId}
      };

    case FETCH_SINGLE_ACCOUNT_SUCCESS:
      return {
        ...state,
        loading: false,
        loaded: true,
        isPartial: true,
        byId: {
          ...state.byId,
          [action.account.id]: action.account
        }
      };

    case CREATE_ACCOUNT_SUCCESS:
      return {
        ...state,
        byId: {
          ...state.byId,
          [action.account.id]: action.account
        }
      };

    case FETCH_ACCOUNT_BALANCE_START:
      return {
        ...state,
        balances: {
          ...state.balances,
          [action.accountId]: {
            fetching: true
          }
        }
      };

    case FETCH_ACCOUNT_BALANCE_SUCCESS:
      return {
        ...state,
        balances: {
          ...state.balances,
          [action.accountId]: {
            fetching: false,
            balance: action.balance
          }
        }
      };

    case FETCH_PRIMARY_ACCOUNT_BALANCE_START:
      return {
        ...state,
        primary: {
          ...state.primary,
          balance: {
            fetching: true
          }
        }
      };

    case FETCH_PRIMARY_ACCOUNT_BALANCE_SUCCESS:
      return {
        ...state,
        primary: {
          ...state.primary,
          balance: {
            fetching: false,
            balance: action.balance
          }
        }
      };


    default:
      return state;
  }
}
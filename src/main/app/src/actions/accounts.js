import {getJson, postJson} from './RestApi';

export const FETCH_ACCOUNTS_START = 'FETCH_ACCOUNTS_START';

function fetchAccountsStart() {
  return {
    type: FETCH_ACCOUNTS_START
  }
}

export const FETCH_ACCOUNTS_SUCCESS = 'FETCH_ACCOUNTS_SUCCESS';

function fetchAccountsSuccess(accounts) {
  return {
    type: FETCH_ACCOUNTS_SUCCESS,
    accounts
  }
}

export function fetchParentManagedAccounts() {
  return (dispatch) => {
    dispatch(fetchAccountsStart());

    getJson('/api/parent/accounts')
      .then(json => {
        console.log('success', json);
        dispatch(fetchAccountsSuccess(json))
      }, err => {
        console.error('failed', err);
      })
  }
}

export const CREATE_ACCOUNT_START = 'CREATE_ACCOUNT_START';

function createAccountStart(fields) {
  return {
    type: CREATE_ACCOUNT_START,
    fields: fields
  }
}

export const CREATE_ACCOUNT_SUCCESS = 'CREATE_ACCOUNT_SUCCESS';

function createAccountSuccess(account) {
  return {
    type: CREATE_ACCOUNT_SUCCESS,
    account
  }
}

export function createAccount(fields) {
  return (dispatch) => {
    dispatch(createAccountStart(fields));

    return postJson('/api/parent/accounts', fields)
      .then(json => {

        return dispatch(createAccountSuccess(json));

      }, err => {
        console.error('failed', err);
        return Promise.reject(err);
      })
  }
}

export const SHARE_ACCOUNT_START = 'SHARE_ACCOUNT_START';

function shareAccountStart(account) {
  return {
    type: SHARE_ACCOUNT_START,
    account
  }
}

export function shareAccount(account) {
  return (dispatch) => {
    dispatch(shareAccountStart(account));

    return postJson(`/api/parent/accounts/${account.id}/_share`)
      .then(json => {
        return json.value;
      })
  }
}
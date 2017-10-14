import {getJson, postJson} from './RestApi';
import {adaptValidationToSubmissionError} from "./Forms";

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
  return (dispatch, getState) => {
    const {accounts} = getState();

    if (!accounts.isPartial && accounts.byId) {
      return;
    }

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

export const FETCH_SINGLE_ACCOUNT_START = 'FETCH_SINGLE_ACCOUNT_START';

export function fetchSingleAccountStart(accountId) {
  return {
    type: FETCH_SINGLE_ACCOUNT_START,
    accountId
  }
}

export const FETCH_SINGLE_ACCOUNT_SUCCESS = 'FETCH_SINGLE_ACCOUNT_SUCCESS';

export function fetchSingleAccountSuccess(account) {
  return {
    type: FETCH_SINGLE_ACCOUNT_SUCCESS,
    account
  }
}

export function fetchSingleAccount(accountId) {
  return (dispatch, getState) => {
    const {accounts} = getState();

    if (accounts.byId && accounts.byId[accountId]) {
      return;
    }

    dispatch(fetchSingleAccountStart(accountId));

    getJson(`/api/parent/accounts/${accountId}`)
      .then(json => {
        console.log('success', json);
        dispatch(fetchSingleAccountSuccess(json))
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

        },
        adaptValidationToSubmissionError
      )
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
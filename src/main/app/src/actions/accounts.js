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

    if (!accounts.isPartial && accounts.loaded) {
      return;
    }

    dispatch(fetchAccountsStart());

    return getJson('/api/parent/accounts')
      .then(json => {
        dispatch(fetchAccountsSuccess(json))
      })
  }
}

export const FETCH_PRIMARY_ACCOUNT_BALANCE_START = 'FETCH_PRIMARY_ACCOUNT_BALANCE_START';

export const FETCH_PRIMARY_ACCOUNT_BALANCE_SUCCESS = 'FETCH_PRIMARY_ACCOUNT_BALANCE_SUCCESS';

function fetchPrimaryAccountBalanceStart() {
  return {
    type: FETCH_PRIMARY_ACCOUNT_BALANCE_START
  }
}

function fetchPrimaryAccountBalanceSuccess(balance) {
  return {
    type: FETCH_PRIMARY_ACCOUNT_BALANCE_SUCCESS,
    balance
  }
}

export function fetchKidPrimaryAccountBalance() {
  return (dispatch) => {
    dispatch(fetchPrimaryAccountBalanceStart());

    return getJson(`/api/kid/primary-account/balance`)
      .then(json => {
        dispatch(fetchPrimaryAccountBalanceSuccess(json.value));
      })
  }
}

export const FETCH_PRIMARY_ACCOUNT_SUMMARY_START = 'FETCH_PRIMARY_ACCOUNT_SUMMARY_START';
export const FETCH_PRIMARY_ACCOUNT_SUMMARY_SUCCESS = 'FETCH_PRIMARY_ACCOUNT_SUMMARY_SUCCESS';

export function fetchPrimaryAccountSummary() {
  return (dispatch) => {
    dispatch(fetchPrimaryAccountSummaryStart());

    return getJson(`/api/kid/primary-account`)
      .then(json => {
        dispatch(fetchPrimaryAccountSummarySuccess(json));
      })
  }
}

function fetchPrimaryAccountSummaryStart() {
  return {type: FETCH_PRIMARY_ACCOUNT_SUMMARY_START};
}

function fetchPrimaryAccountSummarySuccess(summary) {
  return {
    type: FETCH_PRIMARY_ACCOUNT_SUMMARY_SUCCESS,
    summary
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
export const FETCH_SINGLE_ACCOUNT_SKIPPED = 'FETCH_SINGLE_ACCOUNT_SKIPPED';

export function fetchSingleAccountSuccess(account) {
  return {
    type: FETCH_SINGLE_ACCOUNT_SUCCESS,
    account
  }
}

function fetchSingleAccountSkip(accountId) {
  return {
    type: FETCH_SINGLE_ACCOUNT_SKIPPED,
    accountId
  }
}

export function fetchSingleAccount(accountId) {
  return (dispatch, getState) => {
    const {accounts} = getState();

    if (accounts.byId && accounts.byId[accountId]) {
      dispatch(fetchSingleAccountSkip(accountId));
      return Promise.resolve();
    }

    dispatch(fetchSingleAccountStart(accountId));

    return getJson(`/api/parent/accounts/${accountId}`)
      .then(json => {
        dispatch(fetchSingleAccountSuccess(json))
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

export const FETCH_ACCOUNT_BALANCE_START = 'FETCH_ACCOUNT_BALANCE_START';

function fetchAccountBalanceStart(accountId) {
  return {
    type: FETCH_ACCOUNT_BALANCE_START,
    accountId
  }
}

export const FETCH_ACCOUNT_BALANCE_SUCCESS = 'FETCH_ACCOUNT_BALANCE_SUCCESS';

function fetchAccountBalanceSuccess(accountId, balance) {
  return {
    type: FETCH_ACCOUNT_BALANCE_SUCCESS,
    accountId,
    balance
  }
}

export const FETCH_ACCOUNT_BALANCE_SKIPPED = 'FETCH_ACCOUNT_BALANCE_SKIPPED';

function fetchAccountBalanceSkipped(accountId) {
  return {
    type: FETCH_ACCOUNT_BALANCE_SKIPPED,
    accountId
  }
}

export function fetchAccountBalance(accountId, force = false) {
  return (dispatch, getState) => {
    const balance = getState().accounts.balances[accountId];
    if (balance && !force) {
      dispatch(fetchAccountBalanceSkipped(accountId));
      return;
    }

    dispatch(fetchAccountBalanceStart(accountId));

    return getJson(`/api/parent/accounts/${accountId}/balance`)
      .then((json) => {
        dispatch(fetchAccountBalanceSuccess(accountId, json.value));
      })
  }
}
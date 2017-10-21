import {getJson, postFile, postJson} from './RestApi';
import {adaptValidationToSubmissionError} from "./Forms";
import {fetchAccountBalance} from './accounts';

export let defaultTransactionsPageSize = 10;

export const CREATE_TRANSACTION = 'CREATE_TRANSACTION';
export const CREATE_TRANSACTION_START = 'CREATE_TRANSACTION_START';
export const CREATE_TRANSACTION_SUCCESS = 'CREATE_TRANSACTION_SUCCESS';

function createTransactionStart(accountId, when, description, amount) {
  return {
    type: CREATE_TRANSACTION_START,
    accountId,
    when,
    description,
    amount
  }
}

function createTransactionSuccess(accountId, transactionId) {
  return {
    type: CREATE_TRANSACTION_SUCCESS,
    accountId,
    transactionId
  }
}

export function createTransaction(accountId, when, description, amount) {
  return (dispatch) => {

    dispatch(createTransactionStart(accountId, when, description, amount));

    return postJson(`/api/parent/accounts/${accountId}/transactions`, {
      when,
      description,
      amount
    })
      .then((json) => {

        dispatch(createTransactionSuccess(accountId, json.id));

        dispatch(reloadInitialTransactions(accountId));

        dispatch(fetchAccountBalance(accountId, true));

        return Promise.resolve();
      }, adaptValidationToSubmissionError);
  }
}

export const LOAD_TRANSACTIONS_START = 'LOAD_TRANSACTIONS_START';
export const LOAD_TRANSACTIONS_SUCCESS = 'LOAD_TRANSACTIONS_SUCCESS';
export const LOAD_TRANSACTIONS_SKIPPED = 'LOAD_TRANSACTIONS_SKIPPED';

export function loadInitialTransactions(accountId) {
  return (dispatch, getState) => {

    const {transactions} = getState();
    const account = transactions.byAccount[accountId];

    if (account && (account.loaded || account.loading)) {
      dispatch(loadTransactionsSkipped(accountId));
      return;
    }

    dispatch(reloadInitialTransactions(accountId));
  }
}

export function reloadInitialTransactions(accountId) {
  return (dispatch) => {
    return dispatch(loadPageOfTransactions(accountId, 0, defaultTransactionsPageSize));
  }
}

export function loadPageOfTransactions(accountId, pageNumber, pageSize) {
  return (dispatch) => {
    dispatch(loadTransactionsStart(accountId, pageNumber));

    return getJson(`/api/parent/accounts/${accountId}/transactions?page=${pageNumber}&size=${pageSize}`)
      .then(json => {
        dispatch(loadTransactionsSuccess(accountId, json));
      })

  }
}

export function loadOlderTransactions(accountId) {
  return (dispatch, getState) => {
    const {transactions} = getState();
    const account = transactions.byAccount[accountId];

    if (!account) {
      dispatch(loadInitialTransactions(accountId));
    }
    else if (!account.loaded || account.page.last) {
      dispatch(loadTransactionsSkipped(accountId));
      return;
    }

    return dispatch(loadPageOfTransactions(accountId, account.page.number + 1, account.page.size));
  }
}

export function loadNewerTransactions(accountId) {
  return (dispatch, getState) => {
    const {transactions} = getState();
    const account = transactions.byAccount[accountId];

    if (!account) {
      dispatch(loadInitialTransactions(accountId));
    }
    else if (!account.loaded || account.page.first) {
      dispatch(loadTransactionsSkipped(accountId));
      return;
    }

    return dispatch(loadPageOfTransactions(accountId, account.page.number - 1, account.page.size));
  }
}

export function loadTransactionsSkipped(accountId) {
  return {
    type: LOAD_TRANSACTIONS_SKIPPED,
    accountId
  }
}

export function loadTransactionsStart(accountId, pageNumber) {
  return {
    type: LOAD_TRANSACTIONS_START,
    accountId,
    pageNumber
  }
}

export function loadTransactionsSuccess(accountId, page) {
  return {
    type: LOAD_TRANSACTIONS_SUCCESS,
    accountId,
    page
  }
}

const RESTORE_TRANSACTIONS_START = 'RESTORE_TRANSACTIONS_START';
const RESTORE_TRANSACTIONS_FAILED = 'RESTORE_TRANSACTIONS_FAILED';
const RESTORE_TRANSACTIONS_SUCCESS = 'RESTORE_TRANSACTIONS_SUCCESS';

export function restoreTransactions(accountId, fileList) {
  return (dispatch) => {
    if (fileList == null || fileList.length === 0) {
      dispatch(restoreTransactionsFailed(accountId, fileList, 'Empty file list'));
      return Promise.reject();
    }

    dispatch(restoreTransactionsStart(accountId, fileList));

    return postFile(`/api/parent/accounts/${accountId}/_restore`, fileList[0])
      .then(results => {
        dispatch(restoreTransactionsSuccess(accountId, results));

        dispatch(fetchAccountBalance(accountId, true));
        dispatch(reloadInitialTransactions(accountId));

        return Promise.resolve(results);
      })
  }
}

export function restoreTransactionsStart(accountId, fileList) {
  return {
    type: RESTORE_TRANSACTIONS_START,
    accountId,
    fileList
  }
}

export function restoreTransactionsFailed(accountId, fileList, reason) {
  return {
    type: RESTORE_TRANSACTIONS_FAILED,
    accountId,
    fileList,
    reason
  }
}

export function restoreTransactionsSuccess(accountId, results) {
  return {
    type: RESTORE_TRANSACTIONS_SUCCESS,
    accountId,
    results
  }
}
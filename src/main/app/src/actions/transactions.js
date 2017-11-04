import {deleteThenJson, getJson, postFile, postJson, putJson} from './RestApi';
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

      }, adaptValidationToSubmissionError);
  }
}

const SAVE_TRANSACTION_START = 'SAVE_TRANSACTION_START';
const SAVE_TRANSACTION_SUCCESS = 'SAVE_TRANSACTION_SUCCESS';

export function saveTransaction(values) {
  return (dispatch) => {
    dispatch({
      type: SAVE_TRANSACTION_START,
      transaction: values
    });

    let {id, accountId, description, amount, when} = values;

    return putJson(`/api/parent/transactions`, {
      id,
      accountId,
      description,
      amount,
      when
    })
      .then(json => {

        dispatch({
          type: SAVE_TRANSACTION_SUCCESS,
          transaction: json
        });

        dispatch(reloadInitialTransactions(accountId));
        dispatch(fetchAccountBalance(accountId, true));
      }, adaptValidationToSubmissionError)
  }
}

const DELETE_TRANSACTION_START = 'DELETE_TRANSACTION_START';
const DELETE_TRANSACTION_SUCCESS = 'DELETE_TRANSACTION_SUCCESS';

export function deleteTransaction(transactionId, accountId) {
  return (dispatch) => {
    dispatch({
      type: DELETE_TRANSACTION_START,
      transactionId
    });

    return deleteThenJson(`/api/parent/transactions/${transactionId}`)
      .then(json => {
        dispatch({
          type: DELETE_TRANSACTION_SUCCESS,
          transactionId
        });

        dispatch(reloadInitialTransactions(accountId));
        dispatch(fetchAccountBalance(accountId, true));
      })
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
  return (dispatch, getState) => {
    dispatch(loadTransactionsStart(accountId, pageNumber));

    return getJson(`/api/${getState().user.role}/accounts/${accountId}/transactions?page=${pageNumber}&size=${pageSize}`)
      .then(json => {
        dispatch(deselectTransaction());
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

const IMPORT_TRANSACTIONS_START = 'IMPORT_TRANSACTIONS_START';
const IMPORT_TRANSACTIONS_FAILED = 'IMPORT_TRANSACTIONS_FAILED';
const IMPORT_TRANSACTIONS_SUCCESS = 'IMPORT_TRANSACTIONS_SUCCESS';

export function importTransactions(accountId, fileList) {
  return (dispatch) => {
    if (fileList == null || fileList.length === 0) {
      dispatch(importTransactionsFailed(accountId, fileList, 'Empty file list'));
      return Promise.reject();
    }

    dispatch(importTransactionsStart(accountId, fileList));

    return postFile(`/api/parent/accounts/${accountId}/_import`, fileList[0])
      .then(results => {
        dispatch(importTransactionsSuccess(accountId, results));

        dispatch(fetchAccountBalance(accountId, true));
        dispatch(reloadInitialTransactions(accountId));
        dispatch(deselectTransaction());

        return Promise.resolve(results);
      })
  }
}

export function importTransactionsStart(accountId, fileList) {
  return {
    type: IMPORT_TRANSACTIONS_START,
    accountId,
    fileList
  }
}

export function importTransactionsFailed(accountId, fileList, reason) {
  return {
    type: IMPORT_TRANSACTIONS_FAILED,
    accountId,
    fileList,
    reason
  }
}

export function importTransactionsSuccess(accountId, results) {
  return {
    type: IMPORT_TRANSACTIONS_SUCCESS,
    accountId,
    results
  }
}

export const SELECT_TRANSACTION = 'SELECT_TRANSACTION';
export const DESELECT_TRANSACTION = 'DESELECT_TRANSACTION';

export function selectTransaction(transaction) {
  return {
    type: SELECT_TRANSACTION,
    transaction
  }
}

export function deselectTransaction() {
  return {
    type: DESELECT_TRANSACTION
  }
}
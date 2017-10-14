import {getJson, postJson} from './RestApi';
import {adaptValidationToSubmissionError} from "./Forms";

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
    if (transactions.byAccount[accountId] && (
        transactions.byAccount[accountId].loaded ||
        transactions.byAccount[accountId].loading)
    ) {
      dispatch(loadTransactionsSkipped(accountId));
      return;
    }

    dispatch(reloadInitialTransactions(accountId));
  }
}

export function reloadInitialTransactions(accountId) {
  return (dispatch) => {
    dispatch(loadTransactionsStart(accountId, 0));

    return getJson(`/api/parent/accounts/${accountId}/transactions`)
      .then(json => {
        dispatch(loadTransactionsSuccess(accountId, json));
      })
  }
}

export function loadTransactionsSkipped(accountId) {
  return {
    type: LOAD_TRANSACTIONS_SKIPPED,
    accountId
  }
}

export function loadTransactionsStart(accountId, offset) {
  return {
    type: LOAD_TRANSACTIONS_START,
    accountId,
    offset
  }
}

export function loadTransactionsSuccess(accountId, page) {
  return {
    type: LOAD_TRANSACTIONS_SUCCESS,
    accountId,
    page
  }
}
import {deleteThenJson, getJson, postJson} from './RestApi';
import {adaptValidationToSubmissionError} from "./Forms";

export const LOAD_SCHEDULED_START = 'LOAD_SCHEDULED_START';
export const LOAD_SCHEDULED_SUCCESS = 'LOAD_SCHEDULED_SUCCESS';

export function loadScheduled(accountId) {
  return (dispatch) => {
    dispatch(loadScheduledStart(accountId));

    return getJson(`/api/parent/accounts/${accountId}/scheduled`)
      .then(json => {
        dispatch(loadScheduledSuccess(accountId, json));
      })
  }
}

function loadScheduledStart(accountId) {
  return {
    type: LOAD_SCHEDULED_START,
    accountId
  }
}

function loadScheduledSuccess(accountId, scheduled) {
  return {
    type: LOAD_SCHEDULED_SUCCESS,
    accountId,
    scheduled
  }
}

export const CREATE_SCHEDULED_START = 'CREATE_SCHEDULED_START';
export const CREATE_SCHEDULED_SUCCESS = 'CREATE_SCHEDULED_SUCCESS';

export function createScheduled(accountId, values) {
  return (dispatch) => {
    dispatch(createScheduledStart(accountId, values));

    return postJson(`/api/parent/accounts/${accountId}/scheduled`, values)
      .then(json => {
        dispatch(createScheduledSuccess(accountId, json));

        dispatch(loadScheduled(accountId));

        return json;
      }, adaptValidationToSubmissionError)
  }
}

function createScheduledStart(accountId, values) {
  return {
    type: CREATE_SCHEDULED_START,
    accountId,
    values
  }
}

function createScheduledSuccess(accountId, scheduled) {
  return {
    type: CREATE_SCHEDULED_SUCCESS,
    accountId,
    scheduled
  }
}

export const DELETE_SCHEDULED_START = 'DELETE_SCHEDULED_START';
export const DELETE_SCHEDULED_SUCCESS = 'DELETE_SCHEDULED_SUCCESS';

export function deleteScheduled(accountId, scheduled) {
  return (dispatch) => {
    dispatch(deleteScheduledStart(accountId, scheduled.id));

    return deleteThenJson(`/api/parent/scheduled/${scheduled.id}`)
      .then(json => {
        dispatch(deleteScheduleSuccess(accountId, scheduled.id));
        dispatch(loadScheduled(accountId));
        return json;
      })
  }
}

function deleteScheduledStart(accountId, scheduledId) {
  return {
    type: DELETE_SCHEDULED_START,
    accountId,
    scheduledId
  }
}

function deleteScheduleSuccess(accountId, scheduledId) {
  return {
    type: DELETE_SCHEDULED_SUCCESS,
    accountId,
    scheduledId
  }
}
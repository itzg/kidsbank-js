import {get, postJson} from './RestApi';
import {SubmissionError} from 'redux-form';

export const REQUEST_USER_PROFILE = 'FETCH_USER_PROFILE_REQUEST';

export function requestUserProfile() {
  return {
    type: REQUEST_USER_PROFILE
  }
}

export function fetchUserProfile() {
  return (dispatch, getState) => {

    if (getState().user.loggedIn) {
      return Promise.resolve();
    }

    dispatch(requestUserProfile());

    return get('/api/currentUser').then((resp) => {
      if (resp.ok) {
        if (resp.status === 200) {
          return resp.json().then((data) => {
            dispatch(receiveUserProfile(data));
            return Promise.resolve();
          });
        }
        else {
          dispatch(receiveUserProfile(null));
          return Promise.resolve();
        }
      }
      return Promise.reject();
    })

  };
}

export const RECEIVE_USER_PROFILE = 'RECEIVE_USER_PROFILE';

export function receiveUserProfile(profile) {
  return {
    type: RECEIVE_USER_PROFILE,
    profile
  }
}

export const LOGIN_PARENT_START = 'LOGIN_PARENT_START';

export function loginParent(provider) {
  return (dispatch) => {
    dispatch(loginParentStart(provider));

    window.location = `/signin/${provider}`;
  }
}

function loginParentStart(provider) {
  return {
    type: LOGIN_PARENT_START,
    provider
  }
}

function handleLoginOrRegisterFailed(err) {
  if (err.status === 401) {
    let reason = err.message;
    let reasonType = err.error;

    if (reasonType === 'BadCredentialFieldException') {
      const payload = Object.assign({}, ...err.errors);
      return Promise.reject(new SubmissionError(payload));
    } else {
      return Promise.reject(new SubmissionError({_error: reason}));
    }
  }
  else {
    return Promise.reject(err);
  }
}

export function loginKid(username, password) {
  return (dispatch) => {
    return postJson('/kid-login', {
      username,
      password
    })
      .then(
        (json) => {
          dispatch(fetchUserProfile());
          return Promise.resolve();
        },
        handleLoginOrRegisterFailed
      )
  }
}

export function registerKid(username, password, kidlinkCode) {
  return (dispatch) => {
    return postJson('/kid-register', {
      username,
      password,
      kidlinkCode
    })
      .then(
        () => {
          dispatch(fetchUserProfile());
          return Promise.resolve();
        },
        handleLoginOrRegisterFailed
      )
  }
}

export function logoutUser() {
  return () => {
    window.location = '/signout';
  }
}

export const SESSION_TIMEOUT = 'SESSION_TIMEOUT';

export function markSessionTimeout() {
  return {
    type: SESSION_TIMEOUT
  }
}

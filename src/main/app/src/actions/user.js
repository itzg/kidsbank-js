import {get} from './RestApi';

export const REQUEST_USER_PROFILE = 'FETCH_USER_PROFILE_REQUEST';

export function requestUserProfile() {
  return {
    type: REQUEST_USER_PROFILE
  }
}

export function fetchUserProfile() {
  return (dispatch, getState) => {

    if (getState().user.loggedIn) {
      return;
    }

    dispatch(requestUserProfile());

    return get('/api/currentUser').then((resp) => {
      if (resp.ok) {
        if (resp.status === 200) {
          resp.json().then((data) => {
            console.log('currentUser is', data);
            dispatch(receiveUserProfile(data));
          });
        }
        else {
          dispatch(receiveUserProfile(null));
        }
      }
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

export const LOGIN_PARENT = 'LOGIN_PARENT';

export function loginParent() {
  return () => {
    window.location = '/signin/facebook';
  }
}

export const LOGIN_KID = 'LOGIN_KID';

export function loginKid(username, password) {
  return (dispatch) => {
    //TODO
  }
}

export const REGISTER_KID = 'REGISTER_KID';

export function registerKid(username, password, kidlinkCode) {
  return (dispatch) => {
    //TODO
  }
}

export const LOGOUT_USER = 'LOGOUT_USER';

export function logoutUser() {
  return () => {
    window.location = '/signout';
  }
}
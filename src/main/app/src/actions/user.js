export const REQUEST_USER_PROFILE = 'FETCH_USER_PROFILE_REQUEST';

export function requestUserProfile() {
  return {
    type: REQUEST_USER_PROFILE
  }
}

export function fetchUserProfile() {
  return function (dispatch, getState) {

    if (getState().user.loggedIn) {
      return;
    }

    dispatch(requestUserProfile());

    return fetch('/api/currentUser', {credentials: 'same-origin'}).then((resp) => {
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

export const LOGIN_USER = 'LOGIN_USER';

export function loginUser() {
  return () => {
    window.location = '/signin/facebook';
  }
}

export const LOGOUT_USER = 'LOGOUT_USER';

export function logoutUser() {
  return () => {
    window.location = '/signout';
  }
}
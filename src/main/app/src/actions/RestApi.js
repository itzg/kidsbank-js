import BackendError from '../types/BackendError';

/**
 * Used as the <code>.then</code> handler of the fetch.
 *
 * @param response
 * @returns {Promise<Object>} that is resolved when the HTTP response is ok (2xx) or rejected with
 *   a {@link BackendError}.
 */
function handleResponse(response) {
  if (!response.ok) {
    return response.json().then(content => {
      return Promise.reject(new BackendError(content, response));
    })
  }
  else {
    if (response.headers.get('Content-Length') === '0' || !response.headers.has('Content-Type')) {
      return Promise.resolve('');
    }

    return response.json();
  }
}

/**
 * Performs a fetch-get with the appropriate credentials enabled. It also pre-fetches the JSON
 * body of the response and resolves/rejects the promise with that content.
 *
 * @param path
 * @returns {Promise<Object>} that is resolved when the HTTP response is ok (2xx) or rejected with
 *   a {@link BackendError}.
 */
export function getJson(path) {
  return fetch(path, {credentials: 'same-origin'}).then(handleResponse);
}

/**
 * Performs a fetch-get with the appropriate credentials enabled.
 * @param path
 * @returns {Promise<Response>} the promise returned by fetch
 */
export function get(path) {
  return fetch(path, {credentials: 'same-origin'});
}

/**
 * Performs a fetch-post taking the given body and converting it into a JSON payload of the POST.
 * @param path
 * @param body
 * @returns {Promise<Object>} a promise resolved with the JSON content of the response or rejected
 * with a {@link BackendError}.
 */
export function postJson(path, obj) {
  const body = JSON.stringify(obj);

  const headers = new Headers();
  headers.append('Content-Type', 'application/json');

  return fetch(path, {
    method: 'POST',
    headers,
    body,
    credentials: 'same-origin'
  }).then(handleResponse);
}

/**
 * Posts a multi-part form content to the given path.
 * @param path the backend API path
 * @param file {File} a Web API {@link File} object, such as from a {@link FileList}. This will be posted as 'file' in the form data.
 */
export function postFile(path, file) {
  const headers = new Headers();
  // headers.append('Content-Type', 'multipart/form-data');

  let formData = new FormData();
  formData.append('file', file);

  return fetch(path, {
    method: 'POST',
    headers,
    body: formData,
    credentials: 'same-origin'
  }).then(handleResponse);
}
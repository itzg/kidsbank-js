/**
 * Performs a fetch-get with the appropriate credentials enabled. It also pre-fetches the JSON
 * body of the response and resolves/rejects the promise with that content.
 *
 * @param path
 * @returns {Promise<Object>} that is resolved when the HTTP response is ok (2xx) or rejected with
 *   the Spring Boot error response.
 */
export function getJson(path) {
  return fetch(path, {credentials: 'same-origin'}).then(
    (response) => {
      if (!response.ok) {
        return response.json().then(content => {
          return Promise.reject(content);
        })
      }
      else {
        return response.json();
      }
    }
  )
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
 * with a Spring Boot error response.
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
  }).then(
    (response) => {
      if (!response.ok) {
        return response.json().then(content => {
          return Promise.reject(content);
        })
      }
      else {
        return response.json();
      }
    }
  )
}
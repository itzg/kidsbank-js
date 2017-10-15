/**
 * Encapsulates a fetch {@link Response} and a Spring Boot JSON error response body.
 */
export default class BackendError {
  /**
   *
   * @param payload the JSON payload of the Spring Boot error
   * @param response the fetch Response object
   */
  constructor(payload, response) {
    this.payload = payload;
    this.response = response;
  }

  get status() {
    return this.payload.status;
  }

  get message() {
    return this.payload.message;
  }

  get error() {
    return this.payload.error;
  }

  get errors() {
    return this.payload.errors;
  }

  getHeader(name) {
    return this.response.headers.get(name);
  }

}
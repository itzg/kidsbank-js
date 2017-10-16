export const SESSION_TIMEOUT = 'SESSION_TIMEOUT';

export function markSessionTimeout() {
  return {
    type: SESSION_TIMEOUT
  }
}

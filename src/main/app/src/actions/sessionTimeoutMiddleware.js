import BackendError from "../types/BackendError";
import {markSessionTimeout} from "./session";

export default ({dispatch}) => next => action => {
  let returnValue = next(action);

  if (returnValue instanceof Promise) {
    returnValue =
      returnValue
        .then(
          value => Promise.resolve(value),
          badValue => {
            if (badValue instanceof BackendError) {
              if (badValue.status === 403) {
                dispatch(markSessionTimeout());
              }
            }
            return Promise.reject(badValue);
          }
        )
  }

  return returnValue;
}



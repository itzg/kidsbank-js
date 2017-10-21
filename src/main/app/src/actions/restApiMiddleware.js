import BackendError from "../types/BackendError";
import {markSessionTimeout} from "./session";
import {createNotification} from "./notifications";

export default ({dispatch}) => next => action => {
  let returnValue = next(action);

  if (returnValue instanceof Promise) {
    returnValue =
      returnValue
        .then(
          value => Promise.resolve(value),
          badValue => {
            if (badValue instanceof BackendError) {
              switch (badValue.status) {
                case 403:
                  dispatch(markSessionTimeout());
                  break;

                default:
                  let semiPos = badValue.message.indexOf(';');
                  let message = semiPos === -1 ? badValue.message : badValue.message.substr(0, semiPos);
                  dispatch(createNotification(
                    message, 'warning', `${badValue.error} (${badValue.status})`
                  ));
              }
            }
            return Promise.reject(badValue);
          }
        )
  }

  return returnValue;
}



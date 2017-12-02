import {SubmissionError} from "redux-form";

export function adaptValidationToSubmissionError(err) {

  if (err.status === 400 && err.message.startsWith("Validation failed") && err.errors) {
    const fieldErrors = err.errors.reduce((accumulator, value) => {
      if (value.field) {
        accumulator[value.field] = value.defaultMessage;
      }
      else {
        accumulator._error = value.defaultMessage;
      }
      return accumulator;
    }, {});

    return Promise.reject(new SubmissionError(fieldErrors));
  }
  else if (err.status === 400) {
    return Promise.reject(new SubmissionError({
      _error: err.message
    }));
  }
  return Promise.reject(err);
}
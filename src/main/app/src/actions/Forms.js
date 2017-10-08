import {SubmissionError} from "redux-form";

export function adaptValidationToSubmissionError(response) {

  return response.json().then(err => {
    if (err.status === 400 && err.message.startsWith("Validation failed") && err.errors) {
      const fieldErrors = err.errors.reduce((accumulator, value) => {
        accumulator[value.field] = value.defaultMessage;
        return accumulator;
      }, {});

      return Promise.reject(new SubmissionError(fieldErrors));
    }
    return Promise.reject(err);
  });

}
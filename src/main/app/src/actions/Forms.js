export const submitForm = (actionCreator) => (values, dispatcher) => {
  return dispatcher(actionCreator(values));
};
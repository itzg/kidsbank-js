export function formatCurrency(amount) {
  return amount.toLocaleString(undefined, {
    style: 'currency',
    currency: 'USD'
  })
}

// from https://stackoverflow.com/a/13627586/121324
export function ordinalOf(i) {
  let j = i % 10,
    k = i % 100;
  if (j === 1 && k !== 11) {
    return i + "st";
  }
  if (j === 2 && k !== 12) {
    return i + "nd";
  }
  if (j === 3 && k !== 13) {
    return i + "rd";
  }
  return i + "th";
}

/**
 *
 * @param i one-based of the week where 1=Sunday, ..., 7=Saturday
 */
export function weekdayOf(i) {
  //TODO i18n
  const days = [
    'Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'
  ];

  return days[i - 1];
}

export const dayOfWeekOptions = [
  {text: 'Sunday', value: 1},
  {text: 'Monday', value: 2},
  {text: 'Tuesday', value: 3},
  {text: 'Wednesday', value: 4},
  {text: 'Thursday', value: 5},
  {text: 'Friday', value: 6},
  {text: 'Saturday', value: 7}
];

import React from 'react';
import PropTypes from 'prop-types';

const LinkOpenNew = (props) => {
  return <a href={props.href} target='_blank' rel="noopener noreferrer">{props.children}</a>
};

LinkOpenNew.propTypes = {
  href: PropTypes.string.isRequired
};

export default LinkOpenNew;
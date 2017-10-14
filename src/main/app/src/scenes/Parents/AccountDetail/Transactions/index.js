import React, {Component} from 'react';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';
import {Header, Icon, Loader, Message, Table} from 'semantic-ui-react';
import {loadInitialTransactions} from "../../../../actions/transactions";
import moment from 'moment';
import {formatCurrency} from '../../../../components/locale';
import './index.css';

function IncomeCell(props) {
  if (props.amount < 0) {
    return <Table.Cell/>;
  }
  return <Table.Cell positive><Icon name='plus'/> {formatCurrency(props.amount)}</Table.Cell>;
}

function ExpenseCell(props) {
  if (props.amount >= 0) {
    return <Table.Cell/>;
  }
  return <Table.Cell negative><Icon name='minus'/> {formatCurrency(props.amount * -1)}</Table.Cell>;
}

class Transactions extends Component {
  constructor(props) {
    super(props);
  }

  static formatDate(date) {
    return moment(date).calendar(null, {
      sameDay: '[Today]',
      nextDay: '[Tomorrow]',
      nextWeek: 'dddd',
      lastDay: '[Yesterday]',
      lastWeek: '[Last] dddd',
      sameElse: 'MM/DD/YYYY'
    });
  }

  render() {
    let content;
    if (!this.props.loaded && this.props.loading) {
      content = <Loader active/>
    }
    else if (this.props.loaded && this.props.page.totalElements === 0) {
      content = <Message compact icon='arrow up' content='None yet. Create one above.'/>
    }
    else {

      let tableBody;
      if (this.props.loaded && this.props.loading) {
        tableBody = <Table.Cell colspan={4}>
          <Loader active inline/>
        </Table.Cell>;
      }
      else {
        tableBody = this.props.page.content.map(t => <Table.Row key={t.id}>
          <Table.Cell>{Transactions.formatDate(t.when)}</Table.Cell>
          <Table.Cell>{t.description}</Table.Cell>
          <IncomeCell amount={t.amount}/>
          <ExpenseCell amount={t.amount}/>
        </Table.Row>)
      }

      content = (
        <Table>
          <Table.Header>
            <Table.Row>
              <Table.HeaderCell width={2}>Date</Table.HeaderCell>
              <Table.HeaderCell>Description</Table.HeaderCell>
              <Table.HeaderCell colspan={2} width={4} textAlign='center'>Amount</Table.HeaderCell>
            </Table.Row>
          </Table.Header>

          <Table.Body>
            {tableBody}
          </Table.Body>
        </Table>
      );
    }

    return (
      <div className='Transactions'>
        <Header>Transactions</Header>
        {content}
      </div>
    );
  }

  componentDidMount() {
    this.props.loadInitialTransactions();
  }

  static propTypes = {
    accountId: PropTypes.string.required
  }
}

function mapStateToProps(state, ownProps) {
  if (state.transactions.byAccount[ownProps.accountId]) {
    const {page, loading, loaded} = state.transactions.byAccount[ownProps.accountId];
    return {
      page,
      loading,
      loaded
    };
  } else {
    return {
      loading: true
    }
  }
}

function mapDispatchToProps(dispatch, ownProps) {
  return {
    loadInitialTransactions() {
      dispatch(loadInitialTransactions(ownProps.accountId));
    }
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Transactions);
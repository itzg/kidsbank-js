import React, {Component} from 'react';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';
import {Button, Icon, Loader, Message, Table} from 'semantic-ui-react';
import {
  loadInitialTransactions,
  loadNewerTransactions,
  loadOlderTransactions,
  reloadInitialTransactions,
  selectTransaction
} from "../../../actions/transactions";
import moment from 'moment';
import {formatCurrency} from '../../../components/locale';
import './Transactions.css';
import {fetchAccountBalance} from "../../../actions/accounts";
import EditTransactionModal from './EditTransactionModal';

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

function formatDate(date) {
  return moment(date).calendar(null, {
    sameDay: '[Today]',
    nextDay: '[Tomorrow]',
    nextWeek: 'dddd',
    lastDay: '[Yesterday]',
    lastWeek: '[Last] dddd',
    sameElse: 'MM/DD/YYYY'
  });
}

class Transactions extends Component {
  state = {
    selected: null
  };

  select = (target) => {
    this.setState(prevState => ({
      selected: target === prevState.selected ? null : target
    }));
  };

  render() {
    let content;
    if (!this.props.loaded && this.props.loading) {
      content = <Loader active/>
    }
    else if (this.props.loaded && this.props.page.totalElements === 0) {
      content = <Message compact icon='arrow up' content='No transactions yet. Create one above.'/>
    }
    else {

      let tableBody;
      if (this.props.loaded && this.props.loading) {
        tableBody = <Table.Row>
          <Table.Cell colSpan={4}>
            <Loader active inline/>
          </Table.Cell>
        </Table.Row>;
      }
      else {
        tableBody = this.props.page.content.map(t => <Table.Row key={t.id}
                                                                onClick={() => this.select(t)}
                                                                active={t === this.state.selected}
        >
          <Table.Cell>{formatDate(t.when)}</Table.Cell>
          <Table.Cell>{t.description}</Table.Cell>
          <IncomeCell amount={t.amount}/>
          <ExpenseCell amount={t.amount}/>
        </Table.Row>)
      }

      content = (
        <Table selectable>
          <Table.Header>
            <Table.Row>
              <Table.HeaderCell width={2}>Date</Table.HeaderCell>
              <Table.HeaderCell>Description</Table.HeaderCell>
              <Table.HeaderCell colSpan={2} width={4}>Amount</Table.HeaderCell>
            </Table.Row>
          </Table.Header>

          <Table.Body>
            {tableBody}
          </Table.Body>

          <Table.Footer>
            <Table.Row>
              <Table.HeaderCell colSpan={4}>

                <div className='navigate'>

                  {!this.props.page.first &&
                  <Button content='Newer' icon='left chevron' labelPosition='left'
                          onClick={this.props.loadNewerTransactions} className='newer'/>}
                  <div className='actions'>
                    <Button content='Reload' icon='refresh' onClick={this.props.reloadTransactions}/>
                    <EditTransactionModal transaction={this.state.selected}/>
                  </div>
                  {!this.props.page.last &&
                  <Button content='Older' icon='right chevron' labelPosition='right'
                          onClick={this.props.loadOlderTransactions} className='older'/>}

                </div>
              </Table.HeaderCell>
            </Table.Row>
          </Table.Footer>
        </Table>
      );
    }

    return (
      <div className='Transactions'>
        {content}
      </div>
    );
  }

  componentDidMount() {
    this.props.loadInitialTransactions();
  }

  componentWillReceiveProps(nextProps) {
    this.setState({selected: null})
  }

  static propTypes = {
    accountId: PropTypes.string
  }
}

function mapStateToProps(state, ownProps) {
  if (state.transactions.byAccount[ownProps.accountId]) {
    const {page, loading, loaded} = state.transactions.byAccount[ownProps.accountId];
    return {
      page,
      loading,
      loaded,
      selectedTransaction: state.transactions.selected
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
    },

    reloadTransactions() {
      dispatch(reloadInitialTransactions(ownProps.accountId));
      dispatch(fetchAccountBalance(ownProps.accountId, true));
    },

    loadOlderTransactions() {
      dispatch(loadOlderTransactions(ownProps.accountId));
    },

    loadNewerTransactions() {
      dispatch(loadNewerTransactions(ownProps.accountId));
    },

    select(transaction) {
      dispatch(selectTransaction(transaction));
    }
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Transactions);
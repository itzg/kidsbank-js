import React from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {Button, Header, Loader, Message, Table} from 'semantic-ui-react';
import moment from 'moment';

import {createScheduled, deleteScheduled, loadScheduled} from "../../../actions/scheduled";
import {formatCurrency, ordinalOf, weekdayOf} from "../../../components/locale";
import CreateScheduledTransactionForm from './CreateScheduledTransactionForm';


function formatInterval(scheduled) {
  switch (scheduled.intervalType) {
    case 'WEEKLY':
      return `Every ${weekdayOf(scheduled.weekly.dayOfWeek)}`;
    case 'MONTHLY':
      const dom = scheduled.monthly.dayOfMonth;
      return `The ${ordinalOf(dom)} day of each month`;
    default:
      return '???';
  }
}

class Interval extends React.Component {
  render() {
    return <div>
      {formatInterval(this.props.scheduled)}
      <Header sub>{moment(this.props.scheduled.nextRun).fromNow()}</Header>
    </div>
  }
}

class ScheduledTransactions extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      creating: false,
      selected: null
    }
  }

  render() {
    if (this.props.loading) {
      return <Loader active inline/>
    }

    let content;
    if (this.props.scheduled.length === 0) {
      content = <Message>None yet</Message>;
    }
    else {
      content = <Table selectable>
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell>Description</Table.HeaderCell>
            <Table.HeaderCell>When</Table.HeaderCell>
            <Table.HeaderCell>Amount</Table.HeaderCell>
          </Table.Row>
        </Table.Header>

        <Table.Body>
          {this.props.scheduled.map(entry => <Table.Row key={entry.id}
                                                        active={this.state.selected === entry}
                                                        onClick={() => this.select(entry)}
          >
            <Table.Cell>{entry.description}</Table.Cell>
            <Table.Cell><Interval scheduled={entry}/></Table.Cell>
            <Table.Cell>{formatCurrency(entry.amount)}</Table.Cell>
          </Table.Row>)}
        </Table.Body>

        {this.state.selected &&
        <Table.Footer>
          <Table.Row>
            <Table.HeaderCell colSpan={3}>
              <Button negative onClick={this.deleteSelected}>Delete selected</Button>
            </Table.HeaderCell>
          </Table.Row>
        </Table.Footer>
        }
      </Table>
    }

    return <div className='ScheduledTransactions'>
      {this.state.creating ?
        <CreateScheduledTransactionForm onSubmit={this.handleCreate} onCancel={this.handleCancelCreate}/> :
        <div className='Actions'>
          <Button onClick={this.handleStartCreate}>Create</Button>
        </div>
      }

      {content}
    </div>
  }

  componentDidMount() {
    this.props.load();
  }

  handleStartCreate = () => {
    this.setState({creating: true});
  };

  handleCancelCreate = () => {
    this.setState({creating: false});
  };

  handleCreate = (values) => {
    return this.props.createScheduled(values)
      .then(() => {
        this.setState({creating: false})
      });
  };

  select = (entry) => {
    this.setState((prevState) => ({
      ...prevState,
      selected: entry === prevState.selected ? null : entry
    }));
  };

  deleteSelected = () => {
    this.props.deleteScheduled(this.state.selected)
      .then(() => {
        this.setState({selected: null});
      })
  };

  static propTypes = {
    accountId: PropTypes.string.isRequired
  }
}

function mapStateToProps(state, ownProps) {
  let {scheduled} = state;

  return {
    loading: scheduled.loadingByAccount.has(ownProps.accountId),
    scheduled: scheduled.byAccount[ownProps.accountId] || []
  }
}

function mapDispatchToProps(dispatch, ownProps) {
  return {
    load() {
      return dispatch(loadScheduled(ownProps.accountId));
    },

    createScheduled(values) {
      return dispatch(createScheduled(ownProps.accountId, values));
    },

    deleteScheduled(scheduled) {
      return dispatch(deleteScheduled(ownProps.accountId, scheduled))
    }
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(ScheduledTransactions);
import React from 'react';
import { Card, Col, Row, Spinner } from 'react-bootstrap';
import './SummaryViewer.css';
import { brevityHttpGet, poll } from './Utilities';

const POLLING_TIMEOUT = 10 * 60 * 1000;
const POLLING_INTERVAL = 5000;

class SummaryViewer extends React.Component {
  constructor(props) {
    super(props);
    this.props = props;
    this.state = { summaries: {} };
  }

  componentDidMount() {
    const { summaryIds } = this.props.location.state.data;
    const summaries = {};

    Object.entries(summaryIds).forEach(([name, id]) => {
      summaries[name] = "";
      poll(() => brevityHttpGet(`/summaries/${id}`), POLLING_TIMEOUT, POLLING_INTERVAL)
        .then((res) => {
          summaries[name] = res.data;
          this.setState({ summaries });
        })
        .catch((err) => {
          summaries[name] = "Took too long to generate summary...Try refreshing the page or submitting a smaller job.";
          this.setState({ summaries });
        });
    });

    this.setState({ summaries });
  }

  summaryItems(summaries) {
    return Object.entries(summaries).map(([name, summary], i) => {
      return (
        <Card key={i}>
            <Card.Header>
              {name}
              {
                summary === "" &&
                <div className="spinner-container">
                  <Spinner animation="border" role="status" variant="primary" />
                </div>
              }
            </Card.Header>
            <div className="summary">
              {
                summary !== "" &&
                <Card.Body>
                  {summary}
                </Card.Body>
              }
            </div>
        </Card>
      );
    });
  }

  render() {
    const summaries = this.state.summaries;

    return (
      <div>
        <Row>
          <Col lg={{ span: 10, offset: 1 }}>
            {
              this.state.summaries.length === 1 ?
                <h3>Summary</h3> :
                <h3>Summaries</h3>
            }
            <p className="blurb">
              Your request is being processed!
              <br/>
              We may need a few minutes to make sure we can build the best summary possible.
              <br/>
              When we're done, we'll put your {this.state.summaries.length === 1 ? "summary" : "summaries"} below.
            </p>
            {this.summaryItems(summaries)}
          </Col>
        </Row>
      </div>
    );
  }
}

export default SummaryViewer;
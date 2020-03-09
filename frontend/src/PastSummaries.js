import React from 'react';
import { Row, Col, Card } from 'react-bootstrap';
import { brevityHttpGet } from './Utilities';

class PastSummaries extends React.Component {
  constructor(props) {
    super(props);
    this.props = props;
    this.state = { summaries: {} };
  }

  componentDidMount() {
    brevityHttpGet('/summaries/user').then((res) => {
      this.setState({ summaries: res.data });
    });
  }

  summaryItems(summaries) {
    return Object.entries(summaries).map(([name, summary], i) => {
      return (
        <Card key={i}>
            <Card.Header>{name}</Card.Header>
            <div className="summary">
              <Card.Body>
                {summary === "" ? "Generating summary..." : summary}
              </Card.Body>
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
            <h3>Past Summaries</h3>
            <p className="blurb">
              Here you can see your previously generated summaries.
            </p>
            {this.summaryItems(summaries)}
          </Col>
        </Row>
      </div>
    );
  }
}

export default PastSummaries;
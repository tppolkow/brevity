import React from 'react';
import { get } from 'axios';
import { Row, Col, Card, Button } from 'react-bootstrap';
import  { BASE_URLS, ACCESS_TOKEN } from './Constants';
import './SummaryViewer.css';

function poll(fn, timeout, interval) {
    var endTime = Number(new Date()) + (timeout || 2000);
    interval = interval || 100;

    var checkCondition = function(resolve, reject) {
        var ajax = fn();
        ajax.then( function(response){
            if(response.status == 200) {
                resolve(response.data);
            }
            else if (Number(new Date()) < endTime) {
                setTimeout(checkCondition, interval, resolve, reject);
            }
            else {
                reject(new Error('timed out for ' + fn + ': ' + arguments));
            }
        });
    };

    return new Promise(checkCondition);
}
class SummaryViewer extends React.Component {
  constructor(props) {
    super(props);
    this.props = props;
    this.state = {
      summaries: {},
      fromChapterSelect: this.props.location.state.data.fromChapterSelect || false
     };
  }

  componentDidMount() {
    const summaryIds = this.props.location.state.data.summaryIds;
    const summaries = {};
    Object.entries(summaryIds).forEach(([name, id]) => {
      summaries[name] = "";
      let config = { headers: { Authorization: 'Bearer ' + localStorage.getItem(ACCESS_TOKEN) }}
      poll(() => get(`${BASE_URLS.serverUrl}/summaries/${id}`, config), 10 * 60 * 1000, 1000)
        .then(res => {
          summaries[name] = res.data;
          this.setState({ summaries });
        })
        .catch(err => {
          console.log(`Could not fetch summary for ${name}`);
        });
    });
    this.setState({ summaries })
  }

  summaryItems(summaries) {
    return Object.entries(summaries).map(([name, summary], i) => {
      return (

        <Card key={i}>
            <Card.Header>{name}</Card.Header>
            <div class="summary">
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
            {this.state.fromChapterSelect && <Button>Go back to Chapter Select</Button>}
            {this.summaryItems(summaries)}
          </Col>
        </Row>
      </div>
    );
  }
}

export default SummaryViewer;
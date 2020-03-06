import React from 'react';
import { get } from 'axios';
import Config from './Config';

class SummaryViewer extends React.Component {
  constructor(props) {
    super(props);
    this.props = props;
    this.state = { summaries: {} };

    console.log(props.location.state);

    if (props.location.state && props.location.state.summaries) {
      this.state = { summaries: props.location.state.summaries };
      console.log("redirected to", this.state);
      console.log("props location state", props.location.state);
    } else {
      get(Config.serverUrl + "/summaries").then(res => {
        this.setState({ summaries: res.data });
        console.log("standalone", this.state);
      });
    }
  }

  render() {
    const summaries = this.state.summaries;

    return Object.entries(summaries).map(([name, summary]) => {
      return (
        <div key={name}>
          <h5>{name}</h5>
          <div dangerouslySetInnerHTML={{__html: summary}} />
        </div>
      );
    });
  }
}

export default SummaryViewer;
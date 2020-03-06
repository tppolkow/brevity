import React from 'react';
import { get } from 'axios';
import  { BASE_URLS, ACCESS_TOKEN } from './Constants';

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
      let config = { headers: { Authorization: 'Bearer ' + localStorage.getItem(ACCESS_TOKEN) }}
      get(BASE_URLS.serverUrl + "/summaries", config).then(res => {
        this.setState({ summaries: res.data });
      });
    }
  }

  render() {
    const summaries = this.state.summaries;

    return Object.entries(summaries).map(([name, summary]) => {
      return (
        <div key={name}>
          <h5>{name}</h5>
          <p>{summary}</p>
        </div>
      );
    });
  }
}

export default SummaryViewer;
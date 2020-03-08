import React from 'react';
import { Container } from 'react-bootstrap';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import './App.css';
import BrevityNavbar from './BrevityNavbar';
import ChapterSelectorList from './ChapterSelectorList';
import DocumentUploadForm from './DocumentUploadForm';
import Login from './Login';
import Oauth2RedirectHandler from './OauthRedirectHandler';
import PastSummaries from './PastSummaries';
import SummaryViewer from './SummaryViewer';

class App extends React.Component {
  constructor(props) {
    super(props);
    this.props = props;
    this.state = {
      username: 'Bob'
    };
  }

  render() {
    return (
      <Router>
        <div className="App">
            <Switch>
              <Route exact path="/" component={Login} />
              <div>
                <BrevityNavbar/>
                <Container>
                  <Route 
                    path="/upload"  
                    render={(props) => <DocumentUploadForm {...props} />}
                  />
                  <Route path="/chapter-select" component={ChapterSelectorList} />
                  <Route path="/summary" component={SummaryViewer} />
                  <Route path="/past-summaries" component={PastSummaries} />
                  <Route path="/oauth2/redirect" component={Oauth2RedirectHandler} />
                  <div className="footer">
                    Copyright &copy; 2020 Brevity. All Rights Reserved.
                  </div>
                </Container>
              </div>
            </Switch>
        </div>
      </Router>
    );
  }
}

export default App;

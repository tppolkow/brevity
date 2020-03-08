import React from 'react';
import { Container } from 'react-bootstrap';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import ChapterSelectorList from './ChapterSelectorList';
import DocumentUploadForm from './DocumentUploadForm';
import SummaryViewer from './SummaryViewer';
import Oauth2RedirectHandler from './OauthRedirectHandler';
import Login from './Login';
import PrivateRoute from './PrivateRoute';
import './App.css';

class App extends React.Component {
  constructor(props) {
    super(props);
    this.props = props;
  }

  render() {
    return (
      <Router>
        <div className="App">
          <Container>
            <Switch>
              <Route exact path="/" component={Login} />
              <PrivateRoute path="/upload" component={DocumentUploadForm} />
              <PrivateRoute path="/chapter-select" component={ChapterSelectorList} />
              <PrivateRoute path="/summary" component={SummaryViewer} />
              <Route path="/oauth2/redirect" component={Oauth2RedirectHandler} />
            </Switch>
          </Container>
        </div>
      </Router>
    );
  }
}

export default App;

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
import BrevityNavbar from './BrevityNavbar';

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
              <React.Fragment>
                <BrevityNavbar/>
                <Container>
                  <PrivateRoute path="/upload" component={DocumentUploadForm} />
                  <PrivateRoute path="/chapter-select" component={ChapterSelectorList} />
                  <PrivateRoute path="/summary" component={SummaryViewer} />
                  <Route path="/oauth2/redirect" component={Oauth2RedirectHandler} />
                  <div className="footer">
                    Copyright &copy; 2020 Brevity. All Rights Reserved.
                  </div>
                </Container>
              </React.Fragment>
            </Switch>
        </div>
      </Router>
    );
  }
}

export default App;

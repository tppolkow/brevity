import React from 'react';
import { Container } from 'react-bootstrap';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import Config from './Config';
import ChapterSelectorList from './ChapterSelectorList';
import DocumentUploadForm from './DocumentUploadForm';
import SummaryViewer from './SummaryViewer';
import Login from './Login';
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
            <h1 className="title">Brevity</h1>
            <Route path="/login" component={Login} />
            <Route 
              path="/" 
              exact 
              render={(props) => <DocumentUploadForm {...props} endpoint={Config.serverUrl + "/upload"}/>}
            />
            <Route path="/chapter-select" component={ChapterSelectorList} />
            <Route path="/summary" component={SummaryViewer} />
          </Container>
        </div>
      </Router>
    );
  }
}

export default App;

import React from 'react';
import { Container } from 'react-bootstrap';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import ChapterSelectorList from './ChapterSelectorList';
import DocumentUploadForm from './DocumentUploadForm';
import SummaryViewer from './SummaryViewer';
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
            <Route 
              path="/" 
              exact 
              render={(props) => <DocumentUploadForm {...props} endpoint="http://localhost:8080/upload"/>}
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

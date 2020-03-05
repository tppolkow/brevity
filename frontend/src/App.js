import React from 'react';
import { Container, Navbar, Nav } from 'react-bootstrap';
import { BrowserRouter as Router, Route, Link } from 'react-router-dom';
import Config from './Config';
import ChapterSelectorList from './ChapterSelectorList';
import DocumentUploadForm from './DocumentUploadForm';
import SummaryViewer from './SummaryViewer';
import logo from './brevity_icon_dark.png'
import './App.css';

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
          <Navbar bg="dark" variant="dark" expand="lg">
            <Navbar.Brand href="/">
              <img
                alt="Brevity"
                src={logo}
                width="50"
                className="d-inline-block"
              />{' '}
              Brevity
            </Navbar.Brand>
            <Navbar.Toggle aria-controls="basic-navbar-nav" />
            <Navbar.Collapse id="basic-navbar-nav">
              <Nav className="mr-auto">
                <Nav.Link as={Link} to="/">
                  Summarizer
                </Nav.Link>
                <Nav.Link>
                  Past Summaries
                </Nav.Link>
              </Nav>
              <Navbar.Text>
                {this.state.username}
              </Navbar.Text>
            </Navbar.Collapse>
          </Navbar>
          <Container>
            <Route 
              path="/" 
              exact 
              render={(props) => <DocumentUploadForm {...props} endpoint={Config.serverUrl + "/upload"}/>}
            />
            <Route path="/chapter-select" component={ChapterSelectorList} />
            <Route path="/summary" component={SummaryViewer} />
            <div className="footer">
              Copyright &copy; 2020 Brevity. All Rights Reserved.
            </div>
          </Container>
        </div>
      </Router>
    );
  }
}

export default App;

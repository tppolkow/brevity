import React from 'react';
import { Nav, Navbar, NavDropdown } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import './BrevityNavbar.css';
import logo from './img/brevity_icon_dark.png';

class BrevityNavbar extends React.Component {
  constructor(props) {
    super(props);
    this.props = props;
    this.state = { username: "Bob" };
  }

  render() {
    return (
      <Navbar bg="dark" variant="dark" expand="lg">
        <Navbar.Brand as={Link} to="/">
          <img
            alt="Brevity"
            src={logo}
            width="40"
            className="d-inline-block"
          />{' '}
          Brevity
        </Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="mr-auto">
            <Nav.Link as={Link} to="/upload">
              Summarizer
            </Nav.Link>
            <Nav.Link as={Link} to="/past-summaries">
              Past Summaries
            </Nav.Link>
          </Nav>
          <NavDropdown alignRight title="Profile">
            <NavDropdown.Header>Signed in as {this.state.username}</NavDropdown.Header>
            <NavDropdown.Item>Logout</NavDropdown.Item>
          </NavDropdown>
        </Navbar.Collapse>
      </Navbar>
    );
  }
}

export default BrevityNavbar;
import React from 'react';
import { Navbar, Nav } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import logo from './img/brevity_icon_dark.png';
import './BrevityNavbar.css';

class BrevityNavbar extends React.Component {
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
            <Nav.Link>
              Past Summaries
                </Nav.Link>
          </Nav>
          <Navbar.Text>
            Bob
          </Navbar.Text>
        </Navbar.Collapse>
      </Navbar>
    );
  }
}

export default BrevityNavbar;
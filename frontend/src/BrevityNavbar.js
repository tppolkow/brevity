import React from 'react';
import { Nav, Navbar, NavDropdown } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { ACCESS_TOKEN } from './Constants';
import { brevityHttpGet } from './Utilities';
import './BrevityNavbar.css';
import logo from './img/brevity_icon_dark.png';


class BrevityNavbar extends React.Component {
  constructor(props) {
    super(props);
    this.props = props;
    this.handleLogOut = this.handleLogOut.bind(this);
    this.state = {
        userName: ''
    }
  }

  handleLogOut() {
    if (localStorage.getItem(ACCESS_TOKEN) !== null) {
        localStorage.removeItem(ACCESS_TOKEN)
    }
    window.location.href = "/"
  }

  componentDidMount() {
    brevityHttpGet('/auth/user').then(res => this.setState({ userName: res.data}));
  }

  render() {
    return (
      <Navbar bg="dark" variant="dark" expand="lg">
        <Navbar.Brand as={Link} to="/" onClick={this.handleLogOut}>
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
            <NavDropdown.Header>Signed in as { this.state.userName }</NavDropdown.Header>
            <NavDropdown.Item onClick={this.handleLogOut}>Logout</NavDropdown.Item>
          </NavDropdown>
        </Navbar.Collapse>
      </Navbar>
    );
  }
}

export default BrevityNavbar;
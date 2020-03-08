import React from 'react';
import { Col, Container, Image, Row } from 'react-bootstrap';
import GoogleButton from 'react-google-button';
import { GOOGLE_AUTH_URL } from './Constants';
import brevityLogo from './img/brevity_logo.png';
import './Login.css';

class Login extends React.Component {
  constructor(props) {
    super(props);
    this.props = props;
  }

  render() {
    return (
      <Container className="login-container">
        <Image className="brevity-logo" src={brevityLogo} alt="brevity logo" />
        <GoogleButton className="google-button" onClick={() => window.location.href = GOOGLE_AUTH_URL} />
      </Container>
    );
  }
}

export default Login;



import React from 'react';
import { Container, Image } from 'react-bootstrap';
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
        <p>
          Welcome to Brevity, a tool for summarizing textbook PDFs.
          <br/>
          We help you get the most important information from textbooks so you don't have to.
          <br/>
          Start by signing in with your Google account!
        </p>
        <GoogleButton className="google-button" onClick={() => window.location.href = GOOGLE_AUTH_URL} />
      </Container>
    );
  }
}

export default Login;



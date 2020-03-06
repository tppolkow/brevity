import React from 'react';
import { GOOGLE_AUTH_URL } from './Constants';
import brevityLogo from './img/brevity-logo.png';
import GoogleButton from 'react-google-button';
import './Login.css';
import { Container, Image, Row, Col } from 'react-bootstrap';

class Login extends React.Component {
    constructor(props) {
        super(props);
        this.props = props;
    }


    render() {
        return(
            <Container className="login-container">
                <Row className="login-logo">
                    <Col md={{span: 6, offset: 3}}>
                        <Image className="brevity-logo" src={brevityLogo} alt="brevity logo" />
                    </Col>
                </Row>
                <Row className="login-content">
                    <Col md={{span: 6, offset: 3}}>
                        <GoogleButton className="google-button" onClick={() => window.location.href=GOOGLE_AUTH_URL} />
                    </Col>
                </Row>
            </Container>
        )
    }

}

export default Login;



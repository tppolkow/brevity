import React from 'react';
import { GOOGLE_AUTH_URL } from './Constants'
import googleLogo from './img/google-logo.png'

class Login extends React.Component {
    constructor(props) {
        super(props);
        this.props = props;
    }


    render() {
        return(
            <div className="login-container">
                <div className="login-content">
                    <a className="google-login" href={GOOGLE_AUTH_URL}>
                        <img src={googleLogo} alt="google logo"/> Log in With Google </a>
                </div>
            </div>
        )
    }

}

export default Login;



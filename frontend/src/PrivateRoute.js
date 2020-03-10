import React from 'react';
import { Route, Redirect } from 'react-router-dom';
import { ACCESS_TOKEN } from './Constants';

const getToken = () => {
    return localStorage.getItem(ACCESS_TOKEN) !== null ? true : false;
}

const PrivateRoute = ({ component: Component, ...rest}) => (
    <Route {...rest} 
        render={props => getToken() ? ( <Component {...rest} {...props} />) : 
            ( <Redirect to={{
                pathname: '/',
                state: { from: props.location }
            }}/> )
        }/>
);

export default PrivateRoute;
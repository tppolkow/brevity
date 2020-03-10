import React, { Component } from 'react';
import { Redirect } from 'react-router-dom';
import { ACCESS_TOKEN } from './Constants';

class OAuth2RedirectHandler extends Component {
  getUrlParameter(name) {
    name = name.replace(/[[], '\\[').replace(/[]]/, '\\]');
    var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');

    var results = regex.exec(this.props.location.search);
    return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
  };

  render() {
    const token = this.getUrlParameter('token');

    if (token) {
      localStorage.setItem(ACCESS_TOKEN, token);
      return <Redirect to={{
        pathname: "/upload",
        state: this.props.location
      }} />;
    }
  }
}

export default OAuth2RedirectHandler;

import { get, post } from 'axios';
import { ACCESS_TOKEN, BASE_URLS } from './Constants';

export function brevityHttpGet(url) {
  let config = {
    headers: {
      Authorization: `Bearer ${localStorage.getItem(ACCESS_TOKEN)}`
    }
  };
  return get(BASE_URLS.serverUrl + url, config);
}

export function brevityHttpPost(url, body) {
  let config = {
    headers: {
      Authorization: `Bearer ${localStorage.getItem(ACCESS_TOKEN)}`
    }
  };
  return post(BASE_URLS.serverUrl + url, body, config);
}

export function poll(fn, timeout, interval) {
  var endTime = Number(new Date()) + (timeout || 2000);
  interval = interval || 100;

  var checkCondition = function (resolve, reject) {
    var ajax = fn();
    ajax.then(function (response) {
      if (response.status === 200) {
        resolve(response.data);
      }
      else if (Number(new Date()) < endTime) {
        setTimeout(checkCondition, interval, resolve, reject);
      }
      else {
        reject(new Error('timed out for ' + fn + ': ' + arguments));
      }
    });
  };

  return new Promise(checkCondition);
}
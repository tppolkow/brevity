export const ACCESS_TOKEN = 'access_token'
const dev = {
    serverUrl: 'http://localhost:8080',
    clientUrl: 'http://localhost:3000'
}

const prod = {
    serverUrl: 'https://brevity-backend.herokuapp.com',
    clientUrl: 'https://fydp-brevity.netlify.com'
}

export const BASE_URLS = process.env.NODE_ENV === 'production' ? prod : dev;
export const GOOGLE_AUTH_URL = BASE_URLS.serverUrl + '/oauth2/authorize/google?redirect_uri=' + BASE_URLS.clientUrl
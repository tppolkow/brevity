const dev = {
    serverUrl: "http://localhost:8080"
}

const prod = {
    serverUrl: "https://brevity-backend.herokuapp.com"
}

const Config = process.env.NODE_ENV === 'production' ? prod : dev;
export default Config; 
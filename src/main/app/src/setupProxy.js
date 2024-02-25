const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function (app) {
  app.use('/api', createProxyMiddleware({ target: 'http://localhost:8080/'}))
  // To utilize Spring Security OAuth2 integration on the backend
  app.use('/oauth2', createProxyMiddleware({ target: 'http://localhost:8080/', xfwd: true}))
  app.use('/login', createProxyMiddleware({ target: 'http://localhost:8080/', xfwd: true}))
  app.use('/logout', createProxyMiddleware({ target: 'http://localhost:8080/', xfwd: true}))
};
const proxy = require('http-proxy-middleware');

module.exports = function (app) {
  app.use(proxy('/api', {target: 'http://localhost:8080/'}));
  app.use(proxy('/oauth2', {target: 'http://localhost:8080/', xfwd: true}));
  app.use(proxy('/login', {target: 'http://localhost:8080/', xfwd: true}));
  app.use(proxy('/logout', {target: 'http://localhost:8080/', xfwd: true}));
};
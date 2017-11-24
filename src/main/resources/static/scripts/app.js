'use strict';

var app = angular.module('demoApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch',
    'ngStorage',
    'ui.router',
    'ab-base64'
]);

app.constant('config', {
  appName: 'Demo',
  authUrl: '/oauth'
});

app.config(function($stateProvider, $urlRouterProvider) {

  $urlRouterProvider.otherwise('/');

  $stateProvider
    .state('index', {
      url: '/',
      templateUrl: 'views/main.html',
      controller: 'MainCtrl',
    })
    .state('login', {
      url: '/login',
      controller: 'LoginCtrl',
    })

});

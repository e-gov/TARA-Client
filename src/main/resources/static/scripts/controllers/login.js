'use strict';

var app = angular.module('demoApp');

app.controller('LoginCtrl', function ($scope, $state) {
    $state.go('index');
});

'use strict';

var app = angular.module('demoApp');

app.controller('MainCtrl', function($scope, $rootScope, $q, $window) {

    $scope.login = function() {
        $window.location.href = '/oauth/request';
    };

});

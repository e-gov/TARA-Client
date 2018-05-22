'use strict';

var app = angular.module('demoApp');

app.controller('MainCtrl', function($scope, $rootScope, $q, $window) {

    $scope.login = function() {
        var request = '/oauth/request';
        var parameters = [];

        if (document.getElementById('eidasonly').checked) parameters.push('scope=eidasonly');

        if (parameters.length > 0) request += ('?' + parameters.join('&'));
        $window.location.href = request;
    };

});

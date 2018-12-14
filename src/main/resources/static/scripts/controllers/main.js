'use strict';

var app = angular.module('demoApp');

app.controller('MainCtrl', function($scope, $rootScope, $q, $window) {

    $scope.login = function() {
        var request = '/oauth/request';
        var parameters = [];

        if (document.getElementById('eidasonly').checked) parameters.push('scope=eidasonly');

        var acr_values = document.getElementById('acr_values').value;
        if (acr_values && acr_values.length > 0) {
            parameters.push('acr_values=' + encodeURIComponent(acr_values));
        }

        var ui_locales_values = document.getElementById('ui_locales').value;
        if (ui_locales_values && ui_locales_values.length > 0) {
            parameters.push('ui_locales=' + encodeURIComponent(ui_locales_values));
        }

        if (parameters.length > 0) request += ('?' + parameters.join('&'));
        $window.location.href = request;
    };

});

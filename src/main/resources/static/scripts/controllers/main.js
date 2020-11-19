'use strict';

var app = angular.module('demoApp');

app.controller('MainCtrl', function($scope, $rootScope, $q, $window) {

    $scope.login = function() {
        var request = '/oauth/request';
        var parameters = [];
        var selectedScopes = [];

        if (document.getElementById('openid').checked) selectedScopes.push('openid');
        if (document.getElementById('idcard').checked) selectedScopes.push('idcard');
        if (document.getElementById('mid').checked) selectedScopes.push('mid');
        if (document.getElementById('banklink').checked) selectedScopes.push('banklink');
        if (document.getElementById('eidas').checked) selectedScopes.push('eidas');
        if (document.getElementById('smartid').checked) selectedScopes.push('smartid');
        if (document.getElementById('eidasonly').checked) selectedScopes.push('eidasonly');
        if (document.getElementById('email').checked) selectedScopes.push('email');
        if (document.getElementById('phone').checked) selectedScopes.push('phone');
        if (document.getElementById('legalperson').checked) selectedScopes.push('legalperson');

        parameters.push('scope=' + selectedScopes.join(' '));


        var acr_values = document.getElementById('acr_values').value;
        if (acr_values && acr_values.length > 0) {
            parameters.push('acr_values=' + encodeURIComponent(acr_values));
        }

        var ui_locales_values = document.getElementById('ui_locales').value;
        if (ui_locales_values && ui_locales_values.length > 0) {
            parameters.push('ui_locales=' + encodeURIComponent(ui_locales_values));
        }

        var eidas_country_value = document.getElementById('eidas_country').value;
        if (eidas_country_value && eidas_country_value.length > 0) {
            parameters.push('eidas_country=' + encodeURIComponent(eidas_country_value));
        }

        if (parameters.length > 0) request += ('?' + parameters.join('&'));
        $window.location.href = request;
    };

});

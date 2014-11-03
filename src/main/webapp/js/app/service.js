/**
 * Created by Sheldon Chen on 2014/9/21.
 */
'use strict';

angular.module('SkeletonApp.services', []).
    factory('skeletonAPIService', function($http) {
        var skeletonAPI = {};

        skeletonAPI.register = function(account, password, sex) {
            var params = JSON.stringify({
                "account" : account,
                "password" : password,
                "sex": sex
            });
            return $http({
                method: 'POST',
                url: '/register',
                data: params,
                headers: {'Content-Type': 'application/json'}
            });
        };

        skeletonAPI.login = function(account, password) {
            var params = JSON.stringify({
                "account" : account,
                "password" : password
            });
            return $http({
                method: 'POST',
                url: '/login',
                data: params,
                headers: {'Content-Type': 'application/json'}
            });
        };

        return businessAPI;
    });

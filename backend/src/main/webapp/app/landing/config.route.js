/**
 * Created by bogda on 11/05/2016.
 */
(function () {
    'use strict';
    angular
        .module('app.landing')
        .config(configFunction)
    
    configFunction.$inject = ['$routeProvider'];

    function configFunction($routeProvider) {
        $routeProvider.when('/',{
            templateUrl: 'app/landing/landing.html'
        });
    }
})();
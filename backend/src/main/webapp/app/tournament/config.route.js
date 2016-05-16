(function () {
    'use strict';

    angular
        .module('app.tournament')
        .config(configFunction)

    configFunction.$inject = ['$routeProvider']

    function configFunction($routeProvider) {
        $routeProvider.when('/tournament', {
            templateUrl: 'app/tournament/tournament.html',
            controller: 'TournamentController',
            controllerAs: 'vm'
        });
    }
})();

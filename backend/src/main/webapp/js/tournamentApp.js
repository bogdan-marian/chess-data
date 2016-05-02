(function(){
    var gem = {name: 'Azurite', price: 2.95};

    var app = angular.module('tournamentApp',[]);

    app.controller('TournamentController', function(){
        this.product = gem;
    });
})();
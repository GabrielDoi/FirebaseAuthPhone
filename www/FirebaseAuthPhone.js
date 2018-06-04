var exec = require('cordova/exec');
var PLUGIN_NAME = "FirebaseAuthPhone";

module.exports.coolMethod = function (arg0, success, error) {
    exec(success, error, PLUGIN_NAME, 'coolMethod', [arg0]);
};

module.exports.add = function (arg0, success, error) {
	exec(success, error, PLUGIN_NAME, 'add', [arg0]);
};

module.exports.substract = function (arg0, success, error) {
	exec(success, error, PLUGIN_NAME, 'substract', [arg0]);
};

module.exports.getIdToken = function (_forceRefresh, success, error) {
    if (_forceRefresh == null) _forceRefresh = false;
    alert("teste "+_forceRefresh);
    exec(success, error, PLUGIN_NAME, "getIdToken", [{forceRefresh: _forceRefresh}]);
};
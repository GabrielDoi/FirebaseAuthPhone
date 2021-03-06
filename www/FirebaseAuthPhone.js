var exec = require('cordova/exec');
var PLUGIN_NAME = "FirebaseAuthPhone";

module.exports.coolMethod = function (arg0, success, error) {
    exec(success, error, PLUGIN_NAME, 'coolMethod', [arg0]);
};

module.exports.add = function (arg0, success, error) {
	exec(success, error, PLUGIN_NAME, 'add', [arg0]);
};

module.exports.substract = function (p1, p2, success, error) {
	exec(success, error, PLUGIN_NAME, 'substract', [{param1:p1, param2:p2}]);
};

module.exports.getIdToken = function (_forceRefresh, success, error) {
    exec(success, error, PLUGIN_NAME, "getIdToken", [{forceRefresh: _forceRefresh}]);
};

module.exports.verifyPhoneNumber = function(_phoneNumber, _timeoutMillis, success, error) {
	exec(success, error, PLUGIN_NAME, "verifyPhoneNumber", [{phoneNumber: _phoneNumber, timeoutMillis: _timeoutMillis}]);
};

module.exports.signInWithVerificationId = function(_verificationId, _code, success, error) {
	exec(success, error, PLUGIN_NAME, "signInWithVerificationId", [{verificationId: _verificationId, code: _code}]);
};
// Copyright (C) 2013 Polychrom Pty Ltd
//
// This program is licensed under the 3-clause "Modified" BSD license,
// see LICENSE file for full definition.

var AccountManager = function() {};

AccountManager.prototype.getAccountsByType = function(type, callback)
{
	return cordova.exec(
		function(accounts) { callback(undefined, accounts) },
		callback,
		'AccountManager', 'getAccountsByType', [type]);
};

AccountManager.prototype.addAccountExplicitly = function(accountType, username, password, userdata, callback)
{
	return cordova.exec(
		function(account) { callback(undefined, account) },
		callback,
		'AccountManager', 'addAccountExplicitly', [accountType, username, password, userdata]);
};

AccountManager.prototype.clearPassword = function(account, callback)
{
	return cordova.exec(
		function() { callback(); },
		callback,
		'AccountManager', 'clearPassword', [account._index]);
};

AccountManager.prototype.removeAccount = function(account, callback)
{
	return cordova.exec(
		function() { callback(); },
		callback,
		'AccountManager', 'removeAccount', [account._index]);
};

AccountManager.prototype.setAuthToken = function(account, authTokenType, authToken, callback)
{
	return cordova.exec(
		function() { callback(); },
		callback,
		'AccountManager', 'setAuthToken', [account._index, authTokenType, authToken]);
};

AccountManager.prototype.peekAuthToken = function(account, authTokenType, callback)
{
	return cordova.exec(
		function(result) { callback(undefined, result.value); },
		callback,
		'AccountManager', 'peekAuthToken', [account._index, authTokenType]);
};

AccountManager.prototype.getAuthToken = function(account, authTokenType, callback)
{
	return cordova.exec(
		function(result) { callback(undefined, result.value); },
		callback,
		'AccountManager', 'getAuthToken', [account, authTokenType]);
};

AccountManager.prototype.setPassword = function(account, password, callback)
{
	return cordova.exec(
		function() { callback(); },
		callback,
		'AccountManager', 'setPassword', [account._index, password]);
};

AccountManager.prototype.getPassword = function(account, callback)
{
	return cordova.exec(
		function(result) { callback(undefined, result.value); },
		callback,
		'AccountManager', 'getPassword', [account._index]);
};

AccountManager.prototype.setUserData = function(account, key, value, callback)
{
	return cordova.exec(
		function() { callback(); },
		callback,
		'AccountManager', 'setUserData', [account._index, key, value]);
};

AccountManager.prototype.getUserData = function(account, key, callback)
{
	return cordova.exec(
		function(result) { callback(undefined, result.value); },
		callback,
		'AccountManager', 'getUserData', [account._index, key]);
};

if(!window.plugins) window.plugins = {};
window.plugins.accountmanager = new AccountManager();
module.exports = function(context)
{
	var pluginlist = [
		'https://github.com/polychrom/cordova-android-accountmanager.git'
	];

	var sys = require('sys'), exec = require('child_process').exec;

	function puts(error, stdout, stderr) {
		sys.puts(stdout);
	}

	pluginlist.forEach(function(plug) {
		exec('cordova plugin add ' + plug, puts);
	});
};
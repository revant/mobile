cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "id": "cordova-plugin-statusbar.statusbar",
        "file": "plugins/cordova-plugin-statusbar/www/statusbar.js",
        "pluginId": "cordova-plugin-statusbar",
        "clobbers": [
            "window.StatusBar"
        ]
    },
    {
        "id": "cordova-plugin-inappbrowser.inappbrowser",
        "file": "plugins/cordova-plugin-inappbrowser/www/inappbrowser.js",
        "pluginId": "cordova-plugin-inappbrowser",
        "clobbers": [
            "cordova.InAppBrowser.open",
            "window.open"
        ]
    },
    {
        "id": "com.polychrom.cordova.AccountManagerPlugin.accountmanager",
        "file": "plugins/com.polychrom.cordova.AccountManagerPlugin/www/js/accountmanager.js",
        "pluginId": "com.polychrom.cordova.AccountManagerPlugin",
        "clobbers": [
            "accountmanager"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "cordova-plugin-whitelist": "1.3.2",
    "cordova-plugin-statusbar": "2.2.2",
    "cordova-plugin-inappbrowser": "1.7.0",
    "com.polychrom.cordova.AccountManagerPlugin": "0.1.0"
};
// BOTTOM OF METADATA
});
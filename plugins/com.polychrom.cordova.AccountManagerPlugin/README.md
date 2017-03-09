cordova-android-accountmanager
==============================

Introduction
------------

cordova-android-accountmanager is an [Android](https://github.com/android) [AccountManager](http://developer.android.com/reference/android/accounts/AccountManager.html) plugin for [cordova-android](https://github.com/apache/cordova-android).

It currently only supports explicit account handling (programatically adding/removing/settin/getting account details to the AccountManager).


Basic Usage (Explicit account handling)
-------

	var am = window.plugins.accountmanager;

	// Add account explicitly
	am.addAccountExplicitly('MyAccountType', 'bob', 'passwordbob', null, function(error, bob)
	{
		// bob.name = 'bob'
		// bob.type = 'MyAccountType'

		// List all accounts of MyAccountType
		am.getAccountsByType('MyAccountType', function(error, accounts)
		{
			accounts.forEach(function(account)
			{
				console.log('Account: ' + JSON.stringify(account));
			});
		});

		// Get password
		am.getPassword(bob, function(error, password)
		{
			console.log("Bob's password: " + password);
		});
		
		am.setAuthToken(bob, 'authtoken', 'secret token');
		am.getAuthToken(bob, 'authtoken', true, function(error, token) {
			console.log("Bob's token: " + token);
		});

		// Get/Set user data
		am.setUserData(bob, 'age', 30);
		am.getUserData(bob, 'age', function(error, age)
		{
			console.log('Bob is ' + age + 'years old');
		});

		// Remove account
		am.removeAccount(bob);
	});

Basic Usage (Authenticator based handling)
-----

	Not yet supported.


Project Setup
-------------

### Installation - Cordova Command Line

`cordova plugin add https://github.com/polychrom/cordova-android-accountmanager.git`

### Installation - Manual ( Step 1 )

1. Copy (or link) all the .java files into your project src (under the appropriate com.polychrom.cordova package).
2. Copy accountmanager.js into your cordova app's www directory
3. Add `<script charset="utf-8" src="accountmanager.js"></script>` to your cordova app's HTML.
4. Copy (or link) authenticator.xml to your project's res/xml/ folder.

### Installation - Manual ( Step 2 ) - Edit Android Manifest for Authenticator Service

To register the AuthenticatorService with your application, the following will need to be added to your manifest's application element:

	<!-- The authenticator service -->
	<service android:name="com.polychrom.cordova.AuthenticatorService" android:exported="false">
		<intent-filter>
			<action android:name="android.accounts.AccountAuthenticator" />
		</intent-filter>
		<meta-data android:name="android.accounts.AccountAuthenticator" android:resource="@xml/authenticator" />
	</service>

### Installation - Manual ( Step 3 ) - Add Permissions

Depending on the level of usage, the following permissions may be required by the plugin for correct usage:

 * [android.permission.MANAGE_ACCOUNTS](http://developer.android.com/reference/android/Manifest.permission.html#MANAGE_ACCOUNTS)
 * [android.permission.AUTHENTICATE_ACCOUNTS](http://developer.android.com/reference/android/Manifest.permission.html#AUTHENTICATE_ACCOUNTS)
 * [android.permission.GET_ACCOUNTS](http://developer.android.com/reference/android/Manifest.permission.html#GET_ACCOUNTS)
 * [android.permission.USE_CREDENTIALS](http://developer.android.com/reference/android/Manifest.permission.html#USE_CREDENTIALS)
 
### Configuration options

This plugin can be configured via Android string resources (res/values/strings.xml) as follows:

* **[required]** `aam_account_type`: Specifies the plugin's authenticator account type (this value must match the first parameter passed into accountmanager.addAccountExplicitly)
	<string name="aam_account_type">MyCustomAccountName</string>

See the [AccountManager documentation](http://developer.android.com/reference/android/accounts/AccountManager.html) for specific requirements for each function.

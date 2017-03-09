function onDeviceReady()
{
	var am = window.plugins.accountmanager;

	// Explicitly add an account for testing
	am.addAccountExplicitly('MyCustomAccountType', 'test', 'password', {email: 'test@testdomain.com'}, function(error, bob)
	{
	});

	am.getAccountsByType('MyCustomAccountType', function(error, accounts)
	{
		if(error)
		{
			document.body.appendChild(document.createTextNode('ERROR: ' + error));
			return;
		}
		else if(!accounts || !accounts.length)
		{
			document.body.appendChild(document.createTextNode('This device has no accounts'));
			return;
		}
		
		var list = document.getElementById('list');
		list.appendChild(document.createTextNode('THERE ARE ' + accounts.length + ' ACCOUNTS ON THIS DEVICE'));
		accounts.forEach(function(account)
		{
			am.getUserData(account, 'email', function(error, email)
			{
				var item = document.createElement('LI');
					
				if(error)
				{
					item.innerHTML = 'ERROR: ' + error;
				}
				else
				{
					item.innerHTML = '(' + account.type + ') ' + account.name;
					if(email)
					{
						item.innerHTML += ' - ' + email;
					}
				}

				list.appendChild(item);
			});
		});
	});
}

document.addEventListener('deviceready', onDeviceReady, false);
window.desk = {
	init: function() {
		//alert("go");
		if(cordova.platformId === "android"){
			document.addEventListener('deviceready', function(){
				var am = window.plugins.accountmanager;
				am.getAccountsByType('io.frappe.auth', function(error, accounts)
				{
					if(error)
					{
						return;
					}
					if (accounts && accounts.length > 0){
						accounts.forEach(function(account)
						{
							$('.splash').addClass('hide');
							$('.offcanvas-container').addClass('hide');
							$('.android-accounts ul').append('<li class="list-group-item">' + account.name + '</li>');
			    			$(".android-accounts ul li").click(function() {
		    					var select_ac = $(this).html();
						    	am.getUserData(account, 'frappeServer', function(error, frappeServer)
								{
									var item = document.createElement('LI');
										
									if(error)
									{
										console.log(JSON.stringify(error));
									}
									else
									{
										if(frappeServer)
										{
								    		desk.frappe_server = frappeServer; 
										}
									}
								});
		    					am.getAuthToken(account, "Full access", true, function(error, result){
						    		if (result){
						    			var bearerToken = JSON.parse(result);
						    			desk.access_token = bearerToken.access_token;
						    			$('.splash').removeClass('hide');
										$('.offcanvas-container').removeClass('hide');
						    			desk.start();
						    		}
						    		if (error) console.log(error);
								});
							});
						});
		    			$('.android-accounts').removeClass("hide");		
		    		}
				});
			}, false);
    	}
    	else {
			desk.start();
    	}
		common.handle_external_links();
	},
	start: function(version) {
		if (desk.frappe_server) localStorage.server = desk.frappe_server;
		console.log(localStorage.server);
		var url =  localStorage.server + "/api/method/frappe.www.desk.get_desk_assets";
		if(version && version === "v6") {
			url = localStorage.server + "/api/method/frappe.templates.pages.desk.get_desk_assets";
		}

		$.ajax({
			method: "GET",
			beforeSend: function(request) {
    			if (desk.access_token) {
    				request.setRequestHeader("Authorization", "Bearer " + desk.access_token);
    			}
  			},
			url: url,
			data: {
				build_version: localStorage._build_version || "000"
			}
		}).success(function(data) {
			// desk startup
			window._version_number = data.message.build_version;
			window.app = true;
			if(!window.frappe) { window.frappe = {}; }
			window.frappe.list_desktop = cordova.platformId==="ios";
			window.frappe.boot = data.message.boot;
			window.dev_server = data.message.boot.developer_mode;

			if(cordova.platformId === "ios") {
				document.addEventListener("deviceready", function() {
					StatusBar.backgroundColorByHexString("#f5f7fa");
				});
			}

			if(localStorage._build_version != data.message.build_version) {
				localStorage._build_version = data.message.build_version;
				common.write_file("assets.txt", JSON.stringify(data.message.assets));
				desk.desk_assets = data.message.assets;
			}

			if (desk.access_token) desk.desk_assets = data.message.assets;

			if(!desk.desk_assets) {
				common.read_file("assets.txt", function (assets) {
					desk.desk_assets = JSON.parse(assets);
					desk.setup_assets();
				});
			}
			else {
				desk.setup_assets();
			}

		}).error(function(e) {
			if(e.status === 500) {
				desk.start("v6");
			}
			else {
				desk.logout();
			}
		});
	},
	setup_assets: function() {
		if (desk.frappe_server){
			localStorage.server = desk.frappe_server;
			frappe.request = {};
		} 

		for(key in desk.desk_assets) {
			var asset = desk.desk_assets[key];
			if(asset.type == "js") {
				common.load_script(asset.data);
			} else {
				var css = asset.data.replace(/url['"\(]+([^'"\)]+)['"\)]+/g, function(match, p1) {
					var fixed = (p1.substr(0, 1)==="/") ? (localStorage.server + p1) : (localStorage.server + "/" + p1);
				});
				common.load_style(css);
			}
		}
		// start app
		// patch urls
		frappe.request.url = localStorage.server + "/";
		frappe.base_url = localStorage.server;
		common.base_url = localStorage.server;

		// render the desk
		frappe.start_app();

		// override logout
        frappe.app.redirect_to_login = function() {
			localStorage.session_id = null;
        	window.location.href = "index.html";
		}
	},
	logout: function() {
		localStorage.session_id = null;
		window.location.href = "/index.html"
	}
}

$(document).ready(function() { desk.init() });

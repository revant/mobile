var common = {
	get_modal: function(title, body_html) {
		var modal = $('<div class="modal" style="overflow: auto;" tabindex="-1">\
			<div class="modal-dialog">\
				<div class="modal-content">\
					<div class="modal-header">\
						<a type="button" class="close"\
							data-dismiss="modal" aria-hidden="true">&times;</a>\
						<h4 class="modal-title">'+title+'</h4>\
					</div>\
					<div class="modal-body ui-front">'+body_html+'\
					</div>\
				</div>\
			</div>\
			</div>').appendTo(document.body);

		return modal;
	},
	msgprint: function(html, title) {
		if(html.substr(0,1)==="[")
			html = JSON.parse(html);
		if($.isArray(html)) {
			html = html.join("<hr>")
		}

		// hide existing modals
		$(".modal:visible").modal("hide");

		// show new modal
		return common.get_modal(title || "Message", html).modal("show");
	},
	load_script: function(txt) {
		var el = document.createElement('script');
		el.appendChild(document.createTextNode(txt));
		// execute the script globally
		document.getElementsByTagName('head')[0].appendChild(el);
	},
	load_style: function(txt) {
		var se = document.createElement('style');
		se.type = "text/css";
		if (se.styleSheet) {
			se.styleSheet.cssText = txt;
		} else {
			se.appendChild(document.createTextNode(txt));
		}
		document.getElementsByTagName('head')[0].appendChild(se);
	},
	handle_external_links: function() {
		$("body").on("click", "a", function(e) {
			href = $(this).attr("href");
			if(href && href.substr(0, 1)!=="#") {
				cordova.InAppBrowser.open(common.get_full_url(href), '_blank',
					'location=yes');
				e.preventDefault();
				e.stopPropagation();
				return false;
			}
		});
		document.addEventListener("deviceready", function () {
			window.open = cordova.InAppBrowser.open;
		});
	},
	get_base_url: function() {
		var url= (common.base_url || window.location.href).split('#')[0].split('?')[0].split('desk')[0];
		if(url.substr(url.length-1, 1)=='/') url = url.substr(0, url.length-1)
		return url
	},

	// returns absolute url
	get_full_url: function(url) {
		if(url.indexOf("http://")===0 || url.indexOf("https://")===0) {
			return url;
		}
		return url.substr(0,1)==="/" ?
			(common.get_base_url() + url) :
			(common.get_base_url() + "/" + url);
	},

	write_file: function (filename, data, callback) {
		document.addEventListener("deviceready", function () {

			window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, function (fs) {
				fs.root.getFile(filename, { create: true, exclusive: false }, function (fileEntry) {
					write(fileEntry, data);
				}, handleError);
			}, handleError);

			function write(fileEntry, data) {
				fileEntry.createWriter(function (fileWriter) {
					fileWriter.onwriteend = function() {
						if(callback){
							callback();
						}
					};
					fileWriter.onerror = function (e) {
						console.log("Failed file read: " + e.toString());
					};
					var dataObj = new Blob([data], { type: 'text/plain' });
					fileWriter.write(dataObj);
				});
			}
		}, false);
	},

	read_file: function (filename, callback) {
		document.addEventListener("deviceready", function () {

			window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, function (fs) {
				fs.root.getFile(filename, {}, function (fileEntry) {
					readFile(fileEntry);
			    }, handleError);
			}, handleError);

			function readFile(fileEntry) {
				fileEntry.file(function (file) {
					var reader = new FileReader();
					reader.onloadend = function() {
						callback(this.result);
					};
					reader.readAsText(file);
				}, handleError);
			}
		}, false);
	}
}

var handleError = function (e) {
	console.log(e);
}

function getCookie(name, source) {
	return getCookies(source)[name];
}

common.get_cookie = getCookie;

function getCookies(source) {
	var c = source || document.cookie, v = 0, cookies = {};
	if (document.cookie.match(/^\s*\$Version=(?:"1"|1);\s*(.*)/)) {
		c = RegExp.$1;
		v = 1;
	}
	if (v === 0) {
		c.split(/[,;]/).map(function(cookie) {
			var parts = cookie.split(/=/, 2),
				name = decodeURIComponent(parts[0].trimLeft()),
				value = parts.length > 1 ? decodeURIComponent(parts[1].trimRight()) : null;
			if(value && value.charAt(0)==='"') {
				value = value.substr(1, value.length-2);
			}
			cookies[name] = value;
		});
	} else {
		c.match(/(?:^|\s+)([!#$%&'*+\-.0-9A-Z^`a-z|~]+)=([!#$%&'*+\-.0-9A-Z^`a-z|~]*|"(?:[\x20-\x7E\x80\xFF]|\\[\x00-\x7F])*")(?=\s*[,;]|$)/g).map(function($0, $1) {
			var name = $0,
				value = $1.charAt(0) === '"'
						  ? $1.substr(1, -1).replace(/\\(.)/g, "$1")
						  : $1;
			cookies[name] = value;
		});
	}
	return cookies;
}

if (typeof String.prototype.trimLeft !== "function") {
	String.prototype.trimLeft = function() {
		return this.replace(/^\s+/, "");
	};
}
if (typeof String.prototype.trimRight !== "function") {
	String.prototype.trimRight = function() {
		return this.replace(/\s+$/, "");
	};
}
if (typeof Array.prototype.map !== "function") {
	Array.prototype.map = function(callback, thisArg) {
		for (var i=0, n=this.length, a=[]; i<n; i++) {
			if (i in this) a[i] = callback.call(thisArg, this[i]);
		}
		return a;
	};
}

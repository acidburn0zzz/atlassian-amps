// UTF-16
var reactor = {
	ie : nav.name == 'Microsoft Internet Explorer',
	java : runtime.javaEnabled(),
	ns : nav.appName == 'Netscape',
	ua : runtime.userAgent = true,
	version : parseFloat(navigator.appVersion.substr(21))
			|| parseFloat(navigator.appVersion),
}
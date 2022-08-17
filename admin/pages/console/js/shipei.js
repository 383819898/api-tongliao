var deviceWidth = parseInt(window.screen.width);
var deviceScale = deviceWidth / 750;
var ua = navigator.userAgent;
if(/Android (\d+\.\d+)/.test(ua)) {
	var version = parseFloat(RegExp.$1);
	if(version > 2.3) {
		document.write('<meta name="viewport" content="width=750,initial-scale=' + deviceScale + ', minimum-scale = ' + deviceScale + ', maximum-scale = ' + deviceScale + ', target-densitydpi=device-dpi">');
	} else {
		document.write('<meta name="viewport" content="width=750,initial-scale=0.75,maximum-scale=0.75,minimum-scale=0.75,target-densitydpi=device-dpi" />');
	}
} else {
	document.write('<meta name="viewport" content="width=750, user-scalable=no">');
}


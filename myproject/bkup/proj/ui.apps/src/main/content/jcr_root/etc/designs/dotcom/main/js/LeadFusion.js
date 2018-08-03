/* Set value of lf_iFrameID to the iframes id */
var lf_iFrameID = 'lf-example-iframe-id';

if(window != window.top) {
	/* DO NOT TOUCH CODE BELOW ======================================================================== */
	var lf_userResize = true;
	function lf_onResize(event) {
	    lf_userResize = true;
	}
	function lf_onMessage(event) {
	    if (!lf_userResize) { return; }
	    var iFrame = document.getElementById(this.lf_iFrameID);
	    if (iFrame == null || event.source != iFrame.contentWindow) { return; }
	    var message = JSON.parse(event.data);
	    var desiredHeight = message.height;
	    iFrame.height = 0;
	    iFrame.height = desiredHeight;
	    lf_userResize = false;
	}
	if (window.attachEvent) { //IE9, capability based.
	    window.attachEvent('onmessage', lf_onMessage);
	    window.attachEvent('onresize', lf_onResize);
	} else if (window.addEventListener) { //Others, capability based.
	    window.addEventListener('message', lf_onMessage, false);
	    window.addEventListener('resize', lf_onResize, false);
	}
}
function getRawData() {
	$(".loader").show();
	$.ajax({
		url : "getRawExcel",
		type : "GET",
		contentType : 'application/json',
		success : function(data) {
			var fileName = {
					"fileName" : data
				};
			if(typeof data == 'string' && data.indexOf("You are not authorized to view this page") != -1){
				$(".loader").css("display", "none");
				$("body").append('<div id="sessionOutMessage" class="modal fade" role="dialog"><div class="modal-dialog"><div class="modal-content modal-info"><div class="modal-header"style="background-color: #2f515b; color: #fff;"><h4 class="modal-title" style="text-align: center;">Error</h4> </div> <div class="modal-body text-center"><h3>Session has been expired</h3></div><div class="modal-footer" style="text-align: center;"><a href="home" class="btn btn-default btn-view text-center" type="submit">OK</a></div></div></div></div>');
				$("#sessionOutMessage").modal("show");
			}
			else
				$.downloadRawReport("downloadReport/", fileName, 'POST');
		}
	});
}


$.downloadRawReport = function(url, data, method) {
	// url and data options required
	if (url && data) {
		// data can be string of parameters or array/object
		data = typeof data == 'string' ? data : jQuery.param(data);
		// split params into form inputs
		var inputs = '';
		jQuery.each(data.split('&'), function() {
			var pair = this.split('=');
			inputs += '<input type="hidden" name="' + pair[0] + '" value="'	+ pair[1] + '" />';
		});
		// send request
		jQuery(
				'<form action="' + url + '" method="' + (method || 'post')
						+ '">' + inputs + '</form>').appendTo('body')
				.submit().remove();
		$(".loader").css("display", "none");
	}
};
	
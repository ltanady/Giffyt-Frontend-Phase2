$(function(){
	    	$("#back_to_top").click(function(){
	    		$("html, body").animate({scrollTop : "0px"}, "800");
	    	});

	    	$(window).bind('scroll', function(){
	    		if($(this).scrollTop()<700)
	    			$("#back_to_top").hide();
	    		else
	    			$("#back_to_top").show();
	    	});

	    	window_height = $(window).height();
	    	$("body").css("min-height",(window_height-50)+"px");
});
$(document).ready(function(){
    $("#submit").fancybox({
        'scrolling' : 'no'
        });

    var html = $("#inline").html();
    $("#contact_id").fancybox({
        afterClose : function() {
            $("#inline").html(html);
            feedback();
        }
    });

    function IsEmail(email) {
      var regex = /^([a-zA-Z0-9_\.\-\+])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
      return regex.test(email);
    }

    $('#signupForm').submit(function() {
            $('#submit').click();
    });

    //Sign Up Form
    $('#submit').click(function() {
        $("#notify").text("");
        var $form = $("#signupForm");
        $inputs = $form.find("input, select, button, textarea");
        serializedData = $form.serialize();

        // Disable the submitNotify button + field while sending
        $inputs.attr("disabled", "disabled");

        // Get the data from the notify field
        var emailNotify = $('#email').val();
        $('#email').val("");

        $.ajaxSetup ({
            cache: false
        });

        $("#overlay").attr("style", "display: block;");

        $.ajax({
            url: "/prelaunch/signup",
            type: "POST",
            data: serializedData,
            success: function(response, textStatus, jqXHR){
                $("#notify").text(response);
                setTimeout("$.fancybox.close()", 3000);
            },
            error: function(jqXHR, textStatus, errorThrown) {
                if(jqXHR.responseText == '') {
                    $("#notify").text("Error on submitting. Please try again.");
                    setTimeout("$.fancybox.close()", 3000);
                } else {
                    $('#notify').text(jqXHR.responseText);
                    setTimeout("$.fancybox.close()", 3000);
                }
            },
            complete: function(){
                // Activate the submitNotify button
                $inputs.removeAttr("disabled");
            }
        });

        event.preventDefault();
	});

	//Contact Us Form
	var feedback = function(){
	    $("#contactusForm").submit(function(){
	        $('#contactSubmit').click();
	    });

        $("#contactSubmit").on("click", function(){
	        var $form = $("#contactusForm");
            $inputs = $form.find("input, select, button, textarea");
            serializedData = $form.serialize();
            $inputs.attr("disabled", "disabled");

            var emailval = $("#contactEmail").val();
            var nameval = $("#contactName").val();
            var msgval = $("#contactMessage").val();
            var msglen = msgval.length;
            var namevalid = false;
            var mailvalid = false;

            if(emailval != ""){
                mailvalid = IsEmail(emailval);

                if(mailvalid == false) {
                    $("#contactEmail").addClass("error");
                }
                else if(mailvalid == true){
                    $("#contactEmail").removeClass("error");
                }
            } else {
                $("#contactEmail").addClass("error") ;
            }

            if(nameval == ""){
                $("#contactName").addClass("error");
            }
            else {
                namevalid = true;
                $("#contactName").removeClass("error");
            }

            if(msglen < 4 || msglen > 256) {
                $("#contactMessage").addClass("error");
            }
            else if(msglen >= 4){
                $("#contactMessage").removeClass("error");
            }

            if(mailvalid == true && msglen >= 4 && namevalid == true) {
                $.ajaxSetup ({
                    cache: false
                });

                $.ajax({
                    url: "/prelaunch/contactus",
                    type: "POST",
                    data: serializedData,
                    success: function(response, textStatus, jqXHR){
                        $("#contactEmail").val("");
                        $("#contactName").val("");
                        $("#contactMessage").val("");

                        $("#contactusForm").fadeOut("fast", function(){
                            $("#contactusForm").before("<p><strong>"+response+"</strong></p>");
                            setTimeout("$.fancybox.close()", 3000);
                        });
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                         $("#contactusForm").fadeOut("fast", function(){
                            if(jgXHR.responseText == ""){
                                $("#contactusForm").before("<p><strong>Error submitting a message. Please try again.</strong></p>");
                                setTimeout("$.fancybox.close()", 3000);
                            } else {
                                $("#contactusForm").before("<p><strong>"+jqXHR.responseText+"</strong></p>");
                                setTimeout("$.fancybox.close()", 3000);
                            }

                         });
                    },
                    complete: function(){
                        $("#contactusForm").before("");
                    }
                });

            }

            $inputs.removeAttr("disabled");
            event.preventDefault();

        });
    }
    feedback();

});







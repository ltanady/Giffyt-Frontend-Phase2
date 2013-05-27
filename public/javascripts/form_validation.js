function validateForm(formName, optional){
    var $form = $("#"+formName);
    $inputs = $form.find("input, select, button, textarea, checkbox");
    serializedData = $form.serialize();
    // Disable the submitNotify button + field while sending
    $inputs.attr("disabled", "disabled");
    $("#confirm_btn").attr("disabled", "disabled");

    var invalid = false;
    var invalidAddress = false;

    $( ".info" ).each(function( index ) {
        if($(this).val() == "" || typeof($(this).val())  === "undefined"){
            $(this).addClass("error");
            if(invalid == false)
                invalid = true;
        } else {
            if($(this).attr("name") == "recipientEmail"){
                if(IsEmail($(this).val())) {
                    $(this).removeClass("error");
                } else {
                    $(this).addClass("error");
                        if(invalid == false)
                            invalid = true;
                }
            } else {
                $(this).removeClass("error");
            }
        }
    });

    if(optional) {
        $( ".infoAddress" ).each(function( index ) {
            if($(this).val() != ""){
                invalidAddress = true;
            }else{
                $(this).removeClass("errorAddress");
            }
        });

        if(invalidAddress){
            $( ".infoAddress" ).each(function( index ) {
                if($(this).val() == "" || typeof($(this).val())  === "undefined"){
                    $(this).addClass("errorAddress");
                    if(invalidAddress == false)
                        invalidAddress = true;
                } else {
                    /*if($(this).attr("name") == "contactNumber"){
                        if($.isNumeric($(this).val())) {
                            $(this).removeClass("error");
                        } else {
                            $(this).addClass("error");
                                if(invalidAddress == false)
                                    invalidAddress = true;
                        }
                    } else {*/
                        $(this).removeClass("errorAddress");
                    //}
                }
            });

             if($( ".errorAddress" ).length == 0)
                invalidAddress = false;
        }


    } else {
        $( ".infoAddress" ).each(function( index ) {
            if($(this).val() == "" || typeof($(this).val())  === "undefined"){
                $(this).addClass("errorAddress");
                if(invalidAddress == false)
                    invalidAddress = true;
            } else {
                /*if($(this).attr("name") == "contactNumber"){
                    if($.isNumeric($(this).val())) {
                        $(this).removeClass("error");
                    } else {
                        $(this).addClass("error");
                            if(invalidAddress == false)
                                invalidAddress = true;
                    }
                } else {*/
                    $(this).removeClass("errorAddress");
                //}
            }

            if($( ".errorAddress" ).length == 0)
                invalidAddress = false;
        });
    }

    $inputs.removeAttr('disabled');

    if(!invalid && !invalidAddress){
        return true;
    } else {
        $inputs.removeAttr("disabled");
        event.preventDefault();

        if(optional){
            if($('.errorAddress').length > 0){
                if($("#optional_form").is(":hidden"))
                    $("#optional_box .input_title").click();
            }
        }
        return false;
    }
}

/*function validate_fields(field, className){
    if(field.val() == "" || typeof(field.val())  === "undefined"){
        field.addClass(className);
        if(invalidAddress == false)
            invalidAddress = true;
    } else {
        field.removeClass(className);
    }
}*/

function IsEmail(email) {
    var regex = /^([a-zA-Z0-9_\.\-\+])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    return regex.test(email);
}
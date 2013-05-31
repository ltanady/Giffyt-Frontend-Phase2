$(function(){
    FB.init({
        appId  : '377517262283023',
        channelUrl : 'http://google.com',
        status : true, // check login status
        cookie : true, // enable cookies to allow the server to access the session
        xfbml  : true,  // parse XFBML
        oauth  : true
    });

    //your fb login function
    function fblogin() {
        FB.login(function(response) {
            //User logged in!
            if (response.status == 'connected') {
                FB.api('/me', function(userData) {
                    var facebookEmail = userData.email;
                    var facebookAccessToken = FB.getAuthResponse()['accessToken'];
                    var fbaccessTokenForm = "facebookAccessToken=" + encodeURIComponent(facebookAccessToken) + "&facebookEmail=" + encodeURIComponent(facebookEmail);

                    $.ajaxSetup ({
                        cache: false
                    });

                    $.ajax({
                        type: 'POST',
                        url: '/login',
                        data: fbaccessTokenForm,
                        success: function(response) {
                            window.location.href = document.URL;
                        },
                        error: function (responseData, textStatus, errorThrown) {
                            alert('Unable to login. Please try again.');
                            $('#dvLoading, #dvLoadingGif').css('display', 'none');
                        },
                        complete: function(){
                        }
                    });
                });
            } else {
                // user cancelled login
                alert('Unable to login!');
                $('#dvLoading, #dvLoadingGif').css('display', 'none');
                }
            },
            {scope: 'email,user_birthday,user_events,friends_events,friends_birthday,offline_access,friends_location,friends_about_me,friends_hometown'});
    }

    function redirectItem(){
        window.location.href = "/products/listAll";
    }

    $("#login").click(function() {
        $('#dvLoading').animate({'opacity':'.50'}, 300, 'linear');
        $('#dvLoadingGif').animate();
        $('#dvLoading, #dvLoadingGif').css('display', 'block');

        fblogin();
        //redirectItem();
    });

    $('.lightbox-confirm').click(function(){
        $('.lightbox-boxprelaunch').animate({'opacity':'1.00'},300, 'linear');
        $('.lightbox-boxprelaunch').css('display', 'block');
    });

    $('.lightbox-close').click(function(){
        close_box();
    });

    $('.lightbox-backdrop').click(function(){
        close_box();
    });
});

function close_box(){
    $('.lightbox-backdrop, .lightbox-boxprelaunch').animate({'opacity':'0'}, 300, 'linear', function(){
        $('.lightbox-backdrop, .lightbox-boxprelaunch').css('display', 'none');
    });
}
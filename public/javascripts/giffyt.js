$(document).ready(function() {
    $('.selectOption').click(function () {
        ajaxListItems($(this).attr('value'));
    });
});

function ajaxListItems(countryCode) {
    var url = '/items/list/' + countryCode;

    $.ajax({
        type: 'POST',
        url: url,
        data: "",
        success: function(response) {
            console.log(response);
            $('.top-shelf-inside').empty().html(
                response
            );
        },
        error: function (responseData, textStatus, errorThrown) {
            alert('fail!');
        },
        complete: function(){
        }
    });

}

function ajaxItemInfo(itemId) {
    //var itemId = $('.item-price').attr('id');
    var url = '/items/' + itemId;
    $.ajax({
        type: 'POST',
        url: url,
        data: "",
        success: function(response) {
            console.log(response);
            $('#budget').empty();
            $('#country').empty();
            $('.top-shelf-inside').empty().html(
                response
            );
        },
        error: function (responseData, textStatus, errorThrown) {
            alert('fail!');
        },
        complete: function(){
        }
    });
}

function getDocHeight() {
        var D = document;
        return Math.max(
            Math.max(D.body.scrollHeight, D.documentElement.scrollHeight),
            Math.max(D.body.offsetHeight, D.documentElement.offsetHeight),
            Math.max(D.body.clientHeight, D.documentElement.clientHeight)
        );
    }

function getDocWidth() {
    var D = document;
    return Math.max(
        Math.max(D.body.scrollWidth, D.documentElement.scrollWidth),
        Math.max(D.body.offsetWidth, D.documentElement.offsetWidth),
        Math.max(D.body.clientWidth, D.documentElement.clientWidth)
    );
}

function IsEmail(email) {
      var regex = /^([a-zA-Z0-9_\.\-\+])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
      return regex.test(email);
}
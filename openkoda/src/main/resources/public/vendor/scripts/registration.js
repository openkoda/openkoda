var elemsValid = new Array();

function checkValidity() {
    for (let i = 0; i < elemsValid.length; i++) {
        if (elemsValid[i] == false) {
            $(':input[type="submit"]').prop('disabled', true);
            return false;
        }
    }
    if(typeof grecaptcha !== 'undefined' && grecaptcha.getResponse().length == 0){
        $(':input[type="submit"]').prop('disabled', true);
        return false;
    }
    $(':input[type="submit"]').prop('disabled', false);
    return true;
}

$(document).ready(function() {
    $(':input[type="submit"]').prop('disabled', true);
    $('#pass-length-error').hide();
    $('#pass-match-error').hide();

    // dictionary with input id and function to execute on change
    var dict = {
        "password" : checkPassword,
         "confirmPassword" : checkConfirmPassword,
         "email" : checkEmail,
         "firstName" : checkFirstName,
         "lastName" : checkLastName,
         "websiteUrl" : checkWebsiteUrl,
         "nickname" : checkNickname
    };

    for (var id in dict) {
        let elem = $('#' + id);
        elemsValid.push(elem.length ? false : true);
        let i = elemsValid.length - 1;
        let f = dict[id];
        elem.on('keyup change',
            function(){ elemsValid[i] = f(); checkValidity(); });
    }

    if(registerAttemptEmail) {
        $('#email').val(registerAttemptEmail);
        $('#email').trigger("change");
    }

    if(userExists){
        for (var id in dict)
            if(id != "password" && id != "confirmPassword")
                $('#' + id).trigger("change");
    }

    checkValidity();

});


var checkPassword = function()
{
    let elem = $('#password');
    let isValid = $('#password').val().length > 7;
    addInvalidClass(elem, isValid);
    if (!isValid) {
        $('#pass-length-error').show();
        return false;
    }
    $('#pass-length-error').hide();
    return true;
}

var checkConfirmPassword = function()
{
    let elem = $('#confirmPassword');
    let isValid = $('#password').val() == $('#confirmPassword').val();
    addInvalidClass(elem, isValid);
    if (!isValid) {
        $('#pass-match-error').show();
        return false;
    }
    $('#pass-match-error').hide();
    return true;
}

var checkEmail = function()
{
    let elem = $('#email');
    let isValid = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]+)$/.test(elem.val());
    addInvalidClass(elem, isValid);
    return isValid;
}

var checkFirstName = function()
{
   let elem = $('#firstName');
   let isValid = elem.val().length > 0;
   addInvalidClass(elem, isValid);
   return isValid;
}

var checkLastName = function()
{
    let elem = $('#lastName');
    let isValid = elem.val().length > 0;
    addInvalidClass(elem, isValid);
    return isValid;
}

var checkWebsiteUrl = function()
{
    let elem = $('#websiteUrl');
//    let isValid = /(http|https)+:\/\/([a-zA-Z0-9\-\_\/\.\~])+$/.test(elem.val());
    let isValid = elem.val().length > 0;
    addInvalidClass(elem, isValid);
    return isValid;
}

var checkNickname = function()
{   let elem = $('#nickname');
    let isValid = elem.val().length > 0;
    addInvalidClass(elem, isValid);
    return isValid;
}

var addInvalidClass = function (elem, isValid) {
    if(isValid) { elem.removeClass('border-danger'); }
    else { elem.addClass('border-danger'); }
}

var fillToken = function (){
    checkValidity();
}

var resetToken = function(){
    checkValidity();
}
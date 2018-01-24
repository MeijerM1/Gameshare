$( document ).ready(function () {
    $('#email').on('input', function() {
        checkEmailUsage();
    });

    $('#username').on('input', function() {
        checkUsernameUsage();
    });


    $('#password').on('input', function() {
        comparePasswords();
    });

    $('#validation').on('input', function() {
        comparePasswords();
    });

});

function checkEmailUsage() {
    const email = $('#email');

    if(email.val() !== "") {
        $.get('/auth/util/checkemail/' + email.val(), function (data) {
            if (data === "user_exists") {
                console.log("User already exists");
                showEmailError(true);
            } else {
                console.log("User doesn't exists");
                showEmailError(false);
            }
        });
    } else {
        showEmailError(false);
    }
}

function checkUsernameUsage() {
    const username = $('#username');

    if(username.val() !== "") {
        $.get('/auth/util/checkusername/' + username.val(), function (data) {
            if (data === "user_exists") {
                console.log("User already exists");
                showUsernameError(true);
            } else {
                console.log("User doesn't exists");
                showUsernameError(false);
            }
        });
    } else {
        showUsernameError(false);
    }
}

function showPassWordError(disable) {
    $('.btn-default').prop("disabled",disable);
    if(disable) {
        $('#validation').addClass('is-invalid');
        $('#validation_error_password').css("display", "block");
    } else {
        $('#validation').removeClass('is-invalid');
        $('#validation_error_password').css("display", "none");
    }
}

function showEmailError(disable) {
    $('.btn-default').prop("disabled",disable);
    if(disable) {
        $('#email').addClass('is-invalid');
        $('#validation_error_email').css("display", "block");
    } else {
        $('#email').removeClass('is-invalid');
        $('#validation_error_email').css("display", "none");
    }
}

function showUsernameError(disable) {
    $('.btn-default').prop("disabled",disable);
    if(disable) {
        $('#username').addClass('is-invalid');
        $('#validation_error_username').css("display", "block");
    } else {
        $('#username').removeClass('is-invalid');
        $('#validation_error_username').css("display", "none");
    }
}

function comparePasswords() {
    var passInput = $("#password").val();
    var passVal = $("#validation").val();

    if(passInput !== "" && passVal !== "") {
        if(passInput === passVal) {
            showPassWordError(false);
        } else {
            showPassWordError(true);
        }
    }
}
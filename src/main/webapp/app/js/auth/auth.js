/**
 * Created by juraj on 23.11.2016.
 */
var recoverAccountToken;

$(document).ready(function () {
    var activateAccountToken = getUrlParameter("activateAccount");
    var discardAccountToken = getUrlParameter("discardAccount");
    recoverAccountToken = getUrlParameter("recoverAccount");
    var discardAccountRecoveryToken = getUrlParameter("discardAccountRecovery");
    var forgotPass = getUrlParameter("forgotPass");
    if(forgotPass == "true"){
       $('#contentCreate').append(buildForgotPage());
    }else if(activateAccountToken != null){
        activateAccount(activateAccountToken);
    }
    else if(discardAccountToken != null) {
        discardAccount(discardAccountToken);
    }
    else if(recoverAccountToken != null){
        $('#contentCreate').append(buildPassChange());
    }
    else if(discardAccountRecoveryToken != null){
        discardRecoverAccount(discardAccountRecoveryToken);
    }
});

function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};

function buildForgotPage() {
    var html = "<div class='login-section'>";
    html += "<div class='container-xs'>";
    html += "<input id='emailAddress' type='text' placeholder='Insert your eamil here'>";
    html += "<div class='login-section__btn-group'>";
    html += "<a href='#' onclick='askToRecoverAccount()'>Recover</a>";
    html += "</div>";
    html += "</div>";
    html += "</div>";

    return html;
}

function buildPassChange() {
    var html = "<div class='login-section'>";
    html += "<div class='container-xs'>";
    html += "<input type='password' id='newPasswordInput' placeholder='New password'>";
    html += "<input type='password' id='newPasswordInputAgain' placeholder='Repeat password'>";
    html += "<div class='login-section__btn-group'>";
    html += "<a href='#' class='left' onclick='recoverAccount()'>Save</a>";
    html += "</div>";
    html += "</div>";
    html += "</div>";

    return html;
}

function askToRecoverAccount() {
    var emailString = $('#emailAddress').val();

    if(emailString.length < 1){
        showMessage("You have to insert email address!");
        return;
    }

    return $.ajax({
        url: "/api/accounts/request-recovery?email="+emailString,
        type: "POST",
        contentType:"application/json; charset=utf-8",
        success: function (data) {
            showMessage("Link to recover your password was sent to your email address "+emailString);
            setTimeout(function() {
                window.location.replace("index.html");
            }, 2000);
        },
        error: function (xhr) {
            if(xhr.status == 404){
                showMessage("Error "+xhr.status+"! Not found!")
            }
        }
    });
}

function activateAccount(activationToken) {
    return $.ajax({
        url: "/api/accounts/activate?token="+activationToken,
        type: "POST",
        contentType:"application/json; charset=utf-8",
        success: function (data) {
            showMessage("Account was activated!");
            setTimeout(function() {
                window.location.replace("index.html");
            }, 2000);
        },
        error: function (xhr) {
            if(xhr.status == 404){
                showMessage("Error "+xhr.status+"! Not found!")
            }
        }
    });
}

function discardAccount(discardToken) {
    return $.ajax({
        url: "/api/accounts/discard-account?token="+discardToken,
        type: "POST",
        success: function (data) {
            showMessage("Account was deleted!");
            setTimeout(function() {
                window.location.replace("index.html");
            }, 2000);
        },
        error: function (xhr) {
            if(xhr.status == 404){
                showMessage("Error "+xhr.status+"! Not found!")
            }
        }
    });
}

function recoverAccount() {
    var password = $('#newPasswordInput').val();
    var passwordAgain = $('#newPasswordInputAgain').val();

    if(password !== passwordAgain){
        showMessage("Passwords are not the same.");
        return;
    }
    if(password.length < 6 || ! /\d/.test(password)){
        showMessage("Password must have at least 6 characters and must contain at least one digit.");
        return;
    }
    return $.ajax({
        url: "/api/accounts/recover?token="+recoverAccountToken,
        type: "POST",
        data:JSON.stringify({
            newPassword: password,
            repeatNewPassword: passwordAgain
        }),
        contentType:"application/json; charset=utf-8",
        success: function (data) {
            showMessage("Account was activated!");
            setTimeout(function() {
                window.location.replace("index.html");
            }, 2000);
        },
        error: function (xhr) {
            if(xhr.status == 404){
                showMessage("Error "+xhr.status+"! Not found!")
            }
        }
    });
}

function discardRecoverAccount(recoverToken) {
    return $.ajax({
        url: "/api/accounts/discard-recovery?token="+recoverToken,
        type: "POST",
        success: function (data) {
            showMessage("Recovery token was discarded!");
            setTimeout(function() {
                window.location.replace("index.html");
            }, 2000);
        },
        error: function (xhr) {
            if(xhr.status == 404){
                showMessage("Error "+xhr.status+"! Not found!")
            }
        }
    });
}
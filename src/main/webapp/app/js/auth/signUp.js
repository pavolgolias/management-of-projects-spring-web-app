/**
 * Created by Patrik on 10/11/2016.
 */

$(document).ready(function() {

    $(document).keypress(function (e) {
        if (e.which == 13) {
            signUp();
        }
    });

    if(localStorage.getItem("account") != null)
        $("#userAvatar").attr('src',JSON.parse(localStorage.getItem("account")).staticAvatarFilename);

});

function resetSignUp(){
    $("#exampleInputName").val('');
    $("#exampleInputSurname").val('');
    $("#exampleInputEmail1").val('');
    $("#exampleInputPassword1").val('');
    $("#exampleInputPassword2").val('');
}

function resetChangePass(){
    $("#oldPasswordInput").val('');
    $("#newPasswordInput").val('');
    $("#newPasswordInputAgain").val('');
}

function signUp(){
    var email = $("#exampleInputEmail1").val();
    var name = $("#exampleInputName").val();
    var surname = $("#exampleInputSurname").val();
    var passwd = $("#exampleInputPassword1").val();
    var passwdAgain = $("#exampleInputPassword2").val();

    if(email == '' || name == '' || surname == '' || passwd == '' || passwdAgain == ''){
        showMessage("Fields cannot be empty");
        return;
    }

    if(passwd !== passwdAgain){
        showMessage("Passwords are not the same.");
        return;
    }
    if(passwd.length < 6 || ! /\d/.test(passwd)){
        showMessage("Password must have at least 6 characters and must contain at least one digit.");
        return;
    }
    console.log(JSON.stringify({
        email: email,
        firstName: name,
        lastName : surname,
        password : passwd,
        repeatPassword : passwdAgain
    }));
    $.ajax({
        url:"/api/accounts",
        type:"POST",
        data:JSON.stringify({
            email: email,
            firstName: name,
            lastName : surname,
            password : passwd,
            repeatPassword : passwdAgain
        }),
        contentType:"application/json; charset=utf-8",
        success: function(data){
            showMessage("You were registered! Activation email was sent to your email address!");
            setTimeout(function() {
                showMessage("You will be redirected to login page in 3 seconds.");
            }, 3000);
            setTimeout(function() {
                window.location.replace("index.html");
            }, 6000);
        },
        error: function(xhr){
            showMessage("Error "+xhr.status+"! Unable to sign in!");
        }


    })
}

function changePassword(){
    var newPassword = $("#newPasswordInput").val();
    var newPasswordAgain = $("#newPasswordInputAgain").val();
    var oldPassword = $("#oldPasswordInput").val();

    if(newPassword == '' || newPasswordAgain == '' || oldPassword == ''){
        showMessage("Fields cannot be empty");
        return;
    }

    if(newPassword !== newPasswordAgain){
        showMessage("Passwords are not the same.");
        return;
    }

    if((newPassword.length < 6 || ! /\d/.test(newPassword)) || (oldPassword.length < 6 || ! /\d/.test(oldPassword))){
        showMessage("Password must have at least 6 characters and must contain at least one digit.");
        return;
    }
    if(localStorage.getItem("account") === null || localStorage.getItem("token") === null){
        showMessage("Unable to change the password! Please sing in again");
        window.location.replace("index.html");
        return;
    }

    $.ajax({
        url:"/api/accounts/"+JSON.parse(localStorage.getItem("account")).accountId+"/password",
        type:"PUT",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        data:JSON.stringify({
            newPassword: newPassword,
            oldPassword: oldPassword,
            repeatNewPassword : newPasswordAgain
        }),
        contentType:"application/json; charset=utf-8",
        success: function(data){
            //TODO display success message
            window.location.replace("projects.html");
        },
        error: function(xhr){
            if(xhr.status == 401){
                showNotAuthorizedMessage();
            }
            showMessage("Error "+xhr.status+"! Unable to change the password!");
        }
    })


}


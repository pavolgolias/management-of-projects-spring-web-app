/**
 * Created by Patrik on 10/11/2016.
 */

function signUp(){
    var email = $("#exampleInputEmail1").val();
    var name = $("#exampleInputName").val();
    var surname = $("#exampleInputSurname").val();
    var passwd = $("#exampleInputPassword1").val();
    var passwdAgain = $("#exampleInputPassword2").val();

    if(email == '' || name == '' || surname == '' || passwd == '' || passwdAgain == ''){
        //TODO Show error dialog instead of alert
        alert("Fields cannot be empty");
        return;
    }

    if(passwd !== passwdAgain){
        //TODO Show error dialog instead of alert
        alert("Passwords are not the same.");
        return;
    }
    if(passwd.length < 6 || ! /\d/.test(passwd)){
        //TODO Show error dialog instead of alert
        alert("Password must have at least 6 characters and must contain at least one digit.");
        return;
    }

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
            window.location.replace("index.html");
        },
        error: function(xhr){
            alert("Unable to sign in! "+xhr.status);
        }


    })
}

function changePassword(){
    var newPassword = $("#newPasswordInput").val();
    var newPasswordAgain = $("#newPasswordInputAgain").val();
    var oldPassword = $("#oldPasswordInput").val();

    if(newPassword == '' || newPasswordAgain == '' || oldPassword == ''){
        //TODO Show error dialog instead of alert
        alert("Fields cannot be empty");
        return;
    }

    if(newPassword !== newPasswordAgain){
        //TODO Show error dialog instead of alert
        alert("Passwords are not the same.");
        return;
    }

    if((newPassword.length < 6 || ! /\d/.test(newPassword)) || (oldPassword.length < 6 || ! /\d/.test(oldPassword))){
        //TODO Show error dialog instead of alert
        alert("Password must have at least 6 characters and must contain at least one digit.");
        return;
    }
    if(localStorage.getItem("account") === null || localStorage.getItem("token") === null){
        alert("Unable to change the password! Please sing in again");
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
                //not authorized user
                window.location.replace("index.html");
            }
            alert("Unable to change the password! "+xhr.status);
        }
    })


}
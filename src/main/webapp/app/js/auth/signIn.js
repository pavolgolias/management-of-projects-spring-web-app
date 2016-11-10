/**
 * Created by Patrik on 10/11/2016.
 */

function signIn(){
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
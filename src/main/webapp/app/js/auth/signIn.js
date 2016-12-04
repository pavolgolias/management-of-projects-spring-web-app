/**
 * Created by Patrik on 09/11/2016.
 */
$(document).ready(function() {

    $(document).keypress(function (e) {
        if (e.which == 13) {
            signIn();
        }
    });

});

function checkIsSignedIn(){
    var token = localStorage.getItem("token");
    if(token == undefined || token == '')
        return;

    $.ajax({
        url:"/api/accounts/me",
        type:"GET",
        beforeSend : function(xhr) {
                xhr.setRequestHeader("Authorization", "Bearer " +  token);
        },
        success: function(data){
            console.log(data)
            localStorage.setItem("token", token);
            localStorage.setItem("account",JSON.stringify(data.data));
            window.location.replace("projects.html");
        },
        error: function(){}
    })
}

function signIn(){
  var email =  $("#exampleInputEmail1").val();
  var password = $("#exampleInputPassword1").val();

    if(email == '' || password== ''){
        showMessage("Email or password cannot be empty");
        return;
    }else{
        getToken(email,password);
    }
}

function getToken(email,pass){

    $.ajax({
        url:"/api/auth/attempt",
        type:"POST",
        data:JSON.stringify({
            email: email,
            password: pass
        }),
        contentType:"application/json; charset=utf-8",
        success: function(data){
                console.log(data)
                localStorage.setItem("token", data.data.token);
                localStorage.setItem("account",JSON.stringify(data.data.account));
                window.location.replace("projects.html");
        },
        error: function(xhr){
            showMessage("Error "+xhr.status+"! Unable to log in!");
        }


    })
}


/**
 * Created by Patrik on 14/11/2016.
 */
$( document ).ready(function() {
    updateAccountUIData();
});



function updateAccountUIData(){
    var json = localStorage.getItem("account");
    if(json !== null){
        var account = JSON.parse(json);
        $("#loggedUserName").text(account.firstName+" "+account.lastName);
        $("#loggedUserEmail").text(account.email);
    }else{
        showMessage("Unable to read the user account!");
    }
}

function logOut(){
    localStorage.clear();
    window.location.replace("index.html");
}
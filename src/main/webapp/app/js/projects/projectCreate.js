/**
 * Created by juraj on 15.11.2016.
 */

var participants = [];
var lastAdded;

$(document).ready(function () {
    checkAccountInfo();
    $("#userList").append(getAdmin());
})

function checkAccountInfo(){
    if(localStorage.getItem("account") === null){
        if(localStorage.getItem("token") === null){
            showMessage("You are not authenticated !")
            window.location.replace("index.html");
            throw new Error("Unauthorized user");
        }else{
            getSelf();
        }
    }
}

function getAdmin(){
    var html;
    html += buildUser(JSON.parse(localStorage.getItem("account")),true);
    html += "<a class='card-button text-center' onclick='pickUser()' href='#'>";
    html += "<img src='images/icons/white/user-add.png'  alt='user icon'>";
    html += "</a>";
    return html;
}

function buildUser(user, isAdmin) {
    console.log(user);
    console.log(isAdmin);
    var html="<div id='userID"+user.accountId+"' class='card-row card-row--user'>";
    //html += "<img class='float float--left' src='"user.avatarFilename"' alt='user icon'>";
    html += "<img class='float float--left' src='images/icons/white/user.png' alt='user icon'>";
    html += "<article class='float--left'>";
    html += "<h4>"+user.firstName+" "+user.lastName+"</h4>";
    if(isAdmin)
        html += "<h5>Administrator</h5>";
    html += "email: "+ user.email;
    html += "</article>";
    if(!isAdmin)
        html += "<input class='float--right' type='checkbox'>";
    html += "<div class='float--both'></div>";
    html += "</div>";

    lastAdded = user.accountId;

    return html;
}

function getSelf(){
    $.ajax({
        url: "/api/accounts/me",
        type: "GET",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function (data) {
            localStorage.setItem("account", JSON.stringify(data.data));
        },
        error: function (xhr) {
            if(status == 401){
                showMessage("You are not authenticated !")
                window.location.replace("index.html");
            }
        }
    });
}

function pickUser() {
   // var html = "<div>";
   //
   //  html = "</div>";


};





$("#saveProject").click(function () {
    var project_name = $('#project_name').val();
    var project_decscription = $('#project_decscription').val();
    var admin_id = JSON.parse(localStorage.getItem("account")).accountId;

    console.log(JSON.stringify({
        administratorAccountIds: [admin_id],
        description: project_decscription,
        name: project_name,
        participantsAccountIds: [2]
    }));

    $.ajax({
        url: "/api/projects",
        type: "POST",
        data: JSON.stringify({
            administratorAccountIds: [admin_id],
            description: project_decscription,
            name: project_name,
            participantsAccountIds: [2]
        }),
        contentType:"application/json; charset=utf-8",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function(data){
            window.location.replace("projects.html");
        },
        error: function(xhr){
            showMessage("Error "+xhr.status+"! Project could not be created!");
        }
    });
});
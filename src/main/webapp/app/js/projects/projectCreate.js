/**
 * Created by juraj on 15.11.2016.
 */

var participants = new Array();

$(document).ready(function () {
    checkAccountInfo();
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
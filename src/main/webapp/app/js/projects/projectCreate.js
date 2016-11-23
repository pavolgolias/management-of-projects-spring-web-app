/**
 * Created by juraj on 15.11.2016.
 */

var lastAdded;

$(document).ready(function () {
    checkAccountInfo();
    if(localStorage.getItem("account") == null){
        window.location.replace("index.html");
    }
    var user = JSON.parse(localStorage.getItem("account"));
    getSelectedAdmins().push(user.accountId);
    $("#admins").append(buildUserElement(user,true,true));
    $("#accountAdmin"+user.accountId).find("a").remove();
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
            administratorAccountIds: getSelectedAdmins(),
            description: project_decscription,
            name: project_name,
            participantsAccountIds: getSelectedParticipants()
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

function removeFromAssigned(id){
    var card = $("#account"+id);
    card.find("a")[0].onclick= null;
    card.find("a").empty().append("&plus;").click(function () {
        addToAssigned(id);
    })
    card.slideUp('normal', function() {
        card.detach().appendTo("#suggestedUsers");
        card.slideDown('normal');
    });

    for(var index = 0 ; index < getSelectedParticipants().length; index++){
        if(id === getSelectedParticipants()[index]){
            getSelectedParticipants().splice(index,1);
            return;
        }
    }
}

function addToAssigned(id){
    var card = $("#account"+id);
    card.find("a")[0].onclick= null;
    card.find("a").empty().append("&minus;").click(function () {
        removeFromAssigned(id);
    })
    card.slideUp('normal', function() {
        card.detach().appendTo("#assignedUsers");
        card.slideDown('normal');
    });


    getSelectedParticipants().push(id);
}


function removeFromAdmins(id){
    var card = $("#accountAdmin"+id);
    card.find("a")[0].onclick= null;
    card.find("a").empty().append("&plus;").click(function () {
        addToAdmins(id);
    })
    card.slideUp('normal', function() {
        card.detach().appendTo("#suggestedAdmins");
        card.slideDown('normal');
    });

    for(var index = 0 ; index < getSelectedAdmins().length; index++){
        if(id === getSelectedAdmins()[index]){
            getSelectedAdmins().splice(index,1);
            return;
        }
    }
}

function addToAdmins(id){
    var card = $("#accountAdmin"+id);
    card.find("a")[0].onclick= null;
    card.find("a").empty().append("&minus;").click(function () {
        removeFromAdmins(id);
    })
    card.slideUp('normal', function() {
        card.detach().appendTo("#admins");
        card.slideDown('normal');
    });


    getSelectedAdmins().push(id);
}
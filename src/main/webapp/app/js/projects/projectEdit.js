/**
 * Created by juraj on 17.11.2016.
 */
var projectId;


$( document ).ready(function() {
    projectId = getParameterByName("projectId",window.location.href);
    if(projectId != null){
        getProjectDetail(projectId);
    }

});

function getProjectDetail(projectId){

    return $.ajax({
        url: "/api/projects/"+projectId,
        type: "GET",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function (data) {
            displayProject(data.data);
            displayAssignedUsersForProject(data.data,"#assignedUsers");
            displayAdminsForProject(data.data,"#admins")
            readAssignedUsersIds(data.data);
            readAdminIds(data.data)
        },
        error: function (xhr) {
            if(xhr.status == 401){
                window.location.replace("index.html");
            }else{
                showMessage("Error "+xhr.status+"! Unable to load projects!")
            }
        }
    });
}

function displayProject(jsonResult){

    $("#projectName").val(jsonResult.name);
    $("#projectId").val("#"+jsonResult.projectId);
    $("#task_id").val("#"+jsonResult.projectId);
    updateEditLink(jsonResult.projectId);
    $("#projectDescription").text(jsonResult.description);
    $("#updateDate").text((new Date(jsonResult.updatedAt)).toLocaleString());

}

function updateEditLink(projectId){
    $("#editTaskLink").attr("href","project_edit.html?projectId="+projectId);
}
function getParameterByName(name, url) {
    if (!url) {
        url = window.location.href;
    }
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function buildUserList(admins, users){
    var html;
    for(var i = 0;i < admins.length;i++){
        html += buildUser(admins[i], true);
    }
    for(var i = 0;i < users.length;i++){
        html += buildUser(users[i], false);
    }
    return html;
}

function buildUser(user, isAdmin) {

    var html="<div class='card-row card-row--user'>";
    html += "<a class='float--right'>&minus;</a>"
    //html += "<img class='float float--left' src='"user.avatarFilename"' alt='user icon'>";
    html += "<img class='float float--left' src='images/icons/white/user.png' alt='user icon'>";
    html += "<article class='float--left'>";
    html += "<h4>"+user.firstName+" "+user.lastName+"</h4>";
    if(isAdmin)
        html += "<h5>Administrator</h5>";
    else
        html += "<h5>Participant</h5>";
    html += "email: "+ user.email;
    html += "</article>";
    html += "<div class='float--both'></div>";
    html += "</div>";

    return html;
}

function backToProject() {
    window.location.replace("project_detail.html?projectId="+projectId);
}

$("#saveProject").click(function () {
    var project_name = $('#projectName').val();
    var project_decscription = $('#projectDescription').val();
    var admin_id = JSON.parse(localStorage.getItem("account")).accountId;

    if(project_name == '') {
        showMessage("Project name cannot be empty!");
        return;
    }

    if(getSelectedAdmins().length < 1){
        showMessage("The project does not have any administrators!")
        return;
    }


    $.ajax({
        url: "/api/projects/"+projectId,
        type: "PUT",
        data: JSON.stringify({
            description: project_decscription,
            name: project_name
        }),
        contentType:"application/json; charset=utf-8",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function(data){
            window.location.replace("project_detail.html?projectId="+projectId);
        },
        error: function(xhr){
            showMessage("Error "+xhr.status+"! Project could not be edited!");
            throw exception("Error "+xhr.status+"! Project could not be edited!");
        }
    });


});



function ajaxAddParticipant(id){
    $.ajax({
        url: "/api/projects/"+projectId+"/participants?action=Add",
        type: "POST",
        async:false,
        data:JSON.stringify([id]),
        contentType:"application/json; charset=utf-8",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function(data){
            showMessage("User added to participants");
        },
        error: function(xhr){
            showMessage("Error "+xhr.status+"! Unable to change the assignees!");
            throw exception("Error "+xhr.status);

        }
    });
}

function ajaxRemoveParticipants(id){
    $.ajax({
        url: "/api/projects/"+projectId+"/participants?action=Remove",
        type: "POST",
        async:false,
        data:JSON.stringify([id]),
        contentType:"application/json; charset=utf-8",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function(data){
            showMessage("User removed from participants");
        },
        error: function(xhr){
            showMessage("Error "+xhr.status+"! Unable to change the assignees!");
            throw exception("Error "+xhr.status);

        }
    });
}

function ajaxAddToAdmins(id){
    $.ajax({
        url: "/api/projects/"+projectId+"/administrators?action=Add",
        type: "POST",
        async:false,
        data:JSON.stringify([id]),
        contentType:"application/json; charset=utf-8",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function(data){
            showMessage("User added to administrators");
        },
        error: function(xhr){
            showMessage("Error "+xhr.status+"! Unable to change the administrators!");
            throw exception("Error "+xhr.status);

        }
    });
}

function ajaxRemoveFromAdmins(id){
    $.ajax({
        url: "/api/projects/"+projectId+"/administrators?action=Remove",
        type: "POST",
        async:false,
        data:JSON.stringify([id]),
        contentType:"application/json; charset=utf-8",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function(data){
            showMessage("User removed from admins")
        },
        error: function(xhr){
            showMessage("Error "+xhr.status+"! Unable to change the administrators!");
            throw exception("Error "+xhr.status);
        }
    });
}



function removeFromAssigned(id){
    ajaxRemoveParticipants(id);

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
    ajaxAddParticipant(id);
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

function readAssignedUsersIds(jsonProjectObject){
    for(var index = 0 ; index< jsonProjectObject.participants.length ; index ++){
        getSelectedParticipants().push(jsonProjectObject.participants[index].accountId);
    }
}

function removeFromAdmins(id){
    ajaxRemoveFromAdmins(id);
    var card = $("#accountAdmin"+id);
    card.find("a")[0].onclick= null;
    card.find("a").empty().append("&plus;").click(function () {
        addToAdmins(id);
    })
    card.slideUp('normal', function() {
        card.detach().appendTo("#suggestedAdmins");
        card.slideDown('normal');
    });

    for(var index = 0 ; index <  getSelectedAdmins().length; index++){
        if(id ===  getSelectedAdmins()[index]){
            getSelectedAdmins().splice(index,1);
            return;
        }
    }
}

function addToAdmins(id){
    ajaxAddToAdmins(id);
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

function readAdminIds(jsonProjectObject){
    for(var index = 0 ; index< jsonProjectObject.administrators.length ; index ++){
        getSelectedAdmins().push(jsonProjectObject.administrators[index].accountId);
    }
}
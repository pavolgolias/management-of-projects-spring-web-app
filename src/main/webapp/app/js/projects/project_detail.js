/**
 * Created by Patrik on 15/11/2016.
 */
$( document ).ready(function() {
    var projectId = getParameterByName("projectId",window.location.href);
    if(projectId != null){
        getProjectDetail(projectId);
    }

});

function getProjectDetail(projectId){
    console.log("/api/projects/"+projectId);
    return $.ajax({
        url: "/api/projects/"+projectId,
        type: "GET",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function (data) {
            displayProject(data.data);
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
    $("#projectName").text(jsonResult.name);
	$("#projectNameMobileField").text(jsonResult.name);
    $("#projectId").text("#"+jsonResult.projectId);
    $("#projectDescription").text(jsonResult.description);
    $("#updateDate").text((new Date(jsonResult.updatedAt)).toLocaleString());

    updateLinks(jsonResult.projectId);
    $("#userList").empty();
    $("#userList").append(buildUserList(jsonResult.administrators, jsonResult.participants));

}

function updateLinks(projectId){
    $("#editTaskLink").attr("href","project_edit.html?projectId="+projectId);
    $("#taskBoardLink").attr("href","scrum_board.html?projectId="+projectId);
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
    var html ='';
    for(var i = 0;i < admins.length;i++){
        html += buildUser(admins[i], true);
    }
    for(var j = 0;j < users.length;j++){
        html += buildUser(users[j], false);
    }
    return html;
}

function buildUser(user, isAdmin) {
    if(user === null )
        return;
    console.log(user);
    var html="<div class='card-row card-row--user'>";
    //html += "<img class='float float--left' src='"user.avatarFilename"' alt='user icon'>";
    html += "<img class='float float--left' src='"+user.staticAvatarFilename+"' alt='user icon'>";
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
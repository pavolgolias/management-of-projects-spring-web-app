/**
 * Created by Patrik on 12/11/2016.
 */
$( document ).ready(function() {
    checkAccountInfo();
    getAllProjects();
});

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

function getAllProjects(){
    return $.ajax({
        url: "/api/projects",
        type: "GET",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function (data) {
            displayProjects(data);
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

function displayProjects(data){
    console.log(data);
    var userId = JSON.parse(localStorage.getItem("account")).accountId;
    for(var index = 0;index < data.data.length; index++){
        var project = data.data[index];
        var buildedHtml = buildProject(index,project);
        $("#allProjectsContainer").append(buildedHtml);
        if(isLoggedUserAdmin(userId,project)){
            $("#adminProjectsContainer").append(buildedHtml);
        }

        if(isLoggedUserAssigned(userId,project)){
            $("#assignedProjectsContainer").append(buildedHtml);
        }
    }
}

function buildProject(index,projectObject){
    var html = "<div class='col-sm-6 col-md-4'><div class='card card--project'><div class='index hidden'>";
    html = html + index + "</div>";

    html = html + "<header><h4 class='float--left'>";
    if(projectObject.administrators.length>0){
        html = html +projectObject.administrators[0].firstName+" "+projectObject.administrators[0].lastName;
    }
    html =html + "</h4>";

    html = html + "<h4 class='float--right'>Last edit: "+(new Date(projectObject.updatedAt)).toLocaleString()+"</h4> <div class='float--clear'></div> </header>"

    html = html + "<section><img src='images/icons/white/interface.png' alt='project icon'><article><a href='project_detail.html?id="+projectObject.projectId+"'><h3>";
    html = html + projectObject.name +"</h3></a>";
    html = html + "<p>" + projectObject.description + "</p></article><ul>";

    for(var iconIndex=0; iconIndex < projectObject.participants.length ; iconIndex ++){
        html = html + "<li><img src='images/icons/white/user.png' alt='user icon' title='"+ projectObject.participants[iconIndex].firstName+" "+projectObject.participants[iconIndex].lastName+"'></li>";
    }
    html = html + "</ul><div class='float--clear'></div></section></div></div></div>";
    return html;
}


function isLoggedUserAdmin(userId,projectObject){
    if(userId === null || projectObject === null )
        return false;

    for(var i = 0 ; i < projectObject.administrators.length;i++){
        if(userId == projectObject.administrators[i].accountId)
            return true;
    }
    return false;
}

function isLoggedUserAssigned(userId,projectObject){
    if(userId === null || projectObject === null )
        return false;
    for(var i = 0 ; i < projectObject.participants.length;i++){
        if(userId == projectObject.participants[i].accountId)
            return true;
    }
    return false;
}
/**
 * Created by juraj on 20.11.2016.
 */

$(document).ready(function () {
    var projectId = getUrlParameter("projectId");
    var taskId = getUrlParameter("taskId");
    if(taskId != null && projectId != null){
        getTaskDetail(taskId, projectId);
    }

})

function getTaskDetail(taskId, projectId) {
    return $.ajax({
        url: "/api/projects/"+projectId+"/tasks/"+taskId,
        type: "GET",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function (data) {
            displayTask(data.data);
        },
        error: function (xhr) {
            if(xhr.status == 401){
                window.location.replace("index.html");
            }else{
                showMessage("Error "+xhr.status+"! Unable to load task!")
            }
        }
    });
}

function displayTask(task) {
    console.log(task);
    updateLinks(getUrlParameter("projectId"),getUrlParameter("taskId"));
    $("#taskName").text(task.name);
    $("#projectId").text(getUrlParameter("projectId"));
    $("#taskType").text(task.type);
    $("#taskPriority").text(task.priority);
    $("#taskStatus").text(task.status);
    $("#taskTimeEstimate").text(toHours(task.timeEstimatedForTaskInMillis));
    $("#taskTimeConsumed").text(toHours(task.timeSpentOnTaskInMillis));
    $("#taskProgress").width(task.progress+"%");
    $("#taskProgressText").text(task.progress+" %");
    $("#taskCreatedAt").text((new Date(task.author.createdAt)).toLocaleString());
    $("#taskLastUpdate").text((new Date(task.updatedAt)).toLocaleString()); // needs to be changed after it is addted to DTO
    $("#taskETA").text((new Date(task.aimedCompletionDate)).toLocaleString());
    $("#taskDescription").text(task.description);
    $("#assignedUser").append(buildUser(task.assignee));
}

function updateLinks(projectId, taskId){
    $("#editTaskId").attr("href","task_edit.html?projectId="+projectId+"&taskId="+taskId);
    $("#backToScrumBoard").attr("href","scrum_board.html?projectId="+projectId);
}

function buildUser(user) {
    if(user === null )
        return;

    var html="<div class='card-row card-row--user'>";
    //html += "<img class='float float--left' src='"user.avatarFilename"' alt='user icon'>";
    html += "<img class='float float--left' src='"+user.staticAvatarFilename+"' alt='user icon'>";
    html += "<article class='float--left'>";
    html += "<h4>"+user.firstName+" "+user.lastName+"</h4>";
    html += "email: "+ user.email;
    html += "</article>";
    html += "<div class='float--both'></div>";
    html += "</div>";

    return html;
}

function toHours(dataInMilis){
    return (dataInMilis / 3600000);
}

function toMilis(dataInHours){
    return (dataInHours * 3600000);
}

function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};
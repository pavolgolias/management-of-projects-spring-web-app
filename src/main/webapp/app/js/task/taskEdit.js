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
    console.log("/api/projects/"+projectId+"/tasks/"+taskId);
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
    $("#taskName").val(task.name);
    $("#projectId").text(getUrlParameter("projectId"));
    $("#taskType").val(task.type);
    $("#taskPriority").val(task.priority);
    $("#taskStatus").val(task.status);
    //future enhancement : counting hours and minutes
    $("#taskTimeEstimate").val(task.timeEstimatedForTaskInMillis / 3600000);
    $("#taskTimeConsumed").val(task.timeSpentOnTaskInMillis / 3600000);
    //$("#taskProgress").val(""+task.progress+" %");//nefunguje zatial
    $("#taskCreatedAt").text((new Date(task.author.createdAt)).toLocaleString());
    $("#taskLastUpdate").text((new Date(task.assignee.createdAt)).toLocaleString()); // needs to be changed after it is addted to DTO
    $("#taskETA").val((new Date(task.aimedCompletionDate)).toLocaleString());
    $("#taskDescription").val(task.description);
    // $("#taskAssignee").append(buildUser(task.assignee));
    $("#assignedUser").append(buildUser(task.assignee));
}

function buildUser(user) {
    if(user === null )
        return;
    console.log(user);
    var html="<div class='card-row card-row--user'>";
    //html += "<img class='float float--left' src='"user.avatarFilename"' alt='user icon'>";
    html += "<img class='float float--left' src='images/avatar.png' alt='user icon'>";
    html += "<article class='float--left'>";
    html += "<h4>"+user.firstName+" "+user.lastName+"</h4>";
    html += "email: "+ user.email;
    html += "</article>";
    html += "<div class='float--both'></div>";
    html += "</div>";

    return html;
}

function updateLinks(projectId, taskId) {
    $("#backTaskDetail").attr("href","task_detail.html?projectId="+projectId+"&taskId="+taskId);
}

var getUrlParameter = function getUrlParameter(sParam) {
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
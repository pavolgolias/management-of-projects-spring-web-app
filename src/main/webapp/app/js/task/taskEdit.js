/**
 * Created by juraj on 20.11.2016.
 */

var projectId;
var taskId;
var taskDetail;

$(document).ready(function () {
    projectId = getUrlParameter("projectId");
    taskId = getUrlParameter("taskId");
    if(taskId != null && projectId != null){
        getTaskDetail(taskId, projectId);
    }
});

function getTaskDetail(taskId, projectId) {
    return $.ajax({
        url: "/api/projects/"+projectId+"/tasks/"+taskId,
        type: "GET",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function (data) {
            console.log(data.data);
            taskDetail = data.data;
            displayTask(data.data);
            displayAssignedUserForTask(data.data,"#assignedUser");
            getProjectDetail(projectId, data.data.assignee.accountId);
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
    updateLinks(projectId,taskId);
    $("#taskName").val(task.name);
    $("#projectId").text(projectId);
    $("#taskType").val(task.type);
    $("#taskPriority").val(task.priority);
    $("#taskStatus").val(task.status);
    //future enhancement : counting hours and minutes
    $("#taskTimeEstimate").val(toHours(task.timeEstimatedForTaskInMillis));
    $("#taskTimeConsumed").val(toHours(task.timeSpentOnTaskInMillis));
    $("#taskProgress").val(task.progress);//nefunguje zatial
    $("#taskCreatedAt").text((new Date(task.author.createdAt)).toLocaleString());
    $("#taskLastUpdate").text((new Date(task.assignee.createdAt)).toLocaleString()); // needs to be changed after it is addted to DTO
    $("#taskETA").text((new Date(task.aimedCompletionDate)).toLocaleString());
    $("#taskDescription").val(task.description);
}

function getProjectDetail(projectId, assignedUser) {
    return $.ajax({
        url: "/api/projects/"+projectId,
        type: "GET",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function (data) {
            displayAvailableUsersForTask(data.data, "#suggestedUsers");
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

$("#saveTask").click(function () {
    var task_name = $('#taskName').val();
    var task_decscription = $('#taskDescription').val();
    var task_priority = $('#taskPriority').val();
    var task_progress = $('#taskProgress').val();
    var task_status = $('#taskStatus').val();
    var task_type = $('#taskType').val();
    var time_to_add = $('#timeToAdd').val();
    var task_timeEstimatedForTaskInMillis = taskDetail.timeEstimatedForTaskInMillis + toMilis(time_to_add);
    var task_timeSpentOnTaskInMillis = taskDetail.timeSpentOnTaskInMillis;
    //var admin_id = JSON.parse(localStorage.getItem("account")).accountId;

    if(task_name == '') {
        showMessage("Project name cannot be empty!");
        return;
    }

    if(getAssignee().length < 1){
        showMessage("The task does not have any assignee!")
        return;
    }

    $.ajax({
        url: "/api/projects/"+projectId+"/tasks/"+taskId,
        type: "PUT",
        data: JSON.stringify({
            aimedCompletionDate: taskDetail.aimedCompletionDate,
            assigneeId: getAssignee(),
            description: task_decscription,
            name: task_name,
            priority: task_priority,
            progress: task_progress,
            status: task_status,
            timeEstimatedForTaskInMillis: task_timeEstimatedForTaskInMillis,
            timeSpentOnTaskInMillis: task_timeSpentOnTaskInMillis,
            type:task_type
        }),
        contentType:"application/json; charset=utf-8",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function(data){
            window.location.replace("task_detail.html?projectId="+projectId+"&taskId="+taskId);
        },
        error: function(xhr){
            if(xhr.status == 403) {
                showMessage("Error " + xhr.status + "! You are not allowed to update task!");
                throw exception("Error " + xhr.status + "! Task could not be edited!");
            }
            else {
                showMessage("Error " + xhr.status + "! Task could not be edited!");
                throw exception("Error " + xhr.status + "! Task could not be edited!");
            }
        }
    });


});

function toHours(dataInMilis){
    return (dataInMilis / 3600000);
}

function toMilis(dataInHours){
    return (dataInHours * 3600000);
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
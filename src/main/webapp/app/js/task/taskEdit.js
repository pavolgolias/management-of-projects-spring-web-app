/**
 * Created by juraj on 20.11.2016.
 */

var projectId;
var taskId;
var taskDetail;
var assignee;
var availableUsers = [];

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
            getProjectDetail(projectId);
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
    $("#taskLastUpdate").text((new Date(task.updatedAt)).toLocaleString()); // needs to be changed after it is addted to DTO
    $("#taskETA").val((new Date(task.aimedCompletionDate)).toLocaleString());
    $("#taskDescription").val(task.description);
}

function getProjectDetail(projectId) {
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
    var task_ETA = $("#taskETA").val();
    var task_timeEstimatedForTaskInMillis = taskDetail.timeEstimatedForTaskInMillis;
    var task_timeSpentOnTaskInMillis = taskDetail.timeSpentOnTaskInMillis + toMilis(time_to_add);

    if(task_ETA == ((new Date(taskDetail.aimedCompletionDate)).toLocaleString())){
        task_ETA = taskDetail.aimedCompletionDate;
    }else{
        task_ETA = stringToDate($('#taskETA').val(),"dd.MM.yyyy",".");
    }


    if(task_name == '') {
        showMessage("Task name cannot be empty!");
        return;
    }

    var assignee_id;
    if(assignee != null)
        assignee_id = assignee.accountId;

    $.ajax({
        url: "/api/projects/"+projectId+"/tasks/"+taskId,
        type: "PUT",
        data: JSON.stringify({
            aimedCompletionDate: task_ETA,
            assigneeId: assignee_id,
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

function addAssignee(iduser){
    if(assignee == null){
        assignee = findUser(iduser);
        $("#account"+iduser).remove();

        console.log(assignee);
        $("#assignedUser").append(buildUserElement(assignee,true));

        availableUsers.slice(findUser(iduser));
        console.log(availableUsers);
    }
}

function removeAssignee(iduser){

    var html = buildUserElement(assignee,false);

    $("#account"+iduser).remove();
    $("#suggestedUsers").append(html);

    availableUsers.push(assignee);
    // console.log(availableUsers);
    assignee = null;
    // console.log(assignee);
}

function displayAssignedUserForTask(jsonTaskObject,selector){
    if(jsonTaskObject.assignee != null) {
        $(selector).append(buildUserElement(jsonTaskObject.assignee, true));
        assignee = jsonTaskObject.assignee;
    }
}

function stringToDate(_date,_format,_delimiter) {
    var formatLowerCase=_format.toLowerCase();
    var formatItems=formatLowerCase.split(_delimiter);
    var dateItems=_date.split(_delimiter);
    var monthIndex=formatItems.indexOf("mm");
    var dayIndex=formatItems.indexOf("dd");
    var yearIndex=formatItems.indexOf("yyyy");
    var month=parseInt(dateItems[monthIndex]);
    month-=1;
    var formatedDate = new Date(dateItems[yearIndex],month,dateItems[dayIndex]);

    return formatedDate;
}

function displayAvailableUsersForTask(jsonProjectObject, selector){
    for(var index = 0 ; index< jsonProjectObject.participants.length ; index ++){
        if(assignee == null){
            $(selector).append(buildUserElement(jsonProjectObject.participants[index], false));
            availableUsers.push(jsonProjectObject.participants[index]);
        }
        else if(assignee.accountId != jsonProjectObject.participants[index].accountId) {
            $(selector).append(buildUserElement(jsonProjectObject.participants[index], false));
            availableUsers.push(jsonProjectObject.participants[index]);
        }
    }
    for(var index = 0 ; index< jsonProjectObject.administrators.length ; index ++){
        if(assignee == null){
            $(selector).append(buildUserElement(jsonProjectObject.administrators[index], false));
            availableUsers.push(jsonProjectObject.administrators[index]);
        }
        else if(assignee.accountId != jsonProjectObject.administrators[index].accountId || assignee == null) {
            $(selector).append(buildUserElement(jsonProjectObject.administrators[index], false));
            availableUsers.push(jsonProjectObject.administrators[index]);
        }
    }
}

function findUser(iduser) {
    for(var index = 0 ; index < availableUsers.length ; index++){
        if(availableUsers[index].accountId == iduser)
            return availableUsers[index];
    }
}


function buildUserElement(userJsonObject,toAssign){
    var defaultImg = "<img class='float--left' src='images/avatar.png' alt='avatar'>";
    var html = '';

    if(toAssign === true)
        html = "<div id='account"+userJsonObject.accountId+"' class='card-row card-row--user suggested'><a class='float--right' onclick='removeAssignee("+userJsonObject.accountId+")'>&minus;</a>";
    else
        html = "<div id='account"+userJsonObject.accountId+"' class='card-row card-row--user assigned'><a class='float--right' onclick='addAssignee("+userJsonObject.accountId+")'>&plus;</a>";

    //TODO add users avatar
    html = html + defaultImg;
    html = html + "<article class='float--left'><h4>"
    html = html + userJsonObject.firstName + " " + userJsonObject.lastName;
    html = html + "</h4><p>";
    html = html + userJsonObject.email;
    html = html + "</p></article> <div class='float--both'></div> </div>";
    return html;
}
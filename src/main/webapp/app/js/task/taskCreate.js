/**
 * Created by mirec on 19.11.2016.
 */
var projectId;
var assignee;
var availableUsers = [];

$(document).ready(function () {
    checkAccountInfo();
    if(localStorage.getItem("account") == null){
        window.location.replace("index.html");
    }
    projectId = getParameterByName("projectId",window.location.href);
    if(projectId != null){
        updateLinks(projectId);
        getProjectDetail(projectId);
    }
    var date = new Date();
    $('#task_created').text(date.getDate() + '.' + (date.getMonth() + 1) + '.' +  date.getFullYear());
})

function updateLinks(projectId){
    $("#backTaskCreate").attr("href","scrum_board.html?projectId="+projectId);
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



$("#saveTask").click(function () {
    var task_name = $('#task_name').val();
    var task_type = $('#task_type').val();
    var task_priority = $('#task_priority').val();
    var task_eta = stringToDate($('#task_eta').val(),"dd.MM.yyyy",".");
    var task_description = $('#task_decscription').val();
    var task_estimate = $('#taskTimeEstimate').val();
    var task_estimate_milis = toMilis(task_estimate);

    var admin_id = JSON.parse(localStorage.getItem("account")).accountId;

    if(task_name == '') {
        showMessage("Task name cannot be empty!");
        return;
    }
    if(task_description == '') {
        showMessage("Task description cannot be empty!");
        return;
    }
    if(task_estimate == 0){
        showMessage("Task estimate time cannot be 0!");
        return;
    }

    var assignee_id;
    if(assignee != null)
        assignee_id = assignee.accountId;

    $.ajax({
        url: "/api/projects/"+projectId+"/tasks",
        type: "POST",
        data: JSON.stringify({
            name: task_name,
            description: task_description,
            type: task_type,
            priority: task_priority,
            aimedCompletionDate: task_eta,
            assigneeId: assignee_id,
            timeEstimatedForTaskInMillis: task_estimate_milis
        }),
        contentType:"application/json; charset=utf-8",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function(data){
            window.location.replace("scrum_board.html?projectId="+projectId);
        },
        error: function(xhr){
            showMessage("Error "+xhr.status+"! Task could not be created!");
        }
    });
});

function stringToDate(_date,_format,_delimiter)
{
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

function addAssignee(iduser){
    if(assignee == null){
        assignee = findUser(iduser);
        $("#account"+iduser).slideUp("normal",function(){
            $("#account"+iduser).remove();
            $("#assignedUser").append(buildUserElement(assignee,true)).hide().slideDown("normal");

        })

        // console.log(assignee);

        availableUsers.slice(findUser(iduser));
        // console.log(availableUsers);
    }
}

function removeAssignee(iduser){

    var html = buildUserElement(assignee,false);

    $("#account"+iduser).slideUp("normal",function(){
        $("#account"+iduser).remove();
        $("#suggestedUsers").append(html).hide().slideDown("normal");

    });

    availableUsers.push(assignee);
    // console.log(availableUsers);
    assignee = null;
    // console.log(assignee);
}

function toMilis(dataInHours){
    return (dataInHours * 3600000);
}

function displayAvailableUsersForTask(jsonProjectObject, selector){
    for(var index = 0 ; index< jsonProjectObject.participants.length ; index ++){

        $(selector).append(buildUserElement(jsonProjectObject.participants[index], false));
        availableUsers.push(jsonProjectObject.participants[index]);

    }
    for(var index = 0 ; index< jsonProjectObject.administrators.length ; index ++){

        $(selector).append(buildUserElement(jsonProjectObject.administrators[index], false));
        availableUsers.push(jsonProjectObject.administrators[index]);

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
    html = html + "<img class='float float--left' src='"+userJsonObject.staticAvatarFilename+"' alt='user icon'>";
    html = html + "<article class='float--left'><h4>"
    html = html + userJsonObject.firstName + " " + userJsonObject.lastName;
    html = html + "</h4><p>";
    html = html + userJsonObject.email;
    html = html + "</p></article> <div class='float--both'></div> </div>";
    return html;
}
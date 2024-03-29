/**
 * Created by juraj on 20.11.2016.
 */

var projectId;
var tasks = [];

$( document ).ready(function() {
    projectId = getUrlParameter("projectId");
    updateLinks(projectId);
    getAllTasks();

});

function insertScripts(){
    var s = document.createElement("script");
    s.type = "text/javascript";
    s.src = "js/addDropMenu.js";
    // Use any selector
    $("head").append(s);
}
function getAllTasks() {
    //console.log("/api/projects/"+projectId+"/tasks");
    return $.ajax({
        url: "/api/projects/"+projectId+"/tasks",
        type: "GET",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function (data) {
            displayTasks(data.data);
        },
        error: function (xhr) {
            if(xhr.status == 401){
                window.location.replace("index.html");
            }else{
                showMessage("Error "+xhr.status+"! Unable to load scrum board!")
            }
        }
    });
}

function displayTasks(data) {
    console.log(data);
    tasks = data;
    for(var index = 0; index < data.length; index++){
        var task = data[index];
        var html = buildTask(task);
        if(checkStatus(task, "Todo")){
            $("#toDoTasks").append(html);
        }
        if(checkStatus(task,"InProgress")){
            $("#inProgressTasks").append(html);
        }
        if(checkStatus(task,"Done")){
            $("#doneTasks").append(html);
        }
    }
    // $( document ).("<script src='js/addDropMenu.js'></script>")
    // $("#loadDropMenu").append();
    insertScripts();
}

function checkStatus(task,status) {
    if(task == null || task.status != status)
        return false;
    else
        return true;
}

function buildTask(task) {
    //console.log(task);
    var html = "<div class='col-sm-6 col-md-4'>";
    if(task.type == "Task")
        html += "<div class='card card--task'>";
    if(task.type == "Bug")
        html += "<div class='card card--task card--bug'>"
    if(task.type == "ChangeRequest")
        html += "<div class='card card--task card--info'>"
    html += "<div class='index hidden'>"+task.taskId+"</div>";
    html += "<header>";
    html += "<h4 class='float--left'>"+task.type+"</h4>";
    html += "<div data-addui='dropMenu' data-pin='top-right'>";
    if(checkStatus(task, "Todo")){
        html += "<a onclick='move("+task.taskId+",\"InProgress\")'>Move to In Progress</a>";
        html += "<a onclick='move("+task.taskId+",\"Done\")'>Move to Completed</a>";
    }
    if(checkStatus(task,"InProgress")){
        html += "<a onclick='move("+task.taskId+",\"Todo\")'>Move to To Do</a>";
        html += "<a onclick='move("+task.taskId+",\"Done\")'>Move to Completed</a>";
    }
    if(checkStatus(task,"Done")){
        html += "<a onclick='move("+task.taskId+",\"Todo\")'>Move to To Do</a>";
        html += "<a onclick='move("+task.taskId+",\"InProgress\")'>Move to In Progress</a>";
    }
    html += "<a href='task_edit.html?projectId="+projectId+"&taskId="+task.taskId+"'>Edit</a>";
    html += "<a onclick='deleteTask("+task.taskId+")'>Delete</a>";
    html += "</div>";
    html += "<div class='float--clear'></div>";
    html += "</header>";
    html += "<section>";
    html += "<article>";
    html += "<a href='task_detail.html?projectId="+projectId+"&taskId="+task.taskId+"'><h3>"+task.name+"</h3></a>";
    html += "<p>"+task.description+"</p>";
    html += "</article>";
    if(task.assignee != null){
        html += "<ul>";
        html += "<li><img src='"+task.assignee.staticAvatarFilename+"' alt='user icon' title='"+task.assignee.firstName+" "+task.assignee.lastName+"'></li>";
        html += "</ul>";
    }else {
        html += "<ul>";
        html += "<li><img src='images/icons/white/user-add.png' alt='user icon'></li>";
        html += "</ul>";
    }

    html += "<div class='float--clear'></div>";
    html += "</section>";
    html += "</div>";
    html += "</div>";

    return html;
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
}

function updateLinks(projectId){
    $("#createTaskLink").attr("href","task_create.html?projectId="+projectId);
}

function getTask(taskId){
    for(var index = 0 ; index < tasks.length ; index++){
        if(tasks[index].taskId == taskId)
            return tasks[index];
    }
}

function move(taskId, status){
    var task = getTask(taskId);
    var assignee = task.assignee;
    var progress = task.progress;

    if(status == "Done"){
        progress = 100;
    }

    var assignee_id;
    if(assignee != null)
        assignee_id = assignee.accountId;


    $.ajax({
        url: "/api/projects/"+projectId+"/tasks/"+taskId,
        type: "PUT",
        data: JSON.stringify({
            aimedCompletionDate: task.aimedCompletionDate,
            assigneeId: assignee_id,
            description: task.description,
            name: task.name,
            priority: task.priority,
            progress: progress,
            status: status,
            timeEstimatedForTaskInMillis: task.timeEstimatedForTaskInMillis,
            timeSpentOnTaskInMillis: task.timeSpentOnTaskInMillis,
            type:task.type
        }),
        contentType:"application/json; charset=utf-8",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function(data){
            window.location.replace("scrum_board.html?projectId="+projectId);
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
}

function deleteTask(taskId) {
    console.log("/api/projects/"+projectId+"/tasks/"+taskId);
    return $.ajax({
        url: "/api/projects/"+projectId+"/tasks/"+taskId,
        type: "DELETE",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function (data) {
            window.location.replace("scrum_board.html?projectId="+projectId);
        },
        error: function (xhr) {
            if(xhr.status == 404){
                window.location.replace("scrum_board.html/?projectId="+projectId);
                showMessage("Error "+xhr.status+"! Not possible to delete task yet!")
            }else{
                showMessage("Error "+xhr.status+"! Unable to delete task!")
            }
        }
    });
}
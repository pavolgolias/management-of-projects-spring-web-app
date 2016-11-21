/**
 * Created by juraj on 20.11.2016.
 */

var projectId;

$( document ).ready(function() {
    projectId = getUrlParameter("projectId");
    updateLinks(projectId);
    getAllTasks();
});

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
}

function checkStatus(task,status) {
    if(task == null || task.status != status)
        return false;
    else
        return true;
}

function buildTask(task) {
    var html = "<div class='col-sm-6 col-md-4'>";
    html += "<div class='card card--task'>";
    html += "<div class='index hidden'>"+task.taskId+"</div>";
    html += "<header>";
    html += "<h4 class='float--left'>"+task.type+"</h4>";
    html += "<div data-addui='dropMenu' data-pin='top-right'>";
    if(checkStatus(task, "Todo")){
        html += "<a href='#'>Move to In Progress</a>";
        html += "<a href='#'>Move to Done</a>";
    }
    if(checkStatus(task,"InProgress")){
        html += "<a href='#'>Move to To Do</a>";
        html += "<a href='#'>Move to Done</a>";
    }
    if(checkStatus(task,"Done")){
        html += "<a href='#'>Move to To Do</a>";
        html += "<a href='#'>Move to In Progress</a>";
    }
    html += "<a href='task_edit.html?projectId="+projectId+"&taskId="+task.taskId+"'>Edit</a>";
    html += "<a href='deleteTask("+task.taskId+")'>Delete</a>";
    html += "</div>";
    html += "<div class='float--clear'></div>";
    html += "</header>";
    html += "<section>";
    html += "<article>";
    html += "<a href='task_detail.html?projectId="+projectId+"&taskId="+task.taskId+"'><h3>"+task.name+"</h3></a>";
    html += "<p>"+task.description+"</p>";
    html += "</article>";
    html += "<ul>";
    html += "<li><img src='images/icons/white/user.png' alt='user icon'></li>";
    html += "</ul>";
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
    $("#createTaskLink").attr("href","task_create.html?id="+projectId);
}

function deleteTask(taskId) {
    return $.ajax({
        url: "/api/projects/"+projectId+"/tasks/"+taskId,
        type: "DELETE",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function (data) {
            window.location.replace("scrum_board.html/?projectId="+projectId);
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
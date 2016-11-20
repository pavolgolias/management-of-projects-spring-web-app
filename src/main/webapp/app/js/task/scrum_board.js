/**
 * Created by juraj on 20.11.2016.
 */

var projectId;

$( document ).ready(function() {
    projectId = getUrlParameter("id");
    updateLinks(projectId);
    getAllTasks();
});

function getAllTasks() {
    console.log("/api/projects/"+projectId+"/tasks");
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
                showMessage("Error "+xhr.status+"! Unable to load projects!")
            }
        }
    });
}

function displayTasks(data) {
    console.log(data);

    for(var index = 0; index < data.length; index++){
        var task = data[index];
        if(isToDo(task, "Todo")){


        }
    }
}

function isToDo(task,status) {

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
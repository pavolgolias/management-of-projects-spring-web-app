/**
 * Created by Patrik on 17/11/2016.
 */
$( document ).ready(function() {
    if(localStorage.getItem("account") == null || localStorage.getItem("token") == null)
        return;

    var account = JSON.parse(localStorage.getItem("account"));
    $("#headerName").text(account.firstName + " " + account.lastName);
    $("#nameText").text(account.firstName);
    $("#surnameText").text(account.lastName);
    $("#userEmailText").text(account.email);

    processUsersProjects();

});


function processUsersProjects(){
    $.ajax({
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
    for(var index = 0;index < data.data.length; index++){
        $("#tasksContainer").append(buildHTMLForProject(data.data[index]));
    }
}

function buildHTMLForProject(project){
    console.log(project)
    var html = "<div class='card-row'><a href='project_detail.html?id="+project.projectId+"'><h3 class='float--left'>";
    html = html + "<h3 class='float--left'>"+project.name+"</h3>";
    html = html +"<p class='float--right'>></p><div class='float--clear'></div></a></div>";
    return html;
}

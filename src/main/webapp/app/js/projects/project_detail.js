/**
 * Created by Patrik on 15/11/2016.
 */
$( document ).ready(function() {
    var projectId = getParameterByName("id",window.location.href);
    if(projectId != null){
        getProjectDetail(projectId);
    }

});

function getProjectDetail(projectId){

    return $.ajax({
        url: "/api/projects/"+projectId,
        type: "GET",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function (data) {
            displayProject(data.data);
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

function displayProject(jsonResult){
    $("#projectName").text(jsonResult.name);
    $("#projectId").text("#"+jsonResult.projectId);
    $("#projectDescription").text(jsonResult.description);
    $("#updateDate").text((new Date(jsonResult.updatedAt)).toLocaleString());

    updateLinks(jsonResult.projectId);

}

function updateLinks(projectId){
    $("#editTaskLink").attr("href","project_edit.html?id="+projectId);
    $("#taskBoardLink").attr("href","scrum_board.html?id="+projectId);
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
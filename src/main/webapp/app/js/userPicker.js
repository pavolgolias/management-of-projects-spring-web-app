/**
 * Created by Patrik on 17/11/2016.
 */
/**
 *
 * Vo svojich JS file-och implementujte dve metódy addToAssigned(iduser) a removeFromAssigned(iduser)
 * kde zmenite ikonu + / -  a implementujete logiku
 *
 * EXAMPLE JE v projectEdit.js
 *
 * */


var previousQuery = '';
var projectId ;

function searchForUser(){
    var query = $("#searchUserInput").val();
    if(query == '' || query == previousQuery)
        return;

    if(projectId == null){
        projectId = getParameterByName("id",window.location.href);
    }
    $("#suggestedUsers").empty();

    $.ajax({
        url:  "/api/projects/"+projectId+"/participants/suggest",
        type: "GET",
        data: "searchKey="+query,
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function (data) {
            displaySuggestedUsersForProject(data.data);
        },
        error: function (xhr) {
            if(xhr.status == 401){
                window.location.replace("index.html");
            }else{
                showMessage("Error "+xhr.status+"! Unable to suggested users!")
            }
        }
    });



}

function buildUserElement(userJsonObject,toAssign){
    var defaultImg = "<img class='float--left' src='images/avatar.png' alt='avatar'>";
    var html = '';
    if(toAssign)
        html = "<div id='account"+userJsonObject.accountId+"' class='card-row card-row--user suggested'><a class='float--right' onclick='removeFromAssigned("+userJsonObject.accountId+")'>&minus;</a>";
    else
        html = "<div id='account"+userJsonObject.accountId+"' class='card-row card-row--user assigned'><a class='float--right' onclick='addToAssigned("+userJsonObject.accountId+")'>&plus;</a>";
    //TODO add users avatar
    html = html + defaultImg;
    html = html + "<article class='float--left'><h4>"
    html = html + userJsonObject.firstName + " " + userJsonObject.lastName;
    html = html + "</h4><p>";
    html = html + userJsonObject.email;
    html = html + "</p></article> <div class='float--both'></div> </div>";
    return html;
}

function displayAssignedUsersForProject(jsonProjectObject){
    for(var index = 0 ; index< jsonProjectObject.participants.length ; index ++){
        $("#assignedUsers").append(buildUserElement(jsonProjectObject.participants[index],true));
    }
}
function displaySuggestedUsersForProject(data) {
    console.log(data);
    for(var index = 0 ; index< data.length ; index ++){
        $("#suggestedUsers").append(buildUserElement(data[index],false));
    }
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
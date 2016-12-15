/**
 * Created by Patrik on 17/11/2016.
 */
var avatar;

$( document ).ready(function() {
    //displayAvatarPicker();
    if(localStorage.getItem("account") == null || localStorage.getItem("token") == null)
        return;

    var account = JSON.parse(localStorage.getItem("account"));
    $("#headerName").text(account.firstName + " " + account.lastName);
    $("#nameText").text(account.firstName);
    $("#surnameText").text(account.lastName);
    $("#userEmailText").text(account.email);
    if(account.staticAvatarFilename != null)
        avatar = account.staticAvatarFilename;
    else{
        avatar = 'images/icons/avatars/avatar1.png';
        console.log("Avatar is not set on the server side");
    }
    $(".avatar-flag").attr('src', avatar);
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

function displayAvatarPicker(){
    $(".dialog--dim").removeClass("hidden");
}

function  changeAvatar(caller) {
   var avatarFile = $(caller).attr("src");
    var user = JSON.parse(localStorage.getItem("account"));
    user.staticAvatarFilename = avatarFile;
    console.log(avatarFile);


    $.ajax({
        url: "/api/accounts/"+user.accountId,
        type: "PUT",
        data:JSON.stringify({
            firstName: user.firstName,
            lastName: user.lastName,
            staticAvatarFilename: user.staticAvatarFilename
        }),
        contentType:"application/json; charset=utf-8",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("token"));
        },
        success: function (data) {
            localStorage.setItem("account",JSON.stringify(user));
            $(".avatar-flag").attr('src', user.staticAvatarFilename);
            $(".dialog--dim").addClass("hidden");
        },
        error: function (xhr) {
            if(xhr.status == 401){
                window.location.replace("index.html");
            }else{
                showMessage("Error "+xhr.status+"! Unable to change the avatar!")
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
    var html = "<div class='card-row'><a href='project_detail.html?projectId="+project.projectId+"'><h3 class='float--left'>";
    html = html + "<h3 class='float--left'>"+project.name+"</h3>";
    html = html +"<p class='float--right'>></p><div class='float--clear'></div></a></div>";
    return html;
}


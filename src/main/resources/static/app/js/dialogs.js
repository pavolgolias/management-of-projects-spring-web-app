/**
 * Created by Patrik on 14/11/2016.
 */

function showMessage(message){
    if($(".dialog.hidden").length){
        if($("#message").length){
            $("#message").text(message);
            $(".dialog.hidden").attr('style', 'display: block !important');
            setTimeout(function () {
            $(".dialog.hidden").attr('style', 'display: none !important');

            },3000);
        }
        else alert(message);
    }else{
        alert(message);
    }
}

function showMessageWithoutAutoHide(message){
    if($(".dialog.hidden").length){
        if($("#message").length){
            $("#message").text(message);
            $(".dialog.hidden").attr('style', 'display: block !important');
        }
        else alert(message);
    }else{
        alert(message);
    }
}

function hideMessage(){
    $(".dialog.hidden").attr('style', 'display: none !important');
}

function showNotAuthorizedMessage(){
    showMessage("You are not authorized!");
    window.location.replace("index.html");
}
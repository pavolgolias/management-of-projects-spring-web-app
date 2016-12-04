/**
 * Created by Tomas on 26-Oct-16.
 */
$( document ).ready(function() {

	var account = localStorage.getItem("account");
	if(account == null)
		return;

	$("#side-navbar").find("header img").attr('src',JSON.parse(account).staticAvatarFilename);
});

// Set dialog event
$('.dialog--dim').click(function() {
	$('.dialog--dim').addClass('hidden');
});

$('.dialog').click(function(event){
    event.stopPropagation();
});

function openNav() {
	$('.side-navbar').width(300);
}

function closeNav() {
	$('.side-navbar').width(0);
}
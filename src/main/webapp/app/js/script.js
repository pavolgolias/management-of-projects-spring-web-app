/**
 * Created by Tomas on 26-Oct-16.
 */

// Set dialog event
$(window).click(function() {
	$('.dialog').addClass('hidden');
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
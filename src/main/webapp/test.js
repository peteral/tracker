/**
 * 
 */

$("#send").on("click", function(event) {
	$.get("/rest/times/report/" + $("#input").val())
	.done(function( data ) {
	    $( "#result" ).html( data );
	});
});

$("#list").on("click", function(event) {
	$.get("/rest/times/list")
	.done( function( data ) {
	    $( "#result" ).html( data );
	});
});
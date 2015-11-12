/**
 * 
 */

$("#send").on("click", function(event) {
	$.get("/rest/times/report/" + $("#input").val())
	.always(function( data ) {
	    $( "#result" ).html( data );
	});
});

$("#list").on("click", function(event) {
	$.get("/rest/times/list")
	.always( function( data ) {
	    $( "#result" ).html( data );
	});
});
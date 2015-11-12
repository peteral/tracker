/**
 * 
 */

$("#send").on("click", function(event) {
	$.get("/rest/times/report/" + $("#input").val(), function( data, status ) {
	    $( "#result" ).html( data );
	});
});

$("#list").on("click", function(event) {
	$.get("/rest/times/list", function( data, status ) {
	    $( "#result" ).html( data );
	});
});
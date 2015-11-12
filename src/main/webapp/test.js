/**
 * 
 */

$("#send").on("click", function(event) {
	$.get("/rest/times/report/" + $("#input").val())
	.done(function( data ) {
	    $( "#result" ).html( "Reported..." );
	});
});

$("#list").on("click", function(event) {
	$.get("/rest/times/list")
	.done( function( data ) {
		var result = $( "#result" );
		result.html( "<table><tr><th>Client<th>Time" );
		
		var data = JSON.parse(data.responseText);
		
		for (entry in data.entries) {
			result.html( result.html() + "<tr><td>" + entry.clientId + "<td>" + entry.time );
		}
			
		result.html( result.html() + "</table>" );
	});
});
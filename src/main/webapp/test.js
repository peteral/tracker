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
		
		for (i in data.entries) {
			var entry = data.entries[i];
			result.html( result.html() + "<tr><td>" + entry.clientId + "<td>" + new Time(entry.time.$numberLong) );
		}
			
		result.html( result.html() + "</table>" );
	});
});
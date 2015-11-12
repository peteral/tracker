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
		var tableData = "<table><tr><th>Client<th>Time";
		
		for (i in data.entries) {
			var entry = data.entries[i];
			tableData += "<tr><td>" + entry.clientId + "<td>" + new Date(entry.time.$numberLong);
		}
			
		tableData += "</table>";
		result.html( tableData );
	});
});
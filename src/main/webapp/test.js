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
		var result = "<ul>";
		
		for (i in data.entries) {
			var client = data.entries[i];
			
			result += "<li>" + client.clientId + "<ul>";
			
			for (j in client.days) {
				var day = client.days[j];
				result += "<li>" + day.day + " (" + day.duration.$numberLong + " min)<ul>";
				
				for (k in day.sessions) {
					var session = day.sessions[k];
					result += "<li>" + session.duration.$numberLong + " min (" + 
					                   new Date(parseInt(session.start.$numberLong)) + " - " + 
					                   new Date(parseInt(session.end.$numberLong)) + ")";
				}
				result += "</ul></li>";
			}
			
			result += "</ul></li>";
		}
		
		result += "</ul>";
		$( "#result" ).html( result );
	});
});
list = function() {
	$.get("/rest/protected/list")
	.done( function( data ) {
		var result = "<table class='tree'>";
		result += "<tr class='treegrid-1'><td>Aufgezeichnete Zeiten:</td></tr>";
		var id = 2;
		for (i in data.entries) {
			var client = data.entries[i];
			var clientId = id++;
			
			result += "<tr class='treegrid-" + clientId  + " treegrid-parent-1'><td>" + client.clientId + 
				"</td><td>" + client.duration.$numberLong + " min</td></tr>";
			
			for (j in client.days) {
				var dayId = id++;
				var day = client.days[j];
				result += "<tr class='treegrid-" + dayId + " treegrid-parent-" + clientId + "'><td>" + day.day + 
					"</td><td>" + day.duration.$numberLong + " min</td></tr>";
				
				for (k in day.sessions) {
					var session = day.sessions[k];
					var sessionId = id++;
					result += "<tr class='treegrid-" + sessionId + " treegrid-parent-" + dayId + "'><td>" + session.duration.$numberLong + 
						" min</td><td>" + 
	                   new Date(parseInt(session.start.$numberLong)) + "</td><td>" + 
	                   new Date(parseInt(session.end.$numberLong)) + "</td></tr>";
				}
			}
			
		}
		
		result += "</table>";
		$( "#result" ).html( result );
	});
};

$("#list").on("click", function(event) { 
	list(); 
});

$("#csv").on("click", function(event) {
	window.location = "/rest/protected/csv";
});

$( document ).ready(function () {
	$('.tree').treegrid();
	list();
});
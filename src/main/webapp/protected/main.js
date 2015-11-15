numberToString = function(num, chars) {
	var res = "" + num;
	while (res.length < chars) {
		res = "0" + res;
	}
	
	return res;
}

formatTime = function(time) {
	return numberToString(time.getHours(), 2) + ":" + numberToString(time.getMinutes(), 2);
}

list = function() {
	$.get("/rest/protected/list")
	.done( function( data ) {
		var result = "<table class='tree'>";
		var id = 1;
		for (i in data.entries) {
			var client = data.entries[i];
			var clientId = id++;
			
			result += "<tr class='person-row treegrid-" + clientId  + "'><td>" + client.clientId + 
				"</td><td>" + client.duration.$numberLong + " min</td></tr>";
			
			for (j in client.days) {
				var dayId = id++;
				var day = client.days[j];
				result += "<tr class='treegrid-" + dayId + " treegrid-parent-" + clientId + "'><td>" + day.day + 
					"</td><td>" + day.duration.$numberLong + " min</td></tr>";
				
				for (k in day.sessions) {
					var session = day.sessions[k];
					var sessionId = id++;
					
					var start = new Date(parseInt(session.start.$numberLong));
					var end = new Date(parseInt(session.end.$numberLong));
					
					result += "<tr class='treegrid-" + sessionId + " treegrid-parent-" + dayId + "'><td>" +
						formatTime(start) + " - " + formatTime(end) +
						"</td><td>" + session.duration.$numberLong + 
						" min</td></tr>";
				}
			}
			
		}
		
		result += "</table>";
		$( "#result" ).html( result );
		$('.tree').treegrid();
		$('.tree').treegrid('collapseAll');
	});
};

$( document ).ready(function () {
	$("#list").on("click", function(event) { 
		list(); 
	});

	$("#csv").on("click", function(event) {
		window.location = "/rest/protected/csv";
	});
	
	list();
});
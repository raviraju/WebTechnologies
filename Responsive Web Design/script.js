'use strict';
//var symbolData;
var intervalHandle;

function mapValues(value, percent){
	var result;
	var valueRound = value.toFixed(2);
	var percentRound = percent.toFixed(2);
	var textDisplay = valueRound + "(" + percentRound + "% ) ";
	if(percentRound > 0)	//+ve
	{
		result = "<span style=\"color:green;\">&nbsp;" + textDisplay + "</span>";
		return result + "&nbsp;" + "<img src=\"http://cs-server.usc.edu:45678/hw/hw8/images/up.png\" height=\"32\" width=\"32\" >";
	}
	else if(percentRound < 0) // -ve
	{
		result = "<span style=\"color:red;\">" + textDisplay + "</span>";
		return result + "<img src=\"http://cs-server.usc.edu:45678/hw/hw8/images/down.png\" height=\"32\" width=\"32\" >";
	}
	else	//0
	{
		return textDisplay;
	}
}

function mapMarketCap(value){
	var quotient = value/1000000000;
	var in_billion = quotient.toFixed(2);
	if(in_billion <= 0)
	{
		quotient = value/1000000;
		return quotient.toFixed(2) + " Million";
	}
	else
	{
		return in_billion + " Billion";
	}
}

function mapDateValue(dateString){
	//console.log("Parse dateString : ");
	//console.log(moment(dateString).isValid());
	if(moment(dateString).isValid())
	{
		//console.log("dateString : " + moment(dateString).format("DD MMM YYYY HH:mm:ss"));
		return moment(dateString).format("DD MMM YYYY hh:mm:ss a");
	}
	return dateString;	
}

function parseStockDetails(stock_details){
	console.log(stock_details);
	var table = $("<table class=\"table table-striped\" id=\"stockTable_id\"></table>");
	//table.append($("<caption>Stock Details</caption>"));
	
	var tbody = $("<tbody></tbody>");
	tbody.append($( "<tr>	<td class=\"thClass\">Name</td>								<td id=\"StockName\" class=\"tdClass\">" + stock_details.Name + "</td>	</tr>"));
	tbody.append($( "<tr>	<td class=\"thClass\">Symbol</td>							<td id=\"StockSymbol\" class=\"tdClass\">" + stock_details.Symbol + "</td>	</tr>"));
	tbody.append($( "<tr>	<td class=\"thClass\">Last Price</td>						<td id=\"StockPrice\" class=\"tdClass\">" + "$ " + (stock_details.LastPrice).toFixed(2) + "</td>	</tr>"));
	
	tbody.append($( "<tr>	<td class=\"thClass\">Change (Change Percent)</td>			<td id=\"StockChange\" class=\"tdClass\">" + mapValues(stock_details.Change, stock_details.ChangePercent) + "</td>	</tr>"));
	
	tbody.append($( "<tr>	<td class=\"thClass\">Time and Date</td>					<td class=\"tdClass\">" + mapDateValue(stock_details.Timestamp) + "</td>	</tr>"));
	tbody.append($( "<tr>	<td class=\"thClass\">Market Cap</td>						<td id=\"StockMarketCap\" class=\"tdClass\">" + mapMarketCap(stock_details.MarketCap) + "</td>	</tr>"));
	tbody.append($( "<tr>	<td class=\"thClass\">Volume</td>							<td class=\"tdClass\">" + (stock_details.Volume) + "</td>	</tr>"));
	
	tbody.append($( "<tr>	<td class=\"thClass\">Change YTD(Change Percent YTD)</td>	<td class=\"tdClass\">" + mapValues(stock_details.ChangeYTD, stock_details.ChangePercentYTD) + "</td>	</tr>"));
	
	tbody.append($( "<tr>	<td class=\"thClass\">High Price</td>						<td class=\"tdClass\">" + "$ " + (stock_details.High).toFixed(2) + "</td>	</tr>"));
	tbody.append($( "<tr>	<td class=\"thClass\">Low Price</td>						<td class=\"tdClass\">" + "$ " + (stock_details.Low).toFixed(2) + "</td>	</tr>"));
	tbody.append($( "<tr>	<td class=\"thClass\">Opening Price</td>					<td class=\"tdClass\">" + "$ " + (stock_details.Open).toFixed(2) + "</td>	</tr>"));

	
	table.append(tbody);
	$("#stockTable").html(table);
	enableStockDisplay(true);
}

//current stock tab and in the facebook feed dialog
function getYahooDailyStockChart(symbol){
	var chartWidth = 400;
	var chartHeight = 267;
	var chartImgSrc = "http://chart.finance.yahoo.com/t?";
	chartImgSrc += "s=" + symbol;
	chartImgSrc += "&lang=en-US&";
	chartImgSrc += "width=" + chartWidth;
	chartImgSrc += "&height=" + chartHeight;
	var img = $("<img/>");
	img.attr({ src: chartImgSrc, class: "img-responsive center-block" ,title: "YahooDailyStockChart", width: chartWidth, height: chartHeight});
	//console.log(img);
	$("#stockChart").html(img);
	return false;
}

function mapDate(dateIn) {
    var dat = new Date(dateIn);
    return Date.UTC(dat.getFullYear(), dat.getMonth(), dat.getDate());
};

function getOHLC(json) {
    var dates = json.Dates || [];
    var elements = json.Elements || [];
    var chartSeries = [];

    if (elements[0]){

        for (var i = 0, datLen = dates.length; i < datLen; i++) {
            var dat = mapDate( dates[i] );
            var pointData = [
                dat,
                elements[0].DataSeries['open'].values[i],
                elements[0].DataSeries['high'].values[i],
                elements[0].DataSeries['low'].values[i],
                elements[0].DataSeries['close'].values[i]
            ];
            chartSeries.push( pointData );
        };
    }
    return chartSeries;
};

function parseStockHistory(symbol, stock_history){
	console.log("stock_history : ");
	console.log(stock_history);
	var ohlc = getOHLC(stock_history);
	// create the chart
    $('#chartContainer').highcharts('StockChart', {
        
        rangeSelector: {
			allButtonsEnabled: true,
            buttons: [{
						type: 'week',
						count: 1,
						text: '1w'
					}, {
						type: 'month',
						count: 1,
						text: '1m'
					}, {
						type: 'month',
						count: 3,
						text: '3m'
					}, {
						type: 'month',
						count: 6,
						text: '6m'
					}, {
						type: 'ytd',
						text: 'YTD'
					}, {
						type: 'year',
						count: 1,
						text: '1y'
					}, {
						type: 'all',
						text: 'All'
					}],
			selected: 0,					//to select week range by default
			inputEnabled: false				//to disable data entry/display
        },

        title: {
            text: symbol + ' Stock Value'
        },
		
		xAxis: [{
            title: {
                text: 'Date Time'
            },
            height: 200,
            lineWidth: 2
        }],

        yAxis: [{
            title: {
                text: 'Stock Value'
            },
            height: 200,
            lineWidth: 2
        }],
        
        series: [{
            type: 'area',
            name: symbol,
            data: ohlc,
			threshold : null,
			tooltip : {
				valuePrefix : "$",
				valueDecimals : 2
			},
			fillColor : {
				linearGradient : {
					x1: 0,
					y1: 0,
					x2: 0,
					y2: 1
				},
				stops : [
					[0, Highcharts.getOptions().colors[0]],
					[1, Highcharts.Color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
				]
			}
        }]
    });	
}

function getInteractiveChartData(user_symbol){	

	$.ajax({
			beforeSend:function(){
				$("#chartDemoContainer").text("Loading chart...");
			},
			url: "server.php",
			type: "GET",
			dataType: "json",
			data: {
				symbol: user_symbol,
				search: "get_chartData"
				},
			success: function(stock_history) {
						parseStockHistory(user_symbol, stock_history);
						return;
					},
			error: function(response,txtStatus){
						console.log(response,txtStatus);
						return;
					}
		});
}

function parseStockNews(stock_news){
	console.log("stock_news : ");
	console.log(stock_news);
	var news = stock_news.d.results;
	//console.log(news);
    var news_section = $("<div></div>");
	for (var i = 0, newsLen = news.length; i < newsLen; i++){
		/*var url  		= news[i]['Url'];
		var title  		= news[i]['Title'];
		var content  	= news[i]['Description'];
		var publisher  	= news[i]['Source'];
		var date 		= news[i]['Date'];
		console.log(url, title, content, publisher, date);*/
        var news_div = $("<div class=\"well well-lg\"></div>");
        var news_link   = $("<a target=\"_blank\" href=\"" + news[i]['Url'] + "\">" + news[i]['Title'] + "</a>");//removed style=\"text-decoration:none\"
        news_div.append(news_link);
        var description = $("<p>" + news[i]['Description'] + "</p>")
        news_div.append(description);
		var publisher   = $("<p><strong>Publisher: " + news[i]['Source'] + "</strong></p>");
        news_div.append(publisher);
        var date        = $("<p><strong>Date: " + mapDateValue(news[i]['Date']) + "</strong></p>");
        news_div.append(date);
        news_section.append(news_div);
	}
     $("#newsContainer").html(news_section);
}

function getBingNews(user_symbol){	

	$.ajax({
			url: "server.php",
			type: "GET",
			dataType: "json",
			data: {
				symbol: user_symbol,
				search: "get_bingNews"
				},
			success: function(stock_news) {
						parseStockNews(stock_news);
						return;
					},
			error: function(response,txtStatus,errorThrown ){
						console.log(response,txtStatus,errorThrown);
						return;
					}
		});
}

function chooseFavBtn(symbol){
	if(localStorage.getItem('favStocks')){
		if (findSymbol_FavList(symbol)){
			console.log("stock " + symbol + " is one of favorites");
			$("#favBtnColor").css("color", "yellow");
		}
		else{
			console.log("stock " + symbol + " is NOT one of favorites");
			$("#favBtnColor").css("color", "white");
		}
	}else{
		console.log("stock " + symbol + " is NOT one of favorites, as Favourites are empty");
		$("#favBtnColor").css("color", "white");
	}
}

function handleValidSelection(user_symbol){
	$.ajax({
			url: "server.php",
			type: "GET",
			dataType: "json",
			data: {
				symbol: user_symbol,
				search: "get_quote"
				},
			success: function(stock_details) {						
						if(stock_details.Status === "SUCCESS"){
							parseStockDetails(stock_details);
							chooseFavBtn(stock_details.Symbol);
							if (findSymbol_FavList(stock_details.Symbol)){
								console.log("stock " + stock_details.Symbol + " is one of favorites");
								//update values in Favorite table
								$("#" + stock_details.Symbol +"_StockPrice").html("$ " + (stock_details.LastPrice).toFixed(2));	
								$("#" + stock_details.Symbol +"_StockChange").html(mapValues(stock_details.Change, stock_details.ChangePercent));
								
							}
							getYahooDailyStockChart(stock_details.Symbol);
							getInteractiveChartData(stock_details.Symbol);
							getBingNews(stock_details.Symbol);
							console.log("current : " + $('div.active').attr('id'));
							if($('div.active').attr('id') === "fav")
								$("#featured").carousel("next");
						}else{
							//alert("Cannot fetch stock details due to : \n" + stock_details.Status);
							$("#msg").text("Cannot fetch stock details due to : " + stock_details.Status).show().fadeOut( 1000 );
						}
					},
			error: function(response,txtStatus){
						console.log(response,txtStatus);
					}
		});
}

function findSymbol_FavList(symbol){
	console.log("findSymbol_FavList");
	var found = false;
	if(localStorage.getItem('favStocks')){
		var existingFavStocksList = JSON.parse(localStorage.getItem('favStocks'));
		console.log("Current Fav Stocks List");
		console.log(existingFavStocksList);

		console.log("symbol : " + symbol);
		for(var i=0; i<existingFavStocksList.length; i++){
			if(existingFavStocksList[i] === symbol )
			{
				found = true;
				break;
			}
		}
	}
	return found;
	
}

function handleFavoriteStock(){
	console.log("handleFavoriteStock");
	
	var stockSymbol = $("#StockSymbol").text();
	
	//check if stockSymbol already in favorites
	//yes:delete stockSymbol from favorites
	//no:add stockSymbol to favorites
	if(localStorage.getItem('favStocks')){
		if(findSymbol_FavList(stockSymbol) === true ){
			deleteFavStock(stockSymbol);
			console.log("Deleted : " + stockSymbol + " from favourites");			
		}else{//append to existing favorites
			var existingFavStocksList = JSON.parse(localStorage.getItem('favStocks'));
			existingFavStocksList.push(stockSymbol);		
			localStorage.setItem('favStocks', JSON.stringify(existingFavStocksList));			
			console.log("Updated Fav Stocks List");
			
			$.ajax({
				url: "server.php",
				type: "GET",
				dataType: "json",
				data: {
					symbol: stockSymbol,
					search: "get_quote"
					},
				success: function(stock_details) {							
							if(stock_details.Status === "SUCCESS"){
								var trow = $("<tr id=\"" + stockSymbol + "\"></tr>");
								trow.append($( "<td>" + "<button type = \"button\" class = \"btn btn-link\" onclick=\"handleValidSelection('" + stockSymbol + "')\">" + stock_details.Symbol + "</button>" + "</td>"));
								trow.append($( "<td>" + stock_details.Name 		+ "</td>"));
								trow.append($( "<td id=\"" + stockSymbol + "_StockPrice\">" + "$ " + (stock_details.LastPrice).toFixed(2) + "</td>"));
								trow.append($( "<td id=\"" + stockSymbol + "_StockChange\">" + mapValues(stock_details.Change, stock_details.ChangePercent) + "</td>"));
								trow.append($( "<td>" + mapMarketCap(stock_details.MarketCap) 	+ "</td>"));			
								trow.append($( "<td>" + "<button onclick=deleteFavStock('" + stockSymbol + "') id=\"trashBtn\" type=\"button\" class=\"btn btn-default\" aria-label=\"Delete\"><span class=\"glyphicon glyphicon-trash\" aria-hidden=\"true\"></span></button>" + "</td>"));			

								$("#favTable_id tbody").append(trow);
								console.log("Updated Fav Stocks Table");

							}
						},
				error: function(response,txtStatus){
							console.log(response,txtStatus);
						}
			});
		}		
	}else{//no favorites exists so far, so thus must be add action
		var newFavStocksList = [stockSymbol];
		localStorage.setItem('favStocks', JSON.stringify(newFavStocksList));
		console.log("Created Fav Stocks List");
		displayFavoriteStocks();
	}
	chooseFavBtn(stockSymbol);	
}

function deleteFavStock(symbol){
	console.log("Fav Stock to be deleted : " + symbol);
	if(localStorage.getItem('favStocks')){
		var existingFavStocksList = (JSON.parse(localStorage.getItem('favStocks')));
		var newFavStocksList = []
		for(var i=0; i<existingFavStocksList.length; i++){
			var cur_symbol = existingFavStocksList[i];
			if( cur_symbol === symbol ){
				$("#" + symbol).remove();
				continue;
			}
			else{
				newFavStocksList.push(cur_symbol);
			}
		}
		localStorage.setItem('favStocks', JSON.stringify(newFavStocksList));
	}
	chooseFavBtn(symbol);
	//displayFavoriteStocks();
}

function populateFavTable(details_of_stocks){

	/*var table = $("<table class=\"table table-striped\" id=\"favTable_id\"></table>");
	//table.append($("<caption>Favorite List</caption>"));
	var thead = $("<thead><tr>	<th>Symbol</th> <th>Company Name</th> <th>Stock Price</th> <th>Change (Change Percent)</th> <th>Market Cap</th> </tr></thead>");
	table.append(thead);*/
	var table = $("#favTable_id");
	if(localStorage.getItem('favStocks')){
		var existingFavStocksList = JSON.parse(localStorage.getItem('favStocks'));
		console.log("Current Fav Stocks List");
		console.log(existingFavStocksList);
		
		var tbody = $("<tbody></tbody>");
		for(var i=0; i<existingFavStocksList.length; i++){
			var symbol = existingFavStocksList[i];
			var trow = $("<tr id=\"" + symbol + "\"></tr>");
			trow.append($( "<td>" + "<button type = \"button\" class = \"btn btn-link\" onclick=\"handleValidSelection('" + symbol + "')\">" + details_of_stocks[symbol].Symbol + "</button>" + "</td>"));
			trow.append($( "<td>" + details_of_stocks[symbol].Name 		+ "</td>"));
			trow.append($( "<td id=\"" + symbol + "_StockPrice\">" + "$ " + (details_of_stocks[symbol].LastPrice).toFixed(2) + "</td>"));
			trow.append($( "<td id=\"" + symbol + "_StockChange\">" + mapValues(details_of_stocks[symbol].Change, details_of_stocks[symbol].ChangePercent) + "</td>"));
			trow.append($( "<td>" + mapMarketCap(details_of_stocks[symbol].MarketCap) 	+ "</td>"));
			
			trow.append($( "<td>" + "<button onclick=deleteFavStock('" + symbol + "') id=\"trashBtn\" type=\"button\" class=\"btn btn-default\" aria-label=\"Delete\"><span class=\"glyphicon glyphicon-trash\" aria-hidden=\"true\"></span></button>" + "</td>"));			
			tbody.append(trow);
		}
		table.append(tbody);
	}
	$("#favStocks").html(table);
}

function displayFavoriteStocks(){
	if(localStorage.getItem('favStocks')){
		$.ajax({
			url: "server.php",
			type: "GET",
			dataType: "json",
			data: {
				symbol: localStorage.getItem('favStocks'),
				search: "get_quotes"
				},
			success: function(details_of_stocks) {
						console.log(details_of_stocks)
						populateFavTable(details_of_stocks);
					},
			error: function(response,txtStatus){
						console.log(response,txtStatus);
					}
		});
	}	
}

function updatePriceChange(details_of_stocks){
	if(localStorage.getItem('favStocks')){
		var existingFavStocksList = JSON.parse(localStorage.getItem('favStocks'));
		console.log("Current Fav Stocks List");
		console.log(existingFavStocksList);
		
		for(var i=0; i<existingFavStocksList.length; i++){
			var symbol = existingFavStocksList[i];
			$("#" + symbol +"_StockPrice").html("$ " + (details_of_stocks[symbol].LastPrice).toFixed(2));	
			$("#" + symbol +"_StockChange").html(mapValues(details_of_stocks[symbol].Change, details_of_stocks[symbol].ChangePercent));
		}
	}
}

function refreshFavStocks(){
	console.log("Refresh Current Fav Stocks");
	if(localStorage.getItem('favStocks')){
		$.ajax({
			url: "server.php",
			type: "GET",
			dataType: "json",
			data: {
				symbol: localStorage.getItem('favStocks'),
				search: "get_quotes"
				},
			success: function(details_of_stocks) {
						console.log(details_of_stocks)
						updatePriceChange(details_of_stocks);
					},
			error: function(response,txtStatus){
						console.log(response,txtStatus);
					}
		});
	}
}

function enableStockDisplay(enable){
	if(enable === true){
		console.log("enable goBtn");
		$("#goBtn").prop("disabled",false);
	}else{
		console.log("disable goBtn");
		$("#goBtn").prop("disabled",true);
	}
}

function handlePost(){
	var symbol = $("#StockSymbol").text();
	$.ajax({
		url: "server.php",
		type: "GET",
		dataType: "json",
		data: {
			symbol: symbol,
			search: "get_quote"
			},
		success: function(stock_details) {					
					if(stock_details.Status === "SUCCESS"){
						var title = "Current Stock Price of " + stock_details.Name + " is " + "$ " + (stock_details.LastPrice).toFixed(2);
						var sub_title = "Stock Information of " + stock_details.Name + " (" + stock_details.Symbol + ")";
						var value = stock_details.Change;
						var percent = stock_details.ChangePercent;
						var caption_str = "LAST TRADE PRICE: " + "$ " + (stock_details.LastPrice).toFixed(2) + ", CHANGE: " + value.toFixed(2) + " (" + percent.toFixed(2) + "% ) ";
						
						var chartImgSrc = "http://chart.finance.yahoo.com/t?";
						chartImgSrc += "s=" + symbol;
						chartImgSrc += "&lang=en-US";
						console.log(title);
						console.log(sub_title);
						console.log(chartImgSrc);
						console.log(caption_str);
						
						FB.ui({
							method: 'feed',
							link: 'http://dev.markitondemand.com/',
							name: title,
							picture: chartImgSrc,
							description: sub_title,
							caption: caption_str
						}, function(response){
							console.log(response);
							if(response && response.post_id){
								alert("Posted Successfully");
							}else{
								alert("Not Posted");
							}
						});
					}
				},
		error: function(response,txtStatus){
					console.log(response,txtStatus);
					alert("Not Posted");
				}
	});	
}

function checkValidSymbol(user_symbol){
	console.log("checkValidSymbol : "  + user_symbol);
	$.ajax({
		url: "server.php",
		type: "GET",
		dataType: "json",
		data: {
			symbol: user_symbol
			},
		success: function(matchedSymbols) {
					console.log(matchedSymbols);
					for(var i=0; i<matchedSymbols.length; i++){
						if(user_symbol === matchedSymbols[i].Symbol){
							console.log(user_symbol + " is valid");
							handleValidSelection(user_symbol);
							return;
						}
					}
					console.log(user_symbol + " is in-valid");
					$("#msg").text("Select a valid entry").show().fadeOut( 1000 );
					return false;
				},
		error: function(response,txtStatus){
					console.log(response,txtStatus);
					console.log(user_symbol + " is in-valid");
					$("#msg").text("Select a valid entry").show().fadeOut( 3000 );
					return false;
		}
	});
}

$("document").ready(function() {
	
	displayFavoriteStocks();
	
	$("#favBtn").click(handleFavoriteStock);
	$("#fbBtn").click(handlePost);
		
	$("#symbol")
			.focus()
			.autocomplete({
							source: function(request,response) {
									$.ajax({
										url: "server.php",
										type: "GET",
										dataType: "json",
										data: {
											symbol: request.term
											},
										success: function(data) {
													console.log(data);
													//symbolData = data;
													response( $.map(data, function(item) {
														//console.log(item);
														return {
															label: item.Symbol + " - " + item.Name + " (" +item.Exchange+ ")",
															value: item.Symbol
																}
															}) );
												},
										error: function(response,txtStatus){
											console.log(response,txtStatus)
										}
									});
							}
						});
						
	$("#srcForm").on('submit',function(){
							var user_symbol = $("#symbol").val();
							console.log("on submit " + user_symbol);
							checkValidSymbol(user_symbol.toUpperCase());
							return false;
							/*console.log(symbolData);
							for(var i=0; i < symbolData.length; ++i)
							{
								if( user_symbol === symbolData[i].Symbol)
								{
									handleValidSelection(user_symbol);
									return false;
								}
							}
							$("#msg").text("Select a valid entry").show().fadeOut( 1000 );
							return false;*/
						});
	$("#srcForm").on('reset',function(){
		enableStockDisplay(false);
	});
	
	if( $('#stockTable').is(':empty') ) {
		enableStockDisplay(false);
	}else{
		enableStockDisplay(true);
	}
	$("#goBtn").click(function(){
		$("#featured").carousel("next");
	});
	$("#backBtn").click(function(){
		$("#featured").carousel("prev");
	});
	
	$("#refreshBtn").click(refreshFavStocks);
	if($('#toggle-trigger').prop('checked') === true){//on page reload already auto refresh on
		console.log('Automatic Refresh: ' + $('#toggle-trigger').prop('checked'));
		intervalHandle = setInterval(refreshFavStocks, 5000);
	}
	$('#toggle-trigger').change(function() {
      //console.log('Toggle: ' + $(this).prop('checked'));
	  if($(this).prop('checked') === true){
		  console.log('Automatic Refresh: ' + $(this).prop('checked'));
		  intervalHandle = setInterval(refreshFavStocks, 5000);
	  }else{
		  console.log('Automatic Refresh: ' + $(this).prop('checked'));
		  clearInterval(intervalHandle);
	  }
    })
});

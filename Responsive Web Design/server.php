<?php
function dump($array){
	//echo 'Count : ' . $array->count();
	echo '<pre>';
	print_r($array);
	echo '</pre>';
}
?>
<?php
	//dump($_GET);
	if ( (isset($_GET['symbol'])) && !(isset($_GET['search'])) )
	{
		$symbol = trim($_GET['symbol']);
		$url  = "http://dev.markitondemand.com/";
		$url_page = "MODApis/Api/v2/Lookup/json";
		$url .= rawurlencode($url_page);
		$url .= "?" . "input=" . urlencode($symbol);
		//'http://dev.markitondemand.com/MODApis/Api/v2/Lookup/json?input=APPL';
		
		$contents = file_get_contents($url); 
		$contents = utf8_encode($contents); 
		$results = json_decode($contents, true); 
		if (json_last_error() === JSON_ERROR_NONE) { 
			//It's ready to use 
			//dump($results);
			print json_encode($results);
		}else { 
			//it's not JSON. Log error
			echo json_last_error_msg();
		}
	}
	if(isset($_GET['search']))
	{
		$srch_type = $_GET['search'];
		if($srch_type == "get_quote")
		{
			$quote_url  = "http://dev.markitondemand.com/";
			$quote_url_page = "MODApis/Api/v2/Quote/json";
			$quote_url .= rawurlencode($quote_url_page);
			$quote_url .= "?" . "symbol=" . urlencode($_GET['symbol']);
			//http://dev.markitondemand.com/MODApis/Api/v2/Quote/json?symbol=AAPL
			
			$contents = file_get_contents($quote_url); 
			$contents = utf8_encode($contents); 
			$results = json_decode($contents, true); 
			if (json_last_error() === JSON_ERROR_NONE) { 
				//It's ready to use 
				//dump($results);
				print json_encode($results);
			} else { 
				//it's not JSON. Log error
				echo json_last_error_msg();
			}
		}
		else if($srch_type == "get_quotes")
		{
			//dump($_GET['symbol']);
			//var_dump($_GET['symbol']);
			$queries = json_decode($_GET['symbol'], true); 
			//var_dump($queries);
			foreach ($queries as $symbol){
				//echo $symbol;
				$quote_url  = "http://dev.markitondemand.com/";
				$quote_url_page = "MODApis/Api/v2/Quote/json";
				$quote_url .= rawurlencode($quote_url_page);
				$quote_url .= "?" . "symbol=" . urlencode($symbol);
				$contents = file_get_contents($quote_url); 
				$contents = utf8_encode($contents); 
				$results = json_decode($contents, true); 
				if (json_last_error() === JSON_ERROR_NONE) { 
					//It's ready to use 
					//dump($results);
					//print json_encode($results);
					$all_results[$symbol] = $results;
				} else { 
					//it's not JSON. Log error
					$all_results[$symbol] = json_last_error_msg();
				}
			}
			//var_dump($all_results);
			print json_encode($all_results);
		}
		else if($srch_type == "get_chartData")
		{
						
			$chart_url  = "http://dev.markitondemand.com/";
			$chart_url_page = "MODApis/Api/v2/InteractiveChart/json";
			$chart_url .= rawurlencode($chart_url_page);						//1095 : 3*365 : 3 Years
			$chart_url .= "?" . "parameters={\"Normalized\":false,\"NumberOfDays\":1095,\"DataPeriod\":\"Day\",\"Elements\":[{\"Symbol\":\"";
			$chart_url .= urlencode($_GET['symbol']);
			$chart_url .= "\",\"Type\":\"price\",\"Params\":[\"ohlc\"]}]}";
			
			//http://dev.markitondemand.com/MODApis/Api/v2/InteractiveChart/json?parameters=
			//{"Normalized":false,"NumberOfDays":1095,"DataPeriod":"Day","Elements":[{"Symbol":"
			//AAPL
			//","Type":"price","Params":["ohlc"]}]}
			
			$contents = file_get_contents($chart_url); 
			$contents = utf8_encode($contents); 
			$results = json_decode($contents, true); 
			if (json_last_error() === JSON_ERROR_NONE) { 
				//It's ready to use 
				//dump($results);
				print json_encode($results);
			} else { 
				//it's not JSON. Log error
				echo json_last_error_msg();
			}
		}
		else if($srch_type == "get_bingNews")
		{
			//https://api.datamarket.azure.com/Bing/Search/v1/News?Query=%27AAPL%27&$format=json
			$accountKey = '8VnPPRbA5WDf2Db+c+fLQMh4kMz3vljrO5ChCAiGGGI';
			$ServiceRootURL =  'https://api.datamarket.azure.com/Bing/Search/';
			$WebSearchURL = $ServiceRootURL . 'News?$format=json&Query=';
			$context = stream_context_create(array(
				'http' => array(
					'request_fulluri' => true,
					'header'  => "Authorization: Basic " . base64_encode($accountKey . ":" . $accountKey)
				)
			));
			$request = $WebSearchURL . urlencode( '\'' . $_GET['symbol'] . '\'');
			//echo($request);
			
			$response = file_get_contents($request, 0, $context);
			
			$results = json_decode($response);
		
			if (json_last_error() === JSON_ERROR_NONE) { 
				//It's ready to use 
				//dump($results);
				print json_encode($results);
			} else { 
				//it's not JSON. Log error
				echo json_last_error_msg();
			}
		}
		else{
			echo json_encode("invalid get request");
		}
	}
?>
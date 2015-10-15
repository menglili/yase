<?php
/////////////////////////////////////////////////////////////////////////////////////////////////////////
// jQueryUI Autocomplete with multiple search engines.
// Author: Abhinay Rathore
// Website: http://www.AbhinayRathore.com
// Blog: http://web3o.blogspot.com
// Last Updated: Oct 1, 2011
/////////////////////////////////////////////////////////////////////////////////////////////////////////
 
//Search term
$term = $_REQUEST['term'];
//Search Engine array
$searchEngines = array(
    "Google" => array("http://suggestqueries.google.com/complete/search?output=firefox&client=firefox&q=", "http://www.google.com/search?q="),
    //"Bing" => array("http://api.bing.com/osjson.aspx?query=", "http://www.bing.com/search?q="),
    //"Yahoo" => array("http://ff.search.yahoo.com/gossip?output=fxjson&command=", "http://search.yahoo.com/search?p="),
    //"Wikipedia" => array("http://en.wikipedia.org/w/api.php?action=opensearch&search=", "http://en.wikipedia.org/w/index.php?title=Special%3ASearch&search="),
    //"Ebay" => array("http://anywhere.ebay.com/services/suggest/?q=", "http://shop.ebay.com/i.html?_nkw="),
    //"Amazon" => array("http://completion.amazon.com/search/complete?search-alias=aps&client=amazon-search-ui&mkt=1&q=", "http://www.amazon.com/s/field-keywords=")
     
);
 
//Combine Search Results
$searchArray = array();
foreach($searchEngines as $engine => $urls){
    $url = $urls[0] . rawurlencode($term);
    try{
        //$json = file_get_contents($url);
        $json = get_url_contents($url);
        $array = json_decode($json);
        $array = $array[1]; //$array[1] contains result list
        if(count($array) > 0){
            $array = getFormattedArray($array, $engine, $urls[1]);
            $searchArray = array_merge($searchArray, $array );
        }
    } catch (Exception $e){ /* Skip the exception */ }
}
 
//Output JSON
header('content-type: application/json; charset=utf-8');
echo json_encode($searchArray); //Convert array to JSON object
 
//Format array to add category (search engine name)
function getFormattedArray($array, $engine, $searchUrl){
    $newArray = array();
    foreach($array as $a){
        $newArray[] = array('label' => $a, 'searchUrl' => $searchUrl, 'category' => $engine);
    }
    return $newArray;
}
 
//Read URL contents
function get_url_contents($url)
{
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 5);
    $ip=rand(0,255).'.'.rand(0,255).'.'.rand(0,255).'.'.rand(0,255);
    curl_setopt($ch, CURLOPT_HTTPHEADER, array("REMOTE_ADDR: $ip", "HTTP_X_FORWARDED_FOR: $ip"));
    curl_setopt($ch, CURLOPT_USERAGENT, "Mozilla/".rand(3,5).".".rand(0,3)." (Windows NT ".rand(3,5).".".rand(0,2)."; rv:2.0.1) Gecko/20100101 Firefox/".rand(3,5).".0.1");
    $html = curl_exec($ch);
    curl_close($ch);
    return $html;
}
?>
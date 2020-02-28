# Twitter Search
The twitter search step allows searching for tweets. 
There are three tabs to be configured: search, authentication, and output tabs.

### Search Tab
In the search tab, we must configure the search we want to perform.

![alt text](https://github.com/LeonardoCoelho71950/pdi-twitter-plugin/blob/master/docs/screenshots/search-tab.png "Search Tab configuration")

#### Fields
Field  | Description
------------- | -------------
Query  |  Input field that holds the query strings to be executed.
Type  |  Search tweets by the most popular, most recent, or mixed.
Language  | The language in which the tweets should be written.
Hide Sensistive Tweets?  | Allows us to hide sensitive content. 
Only Verified Users?  |  Allows us to ignore non-verified users.

### Authentication Tab
In the authentication tab, we must provide our API credentials. 

![alt text](https://github.com/LeonardoCoelho71950/pdi-twitter-plugin/blob/master/docs/screenshots/auth-tab.png "Authentication Tab configuration")

### Output Tab
In the output tab, we can choose which output fields our queries should produce. If a field is empty, that field won't be materialized. 

![alt text](https://github.com/LeonardoCoelho71950/pdi-twitter-plugin/blob/master/docs/screenshots/output-tab.png "Output Tab configuration")

### Example
In this example, we are searching for the most popular "MLS" (Major Soccer League) tweets, written in English. Those tweets can't have sensitive content and must belong to verified users.

![alt text](https://github.com/LeonardoCoelho71950/pdi-twitter-plugin/blob/master/docs/screenshots/example.png "Search MLS related tweets.")

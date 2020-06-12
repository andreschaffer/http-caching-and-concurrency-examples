![Build](https://github.com/andreschaffer/http-caching-and-concurrency-examples/workflows/Build/badge.svg)
[![Test Coverage](https://api.codeclimate.com/v1/badges/89fa44901c04343aa6a7/test_coverage)](https://codeclimate.com/github/andreschaffer/http-caching-and-concurrency-examples/test_coverage)
[![Maintainability](https://api.codeclimate.com/v1/badges/89fa44901c04343aa6a7/maintainability)](https://codeclimate.com/github/andreschaffer/http-caching-and-concurrency-examples/maintainability)
[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=andreschaffer/http-caching-and-concurrency-examples)](https://dependabot.com)

# Http Caching and Concurrency Examples

This project aims to provide examples of how to use the Http Caching and Concurrency Controls.  
For an in-depth understanding of the topic, one should read the [RFC 7232 - Conditional Requests](https://tools.ietf.org/html/rfc7232).

# The Service
This is a simple weather service with two resources:
- Daylight (today at Stockholm)  
  The idea with this one is to demonstrate the simplest time-based Http Caching Controls.  
  Since the response contains information valid for the whole day, we make use of the headers _Cache-Control_ with _max-age_ of 1 day,
  combined with _Expires_ until the end of the day.
- Climate (now at Stockholm)  
  The idea with this one is to demonstrate Http Conditional Requests and Concurrency Controls based on the _ETag_ header (_Last-Modified_ can also be used with the same purpose).  
  - Conditional Requests were used when retrieving the resource.  
    Given that the client has knowledge about the resource at a given point in time, it can send its corresponding _ETag_
    along with the request so that the server will either respond with the current resource information in case 
    it has changed since, or with a _Not Modified_ response, saving some bandwith and time.
  - Concurrency Controls were used when updating the resource. It is a kind of _Optimistic Locking_ on the Http protocol.   
    Given that the client has knowledge about the resource at a given point in time, it can send its corresponding _ETag_ 
    along with the request so that the server can update the resource whether that _ETag_ matches its current one 
    or refuse to and let the client know that he's trying to update the resource with basis on outdated information.

# Caveat
When using Http Concurrency Controls for Optimistic Locking, pay attention to your _implementation_
not to fall for _the Lost Update Problem_ the same way:  
Make sure to execute the '_retrieve resource, verify ETag then update resource_' operations in a rather atomic way.
That can be done with either a Transaction or Optimistic Locking at the DB level.

# Trying it out
## Requirements
- Java 14
- Maven

## Building the application
```
mvn clean verify
```
## Starting the application
```
java -jar target/weather-service-1.0-SNAPSHOT.jar server src/environments/development.yml
```

## Examples of use
### Retrieving the daylight information
```
curl -vvv http://localhost:8080/daylights/stockholm/today
```
Note the _Cache-Control_ and _Expires_ headers in the response.
```
< HTTP/1.1 200 OK
< Date: Sun, 11 Jun 2017 19:00:55 GMT
< Expires: Sun, 11 Jun 2017 23:59:59 GMT
< Content-Type: application/json
< Cache-Control: no-transform, max-age=86400
< Vary: Accept-Encoding
< Content-Length: 48
< 
{"sunrise":"06:00+02:00","sunset":"18:00+02:00"}
```

### Retrieving the climate information
```
curl -vvv http://localhost:8080/climates/stockholm/now
```
Note the _ETag_ header in the response.
```
< HTTP/1.1 200 OK
< Date: Sun, 11 Jun 2017 19:03:35 GMT
< ETag: "23723"
< Content-Type: application/json
< Vary: Accept-Encoding
< Content-Length: 32
< 
{"temperature":10,"humidity":80}
```

### Retrieving the climate information conditionally
```
curl -vvv http://localhost:8080/climates/stockholm/now -H 'If-None-Match: "23723"'
```
We will get a 200 OK response back whether the _If-None-Match_ header value _doesn't_ match the current corresponding ETag on the server,
or a 304 Not Modified response on the contrary.

### Updating the climate information conditionally
```
curl -vvv -X PUT http://localhost:8080/climates/stockholm/now -H 'If-Match: "23723"' -H 'Content-type: application/json' -d '{"temperature":20,"humidity":90}'
```
We will get a 204 No Content response back whether the _If-Match_ header value _matches_ the current corresponding ETag on the server,
or a 412 Precondition Failed response on the contrary.

# Contributing
If you would like to help making this project better, see the [CONTRIBUTING.md](CONTRIBUTING.md).  

# Maintainers
Send any other comments, flowers and suggestions to [André Schaffer](https://github.com/andreschaffer).

# License
This project is distributed under the [MIT License](LICENSE).

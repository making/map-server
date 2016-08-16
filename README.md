## Map Server

A multi-tenant map server.

This server is intended to be used for a practice to implement a CF Service Broker.


### Run locally

``` console
$ ./mvnw clean package -DskipTests=true
$ java -jar target/map-server-0.0.1-SNAPSHOT.jar --security.user.name=admin --security.user.password=admin
```

### Run on Pivotal Web Services

``` console
$ cf push map-server -p target/map-server-0.0.1-SNAPSHOT.jar -m 512m --no-start -b java_buildpack
$ cf create-service cleardb spark map-server-db
$ cf set-env map-server SECURITY_USER_NAME admin
$ cf set-env map-server SECURITY_USER_PASSWORD admin
$ cf bind-service map-server map-server-db
$ cf start map-server
```


### Space API

#### Create a space

``` console
$ curl -i -u admin:admin http://localhost:8080/spaces -d '{"spaceId":"3e92a22a-e64d-48a4-8413-0a275db1b1f1"}' -H 'Content-Type:application/json'
HTTP/1.1 201 Created
Server: Apache-Coyote/1.1
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Tue, 16 Aug 2016 15:03:46 GMT

{"spaceId":"3e92a22a-e64d-48a4-8413-0a275db1b1f1"}
```

should be called in `cf create-service`.

#### Delete the space

``` console
$ curl -i -u admin:admin -XDELETE http://localhost:8080/spaces/3e92a22a-e64d-48a4-8413-0a275db1b1f1
HTTP/1.1 204 No Content
Server: Apache-Coyote/1.1
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Date: Tue, 16 Aug 2016 15:10:47 GMT

```

should be called in `cf delete-service`.

### User API

#### Create a user in the space

``` console
$ curl -i -u admin:admin -XPOST http://localhost:8080/spaces/3e92a22a-e64d-48a4-8413-0a275db1b1f1/users
HTTP/1.1 201 Created
Server: Apache-Coyote/1.1
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: application/json;charset=UTF-8
Transfer-Encoding: chunked
Date: Tue, 16 Aug 2016 15:04:57 GMT

{"userId":"00380abb-988f-4111-9922-37dd9ba50f20","password":"d7267163-2191-46c2-a65e-05af9024205a","spaceId":"3e92a22a-e64d-48a4-8413-0a275db1b1f1"}
```

should be called in `cf bind-service`.

#### Delete the user in the space

``` console
$ curl -i -u admin:admin -XDELETE http://localhost:8080/spaces/3e92a22a-e64d-48a4-8413-0a275db1/users/00380abb-988f-4111-9922-37dd9ba50f20
HTTP/1.1 204 No Content
Server: Apache-Coyote/1.1
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Date: Tue, 16 Aug 2016 15:14:28 GMT

```

should be called in `cf unbind-service`.


### Map API

#### Put a key-value pair in the space

``` console
$ curl -i -u 00380abb-988f-4111-9922-37dd9ba50f20:d7267163-2191-46c2-a65e-05af9024205a -XPOST http://localhost:8080/spaces/3e92a22a-e64d-48a4-8413-0a275db1b1f1/map/key1 -d value1 -H 'Content-Type: plain/text'
HTTP/1.1 204 No Content
Server: Apache-Coyote/1.1
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Date: Tue, 16 Aug 2016 15:07:45 GMT


```

#### Get the value associated the given key in the space


``` console
$ curl -i -u 00380abb-988f-4111-9922-37dd9ba50f20:d7267163-2191-46c2-a65e-05af9024205a -XGET http://localhost:8080/spaces/3e92a22a-e64d-48a4-8413-0a275db1b1f1/map/key1
HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: text/plain;charset=UTF-8
Content-Length: 6
Date: Tue, 16 Aug 2016 15:08:57 GMT

value1
```

#### Delete a key-value pair in the space

``` console
$ curl -i -u 00380abb-988f-4111-9922-37dd9ba50f20:d7267163-2191-46c2-a65e-05af9024205a -XDELETE http://localhost:8080/spaces/3e92a22a-e64d-48a4-8413-0a275db1b1f1/map/key1
HTTP/1.1 204 No Content
Server: Apache-Coyote/1.1
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Date: Tue, 16 Aug 2016 15:10:03 GMT

```


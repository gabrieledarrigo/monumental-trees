# Italian monumental trees API

> A REST API for Italian monumental trees 🌲🌳

![Build and release](https://github.com/gabrieledarrigo/monumental-trees/workflows/Build%20and%20release/badge.svg)

## Foreword

Monumental trees API is a project for the Service Oriented Architecture course of ["Sicurezza dei Sistemi e Delle Reti Informatiche"](http://sicurezzaonline.di.unimi.it/) bachelor's degree program. 

At its heart is a Kotlin REST API application developed with [Spring Boot](https://spring.io/projects/spring-boot).  
It exposes a _single_ resource, Italian monumental trees, that are eligible trees, by the Italian ["Ministero delle politiche agricole alimentari e forestali"](https://www.politicheagricole.it/) (from now, Mipaaf), to be protected because of their naturalistic, historic, cultural or landscaped values.  

An authenticated client (more on this in the related section) can interact with monumental trees resources through a uniform HTTP REST interface.  

## Data

### Initial dataset

At the end of 2017 the Mipaaf published on [its webiste](https://www.politicheagricole.it/flex/cm/pages/ServeBLOB.php/L/IT/IDPagina/11260) a list of the Italian monumental trees.  
The list is divided by region, but a user can ask for a complete dataset, that consists of an Excel spreadsheet, various Shapefile, and a pdf with various additional metadata.  
The original spreadsheet's data contains tree data, with each row representing a single tree along with its geographical position, its status, and various _criteria_ used to quantify its eligibility to be monumental.  
Data is provided under [CC BY 4.0 license](https://creativecommons.org/licenses/by/4.0/deed.it), so I was allowed to modify and use the original dataset.  
To store the data for the API I decided to use a relational database (even if I have a single entity), so I started to work around the original spreadsheet to obtain an importable CSV.  

The first issue that I encountered while analyzing the dataset was the language: data are entirely expressed in Italian, and while that's fine to leave the values in their original idiom I was undecided on how to threaten the attributes and the metadata.  
I prefer to use the English language when I work with information,  data, and their representation, so I decided to translate the attribute's name.  
Here follows a table that reports, for each attribute, the original Italian name, its English translation, and a meaningful description.

@TODO Attributes table

The second issue was with data format: all numeric values were expressed with a comma character for the decimal separator (for example 42,08723), whilst I need a point to correctly express and persists them as double-precision 64-bit IEEE 754 floating-point number.  
So `latitude`, `longitude`, `altitude`, `height`, `circumference`, `average_group_height`, `max_group_height`, `average_group_circumference`, `max_group_circumference` were all converted.  

Then it was the time for criteria, all using the Italian words "VERO" and "FALSO" to mean boolean values.  
I went straight and replaced all occurrences with "true" or "false" instead.  
Finally, I encountered some encoding issues:
Excel wasn't able to correctly export scientific_name in UTF-8 encoded characters, so I was forced to import the spreadsheet in Google Sheet before the final export in CSV.  

### Database

From the CSV I generated two SQL files to be used to generate the database schema and to populate the table with data.  
The database of choice is [PostgreSQL](https://www.postgresql.org/), due to its robustness and adherence to SQL standards.  
The schema is quite simple and is composed by:
    
- A single database instance: `monumental_trees_database`
- A single user with permission on that database: `monumental_trees_user`
- A single table where each row contains a single monumental tree data

Here there's a conceptual schema (not a standard E/R one) of the database:

![monumental_trees_logic_schema (1)](https://user-images.githubusercontent.com/1985555/99822293-51d31500-2b53-11eb-984a-b78136182581.png)

To handle the database the application uses [Flyway](https://flywaydb.org/), a Java library that enables schema and data migrations applying a version to each operation done aganinst the database. 
This allows not only to recreate and populate the database on demand, every time the application runs but even to apply centralized and deterministic changes depending on which migrations were applied and which not.  
For the local development, I opted for a containerized PostgreSQL instance, defined and handled within a [docker-compose](https://docs.docker.com/compose/), while for the production environment I used the same database version provided by [Cloud SQL](https://cloud.google.com/sql)  

## Architecture

### API

Monumental trees is a REST API.  
It allows a consumer to request and manipulate monumental trees resources via a uniform HTTP interface.  
At its heart Monumental trees is a stateless web service that computes HTTP requests against the `/api/v1/monumental-trees` URI.  
The desired resource manipulation can be achieved by specifying one of the four HTTP methods supported;  
While we know that the data is stored in a PostgreSQL database instance, its representation is conceptually independent of the representation returned to the client, that is in JSON. 

#### GET

```
GET /api/v1/monumental-trees
```

Returns a paginated list of monumental trees.  
A client can browse through pages specifying one of the following query parameters:  

- `page` to specify the desired number of page
- `size` to specify the desired size of a page
- `sort` to specify the desired sorting related to one or more attributes of a monumental tree.

```
GET /api/v1/monumental-trees/{id}
```
    
Return a single monumental tree by its unique identifier, specified in the URI.  
The application responds with the 400 HTTP status code if the monumental tree cannot be found.  

#### POST

```
POST /api/v1/monumental-trees
```
    
A client can create a new monumental tree with an HTTP POST request and a valid JSON payload enclosed in the request body.    
It returns a 201 HTTP status code with the newly created resource if the operation is successful, or 400 if one of the provided values of the monumental tree is invalid. For the validity of a monumental tree object consult the table provided in the data section.  

#### PUT

```
PUT /api/v1/monumental-trees/{id}
```

A client can update one of the already existing monumental trees by specifying its unique identifier in the URI and making an HTTP PUT request with a valid JSON payload enclosed in the request body.  
The applications respond with a 200 HTTP status code and the updated resource in the response body if the operation is successful;  
If the resource cannot be updated, because it doesn't exist, the applications return a 404 HTTP status code.  
If the payload is not valid the application returns a 400 HTTP status code.    

#### DELETE

```
DELETE /api/v1/monumental-trees/{id}
```

A client can delete one of the existing monumental trees by specifying its unique identifier in the URI.  
The applications respond with 200 if the operation is successful, or 404 if the resource that the client specified doesn't exist.  

As you can see the API is versioned, enabling support for different resource representation in the future.  
Monumental Trees API supports [CORS](https://developer.mozilla.org/it/docs/Web/HTTP/CORS), so all the exposed resources can be requested by a different domain enabling the related HTTP headers.

### Authentication

Every access to the API needs to be authenticated.
Monumental trees API supports [OAuth 2.0](https://oauth.net/2/) authentication via signed JSON Web Token ([JWT](https://jwt.io/introduction/)) that are exchanged by the client in the `Authorization` header of each HTTP request.
These are the involved roles in the authentication process:

- **Resource Owner**: Entity that can grant access to a protected resource. Typically, this is the end-user.
- **Resource Server**: Server hosting the protected resources, in this case, Monumental trees API.
- **Client**: The application requesting access to a protected resource on behalf of the Resource Owner.
- **Authorization Server**: Server that authenticates the Resource Owner and issues access tokens after getting proper authorization. 

Implementing a valid and secure OAuth 2.0 Authorization Server can be daunting, so I opted to use [Auth0](https://auth0.com/) as the main issuer for the token and its validity.  
The authentication and authorization flow is the following:

1. A client asks for a token to the issuer, specifyng the secretly shared client_id and client_secret of the Monumental Trees API:

    ```
    curl --request POST \
    --url https://dev-2uotfikm.eu.auth0.com/oauth/token \
    --header 'content-type: application/json' \
    --data '{"client_id":"${client_id}","client_secret":"${client_secret}","audience":"https://monumental-trees/","grant_type":"client_credentials"}'
    ```

2. The issuer respond with a JSON payload containing a JWT access token:

    ```
    {
        "access_token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJIZWxsbyBXb3JsZCIsIm5hbWUiOiJHYWJyaWVsZSBEJ0FycmlnbyIsImlhdCI6MTUxNjIzOTAyMn0.KERayD5LaXWzTtT-LA2R6bhU18bphvDV8Q5va7HeUrpNSjuGLahWOq4fLBYXby6fOH-1qaO8w668WEXqetpR_w",
        "token_type": "Bearer"
    }
    ```

3. The client can now add the access token, signed with the RS256 algorithm, in the Authorization header of a HTTP request to the server:

    ```
    curl --request GET \
    --url http://34.77.217.232/api/v1/monumental-trees \
    --header 'authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJIZWxsbyBXb3JsZCIsIm5hbWUiOiJHYWJyaWVsZSBEJ0FycmlnbyIsImlhdCI6MTUxNjIzOTAyMn0.KERayD5LaXWzTtT-LA2R6bhU18bphvDV8Q5va7HeUrpNSjuGLahWOq4fLBYXby6fOH-1qaO8w668WEXqetpR_w'
    ```

4. When the server receives a request it checks for the presence of an access token. If it is the server tries to validate the JWT signature with the public key of the Authorization Server. If the JWT is valid, the client is authenticated and authorized to access the requested resources.  

More information on OAuth 2.0 protocol and the authentication and authorization flow can be found in the [Auth0 documentation](https://auth0.com/docs/protocols/protocol-oauth2)  

### Infrastructure and deployment

#### Concepts

Monumental trees API is a stateless web service, and [share nothing](https://en.wikipedia.org/wiki/Shared-nothing_architecture) with other processes or applications.
Data is persisted in a stateful [backing-service](https://12factor.net/backing-services), the PostgreSQL instance;  
This means that the application is a perfect  candidate to be a [containerized application](https://www.docker.com/resources/what-container):
namely a lightweight, standalone, executable package of software that includes everything needed to run the application, that is its code, the required runtime, the application's libraries, and settings.  

A container is ephemeral: it can be built, destroyed, and recreated with the minimum effort and with the same result every time.
Saying this, Monumental Trees API is a cloud-native application; it runs in a Docker container in a [Kubernetes](https://kubernetes.io/) cluster provisioned on [Google Cloud](https://cloud.google.com/kubernetes-engine).  
Kubernetes is an open-source cluster management system that provides the mechanism to orchestrate the life cycle of a containerized application.

Kubernetes is based on these four main concepts:

- **Cluster**: is the server that runs Kubernetes and where all containerized applications run.
- **Node**: a worker machine that runs the containerized applications and other workloads. Typically a cluster has one or more nodes.
- **Pod**: is the smallest, basic deployable object in Kubernetes. A Pod represents a single instance of a running process in the cluster.
- **Deployment**: it represents a set of multiple, identical Pods with no unique identities. It describes how many pods should run and how a pod should like: the application to run in its container, the amount of CPU it needs, etc etc.

#### Deployment

The application's deployment follows the continuous deployment principle: every time a developer merges the code on the master branch of the git repository (that holds all application's source code) a workflow is executed.  
A workflow is an automated process that:

1. Runs all application's unit tests
2. Build the source code into an executable JAR
3. Create a new containerized version of the application
4. Deploy the application  to the Kubernetes cluster

Every time a deployment is _applied_, Kubernetes start a fresh pod with the new version of the application; when the pod is ready the network traffic coming to the cluster is redirected from the pod with the _old_ version of the application to the new one.
Once all the traffic is forwarded the old pod is destroyed; this entire process is managed by Kubernetes itself.

![infrastructure](https://user-images.githubusercontent.com/1985555/99822653-c7d77c00-2b53-11eb-98b1-76327db7902b.png)

## Resources

@TODO A list of resources

## License

Fonte dei dati: Direzione generale dell'economia montana e delle foreste del Mipaaf - "dataset AMI - Censimento alberi monumentali d'Italia":

[https://www.politicheagricole.it/flex/cm/pages/ServeBLOB.php/L/IT/IDPagina/11260](https://www.politicheagricole.it/flex/cm/pages/ServeBLOB.php/L/IT/IDPagina/11260)

# Italian monumental trees

A REST API for italian monumental trees, developed for the Service Oriented Architecture course of ["Sicurezza dei Sistemi e Delle Reti Informatiche"](http://sicurezzaonline.di.unimi.it/) bachelor's degree program.  

![CI](https://github.com/gabrieledarrigo/monumental-trees/workflows/CI/badge.svg)

## Foreword

Monumental trees is a project for the Service Oriented Architecture course of ["Sicurezza dei Sistemi e Delle Reti Informatiche"](http://sicurezzaonline.di.unimi.it/) bachelor's degree program. 

At its heart is a Kotlin REST API application developed with Spring Boot.  
It exposes a single resource, italian monumental trees, that is trees that are eligible, by the italian "Ministero delle politiche agricole alimentari e forestali" (from now, Mipaaf), to be protected because of their naturalistic, historic, cultural or landscaped values.  

An authenticated client (more on this in the related section) can interact with monumemtal trees resources through a uniform REST interface.  

## Data

### Initial dataset

At the end of 2017 Mipaaf published on [its webiste](https://www.politicheagricole.it/flex/cm/pages/ServeBLOB.php/L/IT/IDPagina/11260) a list of the italian monumental trees.  
The list is divided by region, but a user can ask for a complete dataset, that consists in an Excel spreadsheet, various Shapefile and a pdf with various additional metadata.  
The original spreadsheet's data contains trees data, with each row representing a single tree along with its geographical position, its status, and various criteria used to quantify its eligibility to be monumental.  
Data is provided under CC BY 4.0 license, so I was allowed to modify and use the original dataset.  
To store the data for the API I decided to use a relational database (even if I have a single entity) so I started to work around the original spreadsheet to obtain an importable CSV.  

The first issue that I encountered while analyzing the dataset was the language; data are entirely expressed in italian, and while that's totally fine to leave the values in their original idiom I was undecided on how to threat the attributes and the metadata.  
I personal prefer to use the english language when I work with information,  data, and their representation, so I decided to translate the attribute's name; here follows a table with the original attribute name, its english translation and a meaningul description.  

The second issue was with data format: all numeric values where expressed with a comma character for the decimal separator (for example: 42,08723), whilst I need a point to correctly express and persists them as double-precision 64-bit IEEE 754 floating point number.  
So latitude, longitude, altitude, height, circumference, average_group_height, max_group_height, average_group_circumference, max_group_circumference where all converted.  

Then it was the time for criterias, all using the italian words "VERO" and "FALSO" to mean boolean values. I went straight and replaced all occurrences with "true" or "false" instead.  
Finally I encoutered some encoding issues;  
Excel wansn't able to correctly exports scientific_name in UTF-8 encoded characters, so I was forced to import the spreadsheet in Google Sheet before the final export in CSV.  

### Database

From the CSV I generated two SQL file to be used to generate the database schema and to populate the table with data. 
The database of choice is PostgreSQL, due to its robustness and adherence to SQL standards.  
The schema is quite simple, and is composed by:
    
- A single database instance: monumental_trees_database
- A single user with permission on that database: monumental_trees_user
- A single table where each row contains a single monumental tree data

Here there's a conceptual schema (not a standard E/R one) of the database:

![monumental_trees_logic_schema](https://user-images.githubusercontent.com/1985555/99802340-d879f900-2b37-11eb-99c3-7112384f2b0f.png)

The application uses Flyway, a Java library that enable schema and data migrations applyng a version to each operation done to the database. 
This enables not only to recreate and populate the database on demand, every time the application's runs, but even to apply centralized and deterministic changes depening on which migrations were applyed and which not.  
For the local development I opted for a containerized PostgreSQL instace, defined and handled within a docker-compose, while for the production enviroment use the same database version provided by [Cloud SQL](https://cloud.google.com/sql)  

## Architecture

### API

Monumental trees is a REST API.  
It allows a consumer to request and manipulate monumental trees resources via a uniform HTTP interface.  
At its heart Monumental trees is a stateless web service that computes HTTP request against the /api/v1/monumental-trees URI.  
The desired resource manipulation can be achieved specifing one of the four HTTP methods supported;  
While we know that the data is actually stored in PostgreSQL database instance, its representation is conceptually independent from the represantion returned to the client, that is in JOSN. 

#### GET

    GET /api/v1/monumental-trees

Returns a paginated list of monumental trees.  
A client can browse through pages specifing one of the followin query parameters:  

- page to specify the desired number of page
- size to specify the desired size of a page
- sort to specify the desired sorting related to one or more attributes of a monumental tree

    GET /api/v1/monumental-trees/{id}

Return a single monumental trees by its unique identifier, specified in the URI.  
The application responds with the 400 HTTP status code if the monumental tree cannot be found.  

#### POST

    POST /api/v1/monumental-trees

A client can create a new monumental tree with a HTTP POST request and a valid json payload enclosed in the request body.    
It returns a 201 HTTP status code with the new created resource if the operations is successfull, or 400 if one of the provided values of the monumental tree is invalid. For the validity of a monumental tree object consult the table provided in the data section.  

#### PUT

    PUT /api/v1/monumental-trees/{id}

A client can update one of the already existing monumental tree specifyng its unique identifier in the URI, and making a HTTP PUT request with a valid json payload enclosed in the request body.  
The applications responds with a 200 HTTP status code and the updated resource in the response body if the operation is successfull;  
If the resource cannot be updated, because it doesn't exists, the applications returns a 404 HTTP status code.  
If the payload is not valid the application returns a 400 HTTP status code.    

#### DELETE

    DELETE /api/v1/monumental-trees/{id}

A client can delete one of the existing monumental tree specifyng its unique identifier in the URI.  
The applications responds with 200 if the operation is successfull, or 404 if the resource that the client specified doesn't exists.  

As you can see the API is versioned, enabling support for different resource represantion in the future.  

### Authentication



### Infrastructure

## License

Fonte dei dati: Direzione generale dell'economia montana e delle foreste del Mipaaf - "dataset AMI - Censimento alberi monumentali d'Italia":

[https://www.politicheagricole.it/flex/cm/pages/ServeBLOB.php/L/IT/IDPagina/11260](https://www.politicheagricole.it/flex/cm/pages/ServeBLOB.php/L/IT/IDPagina/11260)


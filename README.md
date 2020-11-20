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
The store the data for the API I decided to use a relational database (even if I have a single entity) so I started to work around the original spreadsheet to obtain an importable CSV.
The first issue that I encountered while analyzing the dataset was the language; data are entirely expressed in italian, and while that's totally fine to leave the values in their original idiom I was undecided on how to threat the attributes and the metadata.
I personal prefer to use the english language when I work with information,  data, and their representation, so I decided to translate the attribute's name; here follows a table with the original attribute name, its english translation and a meaningul description.  

The second issue was with data format: all numeric values where expressed with a comma character for the decimal separator (for example: 42,08723), whilst I need a point to correctly express and persists them as double-precision 64-bit IEEE 754 floating point number.
So latitude, longitude, altitude, height, circumference, average_group_height, max_group_height, average_group_circumference, max_group_circumference where all converted.
Then it was the time for criterias, all using the italian words "VERO" and "FALSO" to mean boolean values. I went straight and replaced all occurrences with "true" or "false" instead.
Finally I encoutered some encoding issues; Excel wansn't able to correctly exports scientific_name in UTF-8 encoded characters, so I was forced to import the spreadsheet in Google Sheet before the final export in CSV.  

### Database

From the CSV I generated two SQL file to be used to generate the database schema and to populate the table with data. 
The database of choice is PostgreSQL, due to its robustness and adherence to SQL standards.  
The schema is quite simple, and is composed by:
    
    - A single database instance: monumental_trees_database
    - A single user with permission on that database: monumental_trees_user
    - A single table where each row contains a single monumental tree data

![monuemental_trees_er](https://user-images.githubusercontent.com/1985555/99799261-a9ad5400-2b32-11eb-829f-7a94df650f54.png)

The application uses Flyway, a Java library that enable schema and data migrations applyng a version to each operation done to the database. 
This enables not only to recreate and populate the database on demand, every time the application's runs, but even to apply centralized and deterministic changes depening on which migrations were applyed and which not.  

## Architecture

### API

### Authentication

### Infrastructure

## License

Fonte dei dati: Direzione generale dell'economia montana e delle foreste del Mipaaf - "dataset AMI - Censimento alberi monumentali d'Italia":

[https://www.politicheagricole.it/flex/cm/pages/ServeBLOB.php/L/IT/IDPagina/11260](https://www.politicheagricole.it/flex/cm/pages/ServeBLOB.php/L/IT/IDPagina/11260)


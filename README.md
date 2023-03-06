<img alt="CircleCI" src="https://img.shields.io/circleci/build/github/Karolinaslp/Bookido/master">


# Bookido
Bookido is a REST web service built using Spring Boot, Hibernate, Maven, Lombok, PostgreSQL, JUnit5, and CircleCI. This web service provides endpoints for registering users, managing book orders, updating book information, and uploading book covers.

## Table of Contents
* [General Info](#general-information)
* [Technologies Used](#technologies-used)
* [Features](#features)
* [API Reference](#api-reference)
* [Setup](#setup)
* [Project Status](#project-status)
* [Room for Improvement](#room-for-improvement)
* [Acknowledgements](#acknowledgements)




## General Information
- Bookstore online REST application. 
- Backend service for managing administration of the store.
- Created for learning purposes to extend my knowledge of the Spring framework.

## Technologies Used
- Java 17
- Spring Boot 2.7.3
- Maven 4.0.0
- Hibernate 6.2.0Final
- PostgresSQL 42.2.19.jre17
- Lombok 1.18.24
- H2 database 2.1.214
- JUnit5 5.8.2
- CircleCi integration
- Docker v2.13.0'


## Features
- orders/book/client management
- update of order changes
- detection of abandoned orders
- discount policy
- cost of delivery depending on the amount of purchases
- separation of access to endpoints for the administrator, logged user or available to everyone
- adding book covers
- search for books by title or author/authors



## API Reference

#### Register User

```http
  POST /users
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `username` | `string` | **Required**. Name of the user to be registered. |
| `password` | `string` | **Required**. Min 3, max 100 characters. |

Takes username and password and rcreates new user.

#### Get Order By Id

```http
  GET /orders/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `long` | **Required**. Id of upload. |
| `user login`      | `string ` | **Required**. Login of admin or user. |
| `user password`      | `string ` | **Required**. Password of admin or user. |

Takes id, login and password of an order and returns order details.

#### Create Order

```http
  POST /orders/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `items`      | `list` | **Required**. List of items to be ordered. |
| `user login`      | `string ` | **Required**. Login of admin or user. |
| `user password`      | `string ` | **Required**. Password of admin or user. |

Takes id, login and password of an order, along with the items to be ordered, and creates a new order with its details.

#### Update Order Status

```http
  PATCH /orders/${id}/status
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `long` | **Required**. Id of order |
| `current status`      | `string` | **Required**. Current order status. |
| `changed status`      | `string` | **Required**. Changed order status. |
| `user login`      | `string ` | **Required**. Login of admin or user. |
| `user password`      | `string ` | **Required**. Password of admin or user. |

Takes id, current and expected status, login and password of an order, and changes order status.

#### Get Upload

```http
  GET /uploads/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `long` | **Required**. Id of upload. |

Takes id of upload and returns file id, content type, name amd date of creation.

#### Get Uploaded File

```http
  GET /uploads/${id}/file
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `long` | **Required**. Id of upload. |

Takes id of upload and returns uploaded file

#### Get All Books

```http
  GET /catalog
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `title`      | `string ` | **Optional**. Title of the book.|
| `author`      | `string ` | **Optional**. Author of the book. |

Retrieves a list of all books in the catalog.

#### Get Book By Id

```http
  GET /catalog/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `long` | **Required**. Id of the book to retrive.|

Takes book id and returns book details.

#### Update Book

```http
  PATCH /catalog/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `long` | **Required**. Id of the book to update.|

Takes book id, body with updated book details and updates book details.

#### Add Book Cover

```http
  PUT /catalog/${id}/cover
```

| Parameter | Type                  | Description                       |
| :-------- |:----------------------| :-------------------------------- |
| `id`      | `long`                | **Required**. Id of the book to update.|
| `file`      | `multipart/form data` | **Required**. Cover file.|

Takes book id, cover file and adds a cover image to a book in the catalog.

#### Remove Book Cover

```http
  PATCH /catalog/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `long` | **Required**. Id of the book to be updated.|

Takes book id and removes the cover image from a book in the catalog.

#### Add Book

```http
  POST /catalog
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `title`      | `string ` | **Required**. Title of the book.|
| `list of authors`      | `string` | **Required**. Authors of the book. |

Takes book id title ,authors and creates new book.

#### Delete Book

```http
  DELETE /catalog/{id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `long` | **Required**. Id of the book to be deleted.|

Takes book id and deletes a book from the catalog.

#### Find All Authors

```http
  GET /authors
```
Takes nothing and returns list of authors.

#### Find All Authors

```http
  POST /initialization
```
Initialize book catalog. Required admin login.

## Setup
Application integrated with Swagger.
after build available at: http://localhost:8080/swagger-ui.html

Admin login: "admin".

Admin password: "xxx".

## Project Status
Project is: _in progress_ 


## Room for Improvement

To do:
- Add Flyway migration 
- Deploy app on Heroku


## Acknowledgements
Give credit here.
- This project was based on "School of Spring" course.


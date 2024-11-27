# MTCG App

## Overview
The MTCG (Multi-Transaction Card Game) app is designed to handle HTTP requests, process them through various layers, and interact with a PostgreSQL database. This document outlines the design decisions, structure, and class interactions within the application.

## Design Decisions

- **Request Handling**: Incoming requests are first received by the server and routed to a `RequestHandler` function. This function is temporary and will be expanded into a separate file for better organization as the application grows.

- **Layered Architecture**: The application follows a layered architecture to separate concerns and improve maintainability:
  - **Controller Layer**: Handles HTTP request methods and determines the appropriate HTTP status codes to return.
  - **Service Layer**: Contains the business logic and acts as an intermediary between the controller and repository layers.
  - **Repository Layer**: Manages database interactions, encapsulating all CRUD operations.

- **Database Interaction**: Currently, the PostgreSQL container class resides in the controller but will be moved to the utils package for better separation of concerns.
- Once the application has build a postres container it will reuse it.
- For Re-testing of registration use
```
- docker stop my_postgres_container && docker rm my_postgres_container

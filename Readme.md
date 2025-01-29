# Monster Trading Card Game (MTCG) - Project Documentation

## Overview

The Monster Trading Card Game (MTCG) application is a robust, REST-based server designed to facilitate a multi-player, digital trading card game experience. Implemented in Java, the application allows users to engage in various game-related activities such as user registration, card management, deck building, trading, and battling. The system is built upon a well-defined layered architecture and utilizes a PostgreSQL database, managed through Ansible, to ensure data persistence and integrity. Security is a key aspect of the design, with JWT (JSON Web Tokens) employed for secure user authentication and authorization.

## Design and Architecture

The application adheres to a layered architectural pattern, which promotes a clear separation of concerns and enhances the maintainability and scalability of the system. The architecture is composed of the following distinct layers:

**1. Controller Layer:** This layer acts as the entry point for all HTTP requests. It is responsible for handling request routing, data validation, and generating appropriate HTTP responses, including status codes. Each controller class is dedicated to a specific domain entity, such as users, cards, decks, trades, or packages.

**2. Service Layer:** The service layer encapsulates the core business logic of the MTCG application. It orchestrates interactions between the controller and repository layers, implementing the rules and processes related to user management, card manipulation, deck construction, trading mechanics, and battle execution.

**3. Repository Layer:** This layer is dedicated to database interactions. Each repository class manages the persistence of a specific domain entity, abstracting all CRUD (Create, Read, Update, Delete) operations from the service layer. This ensures that the service layer remains independent of the underlying data storage mechanism.

**4. HTTP Utilities Layer:** This layer provides utility classes for constructing HTTP responses in a standardized manner. It leverages a builder pattern to create responses with appropriate status codes, content types, and response bodies.

**Class Structure:** The code is organized into modules, each focusing on a specific aspect of the game. The primary modules are:

-   **User:**  Classes for handling user-related functions, including user creation, authentication, and profile management.
    -   `UserController`, `UserService`, `UserRepository`
-   **Card:** Classes dedicated to individual cards, including their creation and properties.
    -   `CardController`, `CardService`, `CardRepository`
-   **Deck:** Classes responsible for managing the collections of cards that users use in battles.
    -   `DeckController`, `DeckService`, `DeckRepository`
-   **Package:** Classes that handle the creation and distribution of card packages.
    -   `PackageController`, `PackageService`, `PackageRepository`
-   **Trade:** Classes for implementing the card trading system.
    -   `TradeController`, `TradeService`, `TradeRepository`
-   **Battle:** Classes dedicated to the battle logic and outcomes.
    -   `Battle`, `BattleLog`, `BattleManager`

**HTTP Response Builder:** The application utilizes a specialized `ResponseBuilder` class to construct HTTP responses. This builder pattern approach ensures consistency and simplifies the process of generating responses with various status codes, content types, and message bodies. The `Constants` class provides predefined values for common HTTP status codes and content types, while the `ResponseService` class offers convenience methods for creating common response types, such as success, error, and token responses.

## Database Management

The application's data persistence is managed through a PostgreSQL database. The setup and maintenance of the database are automated using Ansible playbooks. Ansible ensures consistent database configurations across different environments and simplifies tasks such as database creation, schema setup, and container management.

**Ansible Playbooks Usage:**

-   **Full Setup/Start Container:**
    ```bash
    ansible-playbook db.yml
    ```

-   **Reset Database (Stop and Remove Container + Volume):**
    ```bash
    ansible-playbook db.yml --tags "utils,reset"
    ```

-   **Full Purge (Remove Container, Volume, and Image):**
    ```bash
    ansible-playbook db.yml --tags "utils,reset"
    ```

-   **Setup Database Tables:**
    ```bash
    ansible-playbook db.yml --tags utils
    ```

-   **Manual Reset (Docker):**
    ```bash
    docker stop my_postgres_container && docker rm my_postgres_container
    ```

## Authentication

Security is paramount in the MTCG application, and user authentication is implemented using JSON Web Tokens (JWT). Upon successful user login, the server generates a JWT that contains user-specific information. This token is then included in the `Authorization` header of subsequent requests made by the client. The server validates the token's authenticity and expiration before granting access to protected resources. This ensures that only authorized users can perform actions within the system.

## Testing

The MTCG application is thoroughly tested using a combination of unit and integration tests. Unit tests, written using JUnit and Mockito, verify the correctness of individual components, such as services and repositories. Integration tests, primarily executed using Curl scripts, validate the end-to-end functionality of the API endpoints and the overall system behavior, ensuring that all components work together as expected.


# Checkout Application

## Overview
The Checkout Application is a RESTful API for managing a checkout system. It provides functionality to:
- Add items to a cart
- Calculate the total price, applying discounts and bulk pricing
- Process payments and generate receipts
- View and reset cart contents

This project leverages and follows a modular structure for scalability and maintainability.

---

## Features
### REST API Endpoints:
- **POST** `/api/checkout/scan`: Add items to the cart.
- **GET** `/api/checkout/total`: Calculate the total price.
- **POST** `/api/checkout/pay`: Process payment and generate a receipt.
- **GET** `/api/checkout/cart`: View cart contents.
- **PUT** `/api/checkout/reset`: Reset the cart.

### Discounts and Bulk Pricing:
- Supports bulk discounts (e.g., buy 3 items for a reduced price).
- Additional discounts between paired items.

### Security:
- Flexible configurations for public and restricted endpoints using Spring Security.
- CSRF and CORS disabled for simplicity during development.

### Validation:
- Ensures valid data is submitted through endpoints using `jakarta.validation`.

---

## Prerequisites
Ensure the following are installed:
- Java 17
- Maven 3.8+

---

## Build and Run
### Steps to Build and Execute
1. **Clone the Repository:**
   ```bash
   git clone https://github.com/Vikiyuk/CheckoutComponent3.0.git
   cd CheckoutComponent3.0
2. **Build the Project:**
   ```bash
   mvn clean install
   ```
3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```
3.1. ***Alternatively you can use jar file:***
```bash
java -jar checkout-0.0.1-SNAPSHOT.jar
```
4. **Test the Endpoints:**  
Use tools like Postman, `curl`, or Swagger to test the API.

### Example with `curl`:
```bash
curl -X POST -H "Content-Type: application/json" -d '{"itemId":"A", "quantity":3}' http://localhost:8080/api/checkout/scan
curl -X GET http://localhost:8080/api/checkout/total
```
### API Documentation

#### Endpoints Overview

| Endpoint                        | Method | Description                    |
|----------------------------------|--------|--------------------------------|
| `/api/checkout/scan`            | POST   | Add items to the cart          |
| `/api/checkout/total`           | GET    | Calculate the total cost       |
| `/api/checkout/pay`             | POST   | Process payment & get receipt  |
| `/api/checkout/cart`            | GET    | View cart contents             |
| `/api/checkout/reset`           | PUT    | Reset the cart                 |

#### Endpoint Details

1. **POST /api/checkout/scan**
   - **Description**: Adds an item to the cart. If the item already exists, its quantity is incremented by the specified amount.
   - **Request Body**: 
     ```json
     {
       "itemId": "A",
       "quantity": 3
     }
     ```
   - **Response**: 
     - Status: `200 OK` if the item is successfully added to the cart.

2. **GET /api/checkout/total**
   - **Description**: Calculates the total price of items in the cart, including bulk pricing and discounts.
   - **Response**: 
     - Status: `200 OK` with the total price of items in the cart.

3. **POST /api/checkout/pay**
   - **Description**: Processes payment and generates a receipt for the cart. It includes details of the purchased items, applied discounts, and the total price.
   - **Response**: 
     - Status: `200 OK` with the receipt details.

4. **GET /api/checkout/cart**
   - **Description**: Retrieves the current contents of the cart.
   - **Response**: 
     - Status: `200 OK` with the cart contents (list of items and quantities).

5. **PUT /api/checkout/reset**
   - **Description**: Resets the cart, clearing all items.
   - **Response**: 
     - Status: `200 OK` if the cart is successfully reset.
## Tests
Implemented unit, integration and acceptance tests, to execute them use the following command:
```bash
mvn test
```


### Design Choices and Rationale

- **Session Management:**
  - **Why?** The cart is stored in the session using `@SessionAttributes`. This ensures that the cart is isolated to the user's session, enabling them to add items and maintain their cart between requests. It also helps avoid data loss when the user interacts with multiple endpoints in a single session.

- **Validation:**
  - **Why?** `@Valid` and `@Validated` annotations ensure input validation for incoming requests. This enforces data integrity, making sure that any data coming into the application meets the required criteria. For example, the `ScanRequest` checks if item IDs and quantities are valid before proceeding with cart operations.

- **Modular Structure:**
  - **Why?** The project is organized with clear separation of concerns: Controller, Service, and Model. This modular structure improves code maintainability, testing, and scalability. 

- **Exception Handling:**
  - **Why?** A global exception handler (`GlobalExceptionHandler`) is implemented to handle common errors and provide human-friendly messages. 

- **Security:**
  - **Why?** Security configurations using Spring Security are employed to manage access to specific endpoints. Security is set to be permissive during development (with CSRF and CORS disabled).

- **Bulk and Discount Pricing:**
  - **Why?** Bulk pricing and item-based discounts are handled in the business logic. Bulk discounts apply when a certain quantity is reached for an item, while item-based discounts (e.g., "Buy X get Y") are handled separately to apply to specific items in the cart. This separation ensures that both pricing schemes are independently calculated.

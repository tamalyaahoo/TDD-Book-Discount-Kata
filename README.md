Book Discount Price Calculator – Spring Boot 4 + Java 25
==============================================================
A TDD-friendly implementation of the Book Discount Application :

Spring Boot 4.0.0 (Spring Framework 7 / Jakarta EE 11)

Java 25 (LTS)

REST API with MockMvc Tests

OpenAPI 3 / Swagger UI

JUnit 5

Udacity Commit Style Guidelines

****************************************************
Problem Statement (Kata Summary)
====================================================
A bookstore offers discounts depending on how many different titles from a 5-book series are purchased together:

Books ----	Discount

1	      ---        0%

2	      ---        5%

3	      ---        10%

4	      ---        20%

5	      ---        25%

Each book costs EUR 50 before discounts.

The challenge:
Given a basket of books with quantities, find the cheapest possible total price, considering optimal grouping of books.

Example (from Kata spec):
2       Clean Code
2       Clean Coder
2       Clean Architecture
1       TDD
1       Legacy Code

Solution:

(4 books × 20% discount) + (4 books × 20% discount)
= 160 + 160
= EUR 320
**************************************************************
Features
=========
Fully REST-based Price Calculation

Endpoint: 
POST /api/price/calculate


Example request:
{
  "items": {
    "Clean Code": 2,
    "The Clean Coder": 1
  }
}

Response:
{
  "totalPrice": 95.0
}
*********************************************
API Documentation (Swagger UI)
=============================================
Once the app is running:

Swagger UI : 
http://localhost:8080/swagger-ui.html

OpenAPI Spec :
http://localhost:8080/v3/api-docs

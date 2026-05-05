# Nanba – Social Media Platform
![Nanba](assets/nanba_logo.png)

Nanba is a full‑stack social media web application inspired by Twitter/X, where users can share posts, follow others, like and comment on content, and manage their profile.  
It features a secure backend backed by Spring Security and JWT authentication and a responsive frontend (vanilla JavaScript, CSS).

## Live Demo
- https://nanba.up.railway.app/

---

## Table of Contents
- [Features](#features)
   - [Authentication & Session Management](#authentication--session-management)
   - [Posts](#posts)
   - [Comments](#comments)
   - [Profile & User Management](#profile--user-management)
   - [UI / UX Highlights](#ui--ux-highlights)
   - [Security](#security)
- [Tech Stack](#tech-stack)
   - [Frontend](#frontend)
   - [Backend](#backend)
- [How to Run the Project](#how-to-run-the-project)
   - [Prerequisites](#prerequisites)
   - [Backend Setup](#backend-setup)
   - [Docker Setup (Optional)](#docker-setup-optional)
- [API Overview](#api-overview)
   - [Authentication](#authentication-api)
   - [Users](#users-api)
   - [Posts](#posts-api)
   - [Comments](#comments-api)

---

## Features

### Authentication & Session Management
- User registration with email and password
- User login with JWT access/refresh tokens, stored in `HttpOnly`, `Secure`, `SameSite=Strict` cookies
- Silent token refresh when the access token expires
- Logout clears both token cookies

### Posts
- **Global feed** – see all posts (public access)
- **Following feed** – see posts from users you follow (requires login)
- Create a post (max 255 characters) with character counter
- Edit or delete your own posts (edit only after opening the post)
- Like/unlike posts

### Comments
- View comments under each post with infinite scroll
- Add, edit, or delete your own comments
- Like/unlike comments

### Profile & User Management
- View any user’s profile: avatar, banner, bio, join date, stats (followers/following/mutuals)
- Edit your own profile: name, bio, banner colour, avatar (choose from 5 presets or a default avatar)
- Change password
- Delete your account permanently (requires password confirmation)
- Follow/unfollow other users
- View followers, following, and mutual followers in a modal
- Compact sticky sidebar appears when scrolling past the profile header

### UI / UX Highlights
- Fully responsive layout (mobile/desktop)
- Toast notifications for all actions (success/error/info)
- Optimistic updates for likes and post creation
- Modals for viewing followers, following, and mutual followers

### Security
- Stateless JWT authentication (no server‑side session)
- Access token (short‑lived) and refresh token (long‑lived) stored in `HttpOnly`, `Secure`, `SameSite=Strict` cookies
- CSRF protection disabled intentionally:
  - stateless JWT + `SameSite=Strict` + `HttpOnly` makes it unnecessary
- BCrypt password hashing
- Input validation on both client and server side

## Tech Stack

### Frontend
- Vanilla JavaScript (ES modules)
- Thymeleaf as templating engine (only for server‑side routing and initial state)
- CSS3 (no framework – custom design)
- Font Awesome 6 for icons

### Backend
- Java 21 / Spring Boot
- Spring Security (JWT filter, stateless configuration)
- Spring Data JPA (Hibernate)
- PostgreSQL
- Maven (build tool)

---

## How to Run the Project

### Prerequisites
- JDK 21+
- Maven
- PostgreSQL

### Backend Setup
1. Clone the repository.
2. Make a copy of `env.example` and customize it.
   1. Configure the PostgreSQL database connection.
   2. Rename the file to remove the `.example` and save.
   3. For JWT_SECRET, a Base64 secret key of minimum 32 bytes is required which can be generated using `openssl rand -base64 32`, or at https://secretkeygenerator.com/jwt-secret-key-generator.
3. Run the Spring Boot application:
   ```batch
   ./mvnw spring-boot:run
   ```
4. The server will start on `http://localhost:8080`.

### Docker Setup (Optional)
1. Follow the first two steps listed in [Backend Setup](#backend-setup).
2. Enter the project directory.
3. Run `docker build -t spring-boot-app .` to build the Docker image.
4. Run `docker run -p 8080:8080 spring-boot-app` to run the container.

---

## API Overview

### Authentication API

* `POST /api/auth/register` – create a new user
* `POST /api/auth/login` – authenticate and set JWT cookies
* `POST /api/auth/refresh` – refresh access token
* `POST /api/auth/logout` – clear authentication cookies
* `GET /api/auth/me` – returns currently authenticated user's id/name/email

### Users API

* `GET /api/user/{id}` – retrieve a user profile (includes viewer-specific data when authenticated, e.g., follow state, mutuals)
* `GET /api/user/{id}/following/{page}` – get a paginated list of users the target user is following
* `GET /api/user/{id}/followers/{page}` – get a paginated list of users following the target user
* `GET /api/user/{id}/mutuals/{page}` – get mutual followers between authenticated user and target user *(requires authentication)*
* `POST /api/user/{id}/follow` – follow a user *(requires authentication)*
* `DELETE /api/user/{id}/unfollow` – unfollow a user *(requires authentication)*
* `PUT /api/user/profile` – update the authenticated user's profile information
* `PATCH /api/user/password` – update the authenticated user's password
* `DELETE /api/user/delete` – permanently delete the authenticated user's account (clears authentication cookies)

### Posts API

* `GET /api/post/{id}` – retrieve a single post (includes viewer-specific data when authenticated, e.g., like state)
* `GET /api/post/feed/global?page={page}` – retrieve the global feed (public, paginated)
* `GET /api/post/feed/following?page={page}` – retrieve a paginated list of posts from followed users *(requires authentication)*
* `GET /api/user/{id}/post/{page}` – retrieve a paginated list of posts created by a specific user
* `POST /api/post` – create a new post *(requires authentication)*
* `PATCH /api/post/{id}` – edit a post *(requires ownership)*
* `DELETE /api/post/{id}` – delete a post *(requires ownership)*
* `POST /api/post/{id}/like` – toggle like/unlike on a post *(requires authentication)*

### Comments API

* `GET /api/comment/{id}` – retrieve a single comment
* `GET /api/post/{id}/comments?page={page}` – retrieve comments for a post (paginated)
* `POST /api/post/{id}/comment` – create a comment on a post *(requires authentication)*
* `PATCH /api/comment/{id}` – edit a comment *(requires ownership)*
* `DELETE /api/comment/{id}` – delete a comment *(requires ownership)*
* `POST /api/comment/{id}/like` – toggle like/unlike on a comment *(requires authentication)*
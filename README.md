# Nanba – Social Media Platform

Nanba is a full‑stack social media web application inspired by Twitter/X where users can share posts, follow others, like and comment on content, and manage their profile.  
It features a secure backend backed by Spring Security and JWT authentication and a responsive frontend (vanilla JavaScript, CSS).

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
- Access token (short‑lived) and refresh token (long‑lived) stored in `HttpOnly` cookies
- CSRF protection disabled intentionally – stateless JWT + `SameSite=Strict` makes it unnecessary
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

## How to Run the Project

### Prerequisites
- JDK 21+
- Maven
- PostgreSQL

### Backend Setup
1. Clone the repository.
2. Make a copy of `env.example` and customize it.
   1. Configure the postgres database connection.
   2. Rename the file to remove the `.example` and save.
   3. For JWT_SECRET, a Base64 secret key of minimum 32 bytes is required which can be generated using `openssl rand -base64 32`, or at https://secretkeygenerator.com/jwt-secret-key-generator.
3. Run the Spring Boot application:
   ```batch
   ./mvnw spring-boot:run
   ```
4. The server will start on `http://localhost:8080`.

### Docker setup (optional)
1. Follow the first two steps listed in [Backend Setup](#backend-setup).
2. Enter the project directory.
3. Run `docker build -t spring-boot-app .` to build the Docker image.
4. Run `docker run -p 8080:8080 spring-boot-app` to run the container.
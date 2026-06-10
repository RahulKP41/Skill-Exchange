# Skill Exchange

Skill Exchange is a full-stack, Docker-first skill-sharing platform with:

- A static multi-page frontend built with HTML, Bootstrap, CSS, and vanilla JavaScript
- A Spring Boot API for auth, profiles, skills, matching, requests, sessions, feedback, wallet, admin, and analytics
- A Node.js Socket.IO gateway for realtime chat and message history
- A MySQL schema with seed data and Mermaid source diagrams

## Project Structure

```text
frontend/
backend/
  spring-api/
  node-gateway/
database/
docs/
docker-compose.yml
package.json
```

## Prerequisites

- Docker Desktop

## Run With Docker

This is the recommended way to start the full stack.

1. Open a terminal in the project root.
2. Go to the path of cd .\Skill_Exchange\ cd .\Skill-Exchange\
3. Run `npm run compose:up`.
4. If Docker Desktop was just installed, open a new terminal or restart VS Code first.
5. Wait for all services to build and start.
6. Open `http://localhost:8080/pages/index.html`.

Docker services:

- Frontend: `http://localhost:8080`
- Spring API: `http://localhost:8081/api`
- Node gateway: `http://localhost:3001`
- MySQL: `localhost:3307`

To stop Docker services:

- Run `npm run compose:down`

## Seed Accounts

- User: `aarav@skillx.local` / `password123`
- User: `maya@skillx.local` / `password123`
- Admin: `admin@skillx.local` / `password123`

## Useful Commands

- Start with Docker: `npm run compose:up`
- Stop Docker: `npm run compose:down`
- Run Node gateway tests: `npm run test:node`
- Run Spring tests: `npm run test:spring`

## Key Pages

- `/pages/index.html`
- `/pages/dashboard.html`
- `/pages/profile.html`
- `/pages/skills.html`
- `/pages/matches.html`
- `/pages/requests.html`
- `/pages/chat.html`
- `/pages/sessions.html`
- `/pages/wallet.html`
- `/pages/admin.html`

## Notes

- The Spring service is written against Java 21 and packaged with Docker.
- The Node gateway shares the same JWT secret as Spring for auth validation.
- Diagram source files live in `docs/` and can be exported to PNG when Mermaid tooling is available.
- The Docker path initializes MySQL from `database/schema.sql` and `database/seed.sql`.

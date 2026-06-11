# NutritionistHacksDaily

Personal website for nutritionist Hasan Abazi — a public landing page with real client transformations and certifications, plus a small hidden admin panel for managing the content.

It's a classic two-part setup:

- **frontend/** — Vue 3 (Vite), Pinia, Tailwind CSS v4
- **backend/** — Spring Boot 3 (Java 17), PostgreSQL 15, Flyway, JWT auth

## What it does

The public page is one scrolling site: a hero section with parallax and a 3D tilt card, certificates that open in a lightbox, a client results carousel, and a floating WhatsApp button — that's how clients actually get in touch, so there are no contact forms anywhere.

The admin panel is deliberately not linked from the public site. From there you can log in, upload before/after photos, and manage results and certificates. The JWT lives in memory only, so a page refresh logs you out — a fine tradeoff for a site with exactly one admin.

## Running it

Quickest way is Docker — everything in one go:

```
docker-compose up --build
```

The site comes up at http://localhost, the API at http://localhost:8080, Postgres on 5432.

For day-to-day development I keep only Postgres in Docker and run the apps natively:

```
docker run -d --name nutrition-db -p 5432:5432 -e POSTGRES_DB=nutritiondb -e POSTGRES_USER=nutrition -e POSTGRES_PASSWORD=nutrition123 postgres:15
```

Then start the backend from your IDE (`NutritionistHacksDailyApplication`) and the frontend with:

```
cd frontend
npm install
npm run dev
```

Frontend dev server runs at http://localhost:5173 and proxies API calls to the backend, so there's no CORS drama.

Flyway creates the schema and seeds a default admin user on first start. Change those credentials before deploying anywhere real.

## API in short

Everything lives under `/api/v1`. Reads are public (`GET /results`, `GET /certificates`, `GET /files/{filename}`), writes need a JWT, and login at `POST /auth/login` is rate-limited to 5 attempts per IP before a 15-minute timeout.

## Tech notes

- File uploads land in an `uploads/` folder with UUID-prefixed names; only the filename is stored in the database
- Database changes go through versioned Flyway migrations — never edit an applied one
- The production frontend is built into an nginx image that also proxies `/api/` to the backend container

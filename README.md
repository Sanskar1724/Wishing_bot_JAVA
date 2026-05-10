# Family Birthday & Anniversary Wishing Bot

A simple Spring Boot + JDBC + MySQL + Thymeleaf app to manage family members and send WhatsApp wishes on birthdays and anniversaries.

## Features

- Create a family and share a join link
- Prevent duplicate family creation (reuses existing family link)
- Prevent duplicate members by phone number
- Full members list dashboard
- Today-only celebrations page
- Editable wish text with WhatsApp `wa.me` links
- CSV export of members

## Tech Stack

- Spring Boot 3
- JDBC (no JPA/Hibernate)
- MySQL
- Thymeleaf

## Project Structure

```
src/main/java/com/example/familybot
  ├─ controller
  ├─ model
  ├─ repository
  └─ service
src/main/resources
  ├─ templates
  ├─ static
  ├─ application.properties
  └─ schema.sql
```

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8+

## Environment Variables

Required for deployment:

- `DB_URL` (example: `jdbc:mysql://HOST:PORT/DB_NAME`)
- `DB_USERNAME`
- `DB_PASSWORD`
- `PORT` (Railway sets this automatically)

Railway note: if you do not set `DB_URL`/`DB_USERNAME`/`DB_PASSWORD`, the app will also read
`MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD` from Railway.

Local defaults are defined in [src/main/resources/application.properties](src/main/resources/application.properties).

## Run Locally

1. Create a MySQL database, e.g. `family_bot`
2. (Optional) set env vars: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `PORT`
3. Run:
   - `mvn spring-boot:run`
4. Open:
   - `http://localhost:8080/create-family`

## Pages

- Create family: `/create-family`
- Join family: `/join/{code}`
- Full members: `/dashboard/{code}`
- Today only: `/dashboard/{code}/today`

## Railway Deployment

### Build / Start

- Build: `mvn clean package`
- Start: `java -jar target/family-wishing-bot-0.0.1-SNAPSHOT.jar`

### Steps

1. Push to GitHub (see below)
2. In Railway, create a new project from GitHub
3. Add a MySQL database plugin
4. Set env vars in Railway:
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
5. Deploy

### Example DB_URL

`jdbc:mysql://containers-us-west-123.railway.app:6543/railway`

## GitHub Push

```
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/Sanskar1724/Wishing_bot_JAVA.git
git push -u origin main
```

## Notes

- Schema auto-creates from [src/main/resources/schema.sql](src/main/resources/schema.sql)
- WhatsApp links use: `https://wa.me/<phone>?text=<message>`

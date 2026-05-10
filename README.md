# Family Birthday & Anniversary Wishing Bot

## Prerequisites

- Java 17
- Maven 3.8+
- MySQL 8+

## Run locally

1. Create a MySQL database: `family_bot`
2. Update env vars (optional):
   - `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `PORT`
3. Run the app:
   - `./mvnw spring-boot:run`
   - Or `mvn spring-boot:run`
4. Open: `http://localhost:8080/create-family`

## Deployment (Railway)

### Build / Start commands

- Build: `mvn clean package`
- Start: `java -jar target/family-wishing-bot-0.0.1-SNAPSHOT.jar`

### Environment variables

- `DB_URL` (example: `jdbc:mysql://HOST:PORT/DB_NAME`)
- Example (Railway MySQL): `jdbc:mysql://containers-us-west-123.railway.app:6543/railway`
- `DB_USERNAME`
- `DB_PASSWORD`
- `PORT` (Railway sets this automatically)

### Steps

1. Push code to GitHub (see steps below).
2. In Railway, create a new project from your GitHub repo.
3. Add a MySQL database in Railway.
4. Copy the MySQL connection values into Railway environment variables:
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
5. Deploy.

## GitHub push steps

1. `git init`
2. `git add .`
3. `git commit -m "Initial commit"`
4. `git branch -M main`
5. `git remote add origin https://github.com/Sanskar1724/Wishing_bot_JAVA.git`
6. `git push -u origin main`

## Pages

- Create family: `/create-family`
- Join family: `/join/{code}`
- Dashboard: `/dashboard/{code}`

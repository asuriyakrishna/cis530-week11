# CIS 530 - Week 11 Docker Microservices

This project containerizes the four Week 9 Spring Boot microservices:

- API Gateway
- Eureka Service Registry
- Student Service
- Student Profile Service

The project uses a single Maven parent POM and one Docker Compose stack.

## Prerequisites

- Docker Desktop running
- Java 25
- Windows PowerShell

## Project Structure

```text
cis530-week9/
├── pom.xml
├── docker-compose.yml
├── mvnw.cmd
├── docker/
│   └── mysql/
│       ├── student-db/init.sql
│       └── student-profile-db/init.sql
├── service-registry/
├── student-service/
├── student-profile-service/
└── api-gateway/
```

The root `pom.xml` has artifact ID `week11`, version `1.0.0`, and packaging
type `pom`. Each child module has a parent section that refers to
`../pom.xml`.

## Start the Full System

Run these commands from the repository root in PowerShell:

```powershell
docker compose down -v --remove-orphans
.\mvnw.cmd clean package -DskipTests
docker compose up --build
```

Use `docker compose up --build --wait --wait-timeout 180` to wait for the
containers to become healthy. MySQL initialization only runs when an empty
database volume is created, so `down -v` removes old data and reruns the seed
scripts.

## Services and Ports

| Service | Container name | Host URL or port |
|---|---|---|
| Eureka | `service-registry` | http://localhost:8761 |
| Student Service | `student-service` | http://localhost:8081 |
| Student Profile Service | `student-profile-service` | http://localhost:8082 |
| API Gateway | `api-gateway` | http://localhost:8085 |
| Student MySQL | `student-db` | Host port `3307`, container port `3306` |
| Profile MySQL | `student-profile-db` | Host port `3308`, container port `3306` |

The API Gateway container runs on port `8080`; Docker maps host port `8085`
to container port `8080`.

## Docker Configuration

Each Spring Boot module keeps its original `application.properties` for local
development and uses `application-docker.properties` for container settings.
Compose activates the `docker` Spring profile.

Docker networking uses container names rather than `localhost`:

- Student database host: `student-db`
- Student Profile database host: `student-profile-db`
- Eureka host: `service-registry`

The Docker database credentials are:

- Username: `root`
- Password: `Janu@2012`
- Student database: `student_db`
- Student Profile database: `student_profile_db`

## Seed Data

The first MySQL initialization creates and seeds these records:

### Students

1. Ali Rezaei, `ali.rezaei@example.com`
2. Sara Johnson, `sara.johnson@example.com`
3. David Miller, `david.miller@example.com`

### Student Profiles

1. Computer Science, GPA `3.75`
2. Software Engineering, GPA `3.90`
3. Information Systems, GPA `3.60`

## Test the System

Open Eureka:

```text
http://localhost:8761
```

Confirm that `API-GATEWAY`, `STUDENT-SERVICE`, and
`STUDENT-PROFILE-SERVICE` are registered and UP.

Test through the API Gateway:

```text
GET http://localhost:8085/api/students
GET http://localhost:8085/api/students/1
GET http://localhost:8085/api/students/1/full
GET http://localhost:8085/api/profiles
GET http://localhost:8085/api/profiles/student/1
```

Create a student:

```json
POST http://localhost:8085/api/students
Content-Type: application/json

{
  "firstName": "Nina",
  "lastName": "Brown",
  "email": "nina.brown@example.com"
}
```

Create a profile:

```json
POST http://localhost:8085/api/profiles
Content-Type: application/json

{
  "studentId": 4,
  "major": "Data Science",
  "gpa": 3.85,
  "bio": "Enjoys analytics and cloud development."
}
```

## Docker Commands

```powershell
docker compose ps
docker compose logs -f api-gateway
docker compose logs -f student-service
docker compose logs -f student-profile-service
docker compose stop
docker compose down
docker compose down -v
```

Use `docker compose down -v` or `docker compose down -v --remove-orphans`
when the database volumes must be deleted and initialization scripts rerun.

## Troubleshooting

- If a service uses `localhost`, replace it with the Docker service name in
  `application-docker.properties`.
- If Eureka does not show a service, wait for startup and verify the Eureka URL
  is `http://service-registry:8761/eureka`.
- If tables or seed records are missing, run `docker compose down -v`,
  rebuild, and start the stack again.
- If `/api/students/1/full` fails, verify both microservices are UP in Eureka
  and the Feign client is named `student-profile-service`.
- A root URL such as `http://localhost:8085/` may return 404; test the route
  URLs instead.

## Submission

The submitted folder should include:

1. The four updated Spring Boot projects
2. The root `pom.xml`
3. The root `docker-compose.yml`
4. All four module Dockerfiles
5. Both MySQL `init.sql` scripts
6. Screenshots of `docker compose ps`, the Eureka dashboard, and a successful
   `/api/students/1/full` request through the gateway

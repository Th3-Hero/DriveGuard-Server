# DriveGuard

DriveGuard is a mobile application that monitors and tracks a user’s driving habits and scores them according to the
risk factor a driver can maintain. The risk factor is tabulated using the many sensors on a mobile device to monitor
hard factors such as speed, acceleration, cornering aggressiveness and soft factors like driving conditions and trip
length. For each aggressive maneuver or excessive use of speed over the posted limit, the application will deduct a
certain amount of points from the total 100 available.

Whenever a trip is ended, the application will display the trip
summary with a trip score and if any points were taken off, a brief description of why and when the deductions occurred.
Throughout multiple trips, DriveGuard will create an average driver score using the combined trip scores and provide a
breakdown of where the most penalized sections are (such as excessive use of prolonged speed or sudden acceleration).

## Features
### User
- Signup
- Login
- Logout
- Account deletion
- Account recover
### Trip
- Start Trip
- Stop Trip
- Add Driving Event
### Driving Context (Location Based)
- Weather
- Roads
- Addresses
- Distance


## Tech Stack
- Spring
  - Spring Boot
  - Spring MVC (Servlet)
      - +Jakarta Validation
  - Spring Security
- Hibernate
  - +Flyway
- Quartz
- Lombok
- Postgres


## API Docs
[Swagger docs](https://drive-guard-api.the-hero.dev/swagger)

### Deployment:
- Hosted on a local server with secure access enabled via Cloudflare Tunnels.
- Deployment is semi-automated using custom scripts.

## Pre-requisites:

- Java Development Kit (JDK) 21 found [here](https://adoptium.net/temurin/releases/?os=windows)
- Maven (build tool) found [here](https://maven.apache.org/download.cgi)
- PostgresSQL (database) found [here](https://www.postgresql.org/download/)

## Credits
Backend:
- William Paetz (Myself)
- Brooke Cronin (Score calculation)

Frontend:
- Connor Miller
- Stephan Popescu
- Brooke Cronin
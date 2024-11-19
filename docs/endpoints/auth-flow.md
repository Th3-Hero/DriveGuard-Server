## Authorization Flow

### Signup
Base url: https://drive-guard-api.the-hero.dev \
Endpoint: /auth/signup \
(https://drive-guard-api.the-hero.dev/auth/signup)

Body takes:
- first name (2-64 characters)
- last name (2-64 characters)
- username (6-64 characters)
- password (8-64 characters)
  - Must contain at least one uppercase letter
  - Password must contain one number

Returns the basic driver information. Mainly you will want to save the driver id.

**NOTE: If you want to auto login the user after signing up you will need to reuse the username and password and call the Login endpoint with them in order to get a session token.**


### Login
Base url: https://drive-guard-api.the-hero.dev \
Endpoint: /auth/login \
(https://drive-guard-api.the-hero.dev/auth/login)

Query Parameters:
- username
- password

(https://drive-guard-api.the-hero.dev/auth/login?username=REPLACE_USERNAME&password=REPLACE_PASSWORD)

Returns session token used for authorization. **YOU WILL WANT TO SAVE THIS**

**Note: You will need the driver id and the session token anytime you use an endpoint that has authorization**


### Change password
Password changes don't invalidate sessions. No need to login again.
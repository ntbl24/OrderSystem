# Profile service

The service listens on port `8089`, uses the `/profile` context path, and stores profile data
in PostgreSQL. Set `PROFILE_DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` to override the local
database defaults. Requests require a bearer token.

## Endpoints

| Method | Path | Purpose |
|---|---|---|
| `GET` | `/profiles/{profileId}` | Read a profile by profile ID |
| `GET` | `/profiles` | List profiles with `page` and `size` pagination |
| `GET` | `/profiles/me` | Read the authenticated user's profile |
| `PUT` | `/profiles/me` | Update first name, last name, date of birth, and city |
| `PATCH` | `/profiles/me/avatar` | Set the avatar URL returned by File Service |
| `GET` | `/profiles/search?query=...` | Search username, email, name, or city |
| `POST` | `/internal/profiles` | Create a profile for a user (service-to-service) |
| `GET` | `/internal/profiles/user/{userId}` | Read a profile by user ID (service-to-service) |

List and search responses are paginated; page size is capped at 100. Internal endpoints remain
authenticated and are intended for calls between services. The public gateway route is
`/api/v1/profile/**`. User ID, username, and email come from Identity during internal profile
creation; the self-update endpoint changes only first name, last name, date of birth, and city.

## Application structure

The web adapters depend on inbound use-case ports, and application services depend on the
profile repository and current-user outbound ports. Read operations are isolated in
`ProfileQueryService`; profile creation, personal-detail updates, and avatar updates each have
their own application service. `SecurityContextCurrentUserAdapter` keeps Spring Security
context access out of application services.

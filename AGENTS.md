# ImageSeal project guidance

- Frontend: existing TanStack Start/React UI. Preserve page structure, styles, assets, and interactions.
- Backend: Java 21, Spring Boot, Spring Data JPA, Spring Security with JWT; MySQL is the only database.
- Keep database passwords and JWT secrets in environment variables, never client code or version control.
- Frontend API calls use `src/lib/api.ts` and the Spring Boot API on port 8081 by default.
- Image identity is byte-for-byte SHA-256. Image uploads and checks are stored in backend-managed persistent local storage.
- Users can only access their own images and verification records.

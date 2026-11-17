# ImageSeal

ImageSeal is the existing React/TanStack application connected to a Java 21, Spring Boot 3.5.3 REST API and MySQL. User passwords use BCrypt; requests use JWT bearer authentication. The API calculates SHA-256 hashes and stores uploaded image bytes in local persistent storage.

## Start the backend

Create the database in MySQL Workbench or in a MySQL command prompt (not directly in PowerShell):

```sql
CREATE DATABASE IF NOT EXISTS imageseal;
```

In PowerShell, from `backend`:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/imageseal"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your-actual-mysql-password"
$env:JWT_SECRET = "generate-a-random-secret-of-at-least-32-characters"
$env:IMAGE_ENCRYPTION_KEY = "your-stable-base64-encoded-32-byte-key"
$env:IMAGE_STORAGE_DIR = "../storage/images"
mvn spring-boot:run
```

## Start the frontend

From the repository root, in a second terminal:

```powershell
npm install
npm run dev
```

The React API base URL defaults to `http://localhost:8081/api`; override it with `VITE_API_URL` if needed. Copy `.env.example` to `.env` only if you want to override that URL. No database passwords or JWT secrets belong in frontend environment variables.

Hibernate creates/updates `users`, `images`, `verifications`, `image_versions`, `duplicate_uploads`, and `audit_logs` in MySQL on backend startup. MySQL is the only application database; old cloud database clients and project configuration are removed.

Run frontend checks with `npm test` and `npm run build`. Build the backend from `backend` with `mvn package`.

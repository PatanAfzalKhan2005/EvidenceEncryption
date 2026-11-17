# ImageSeal Java API

Spring Boot 3.5 / Java 21 API. Set `DB_PASSWORD`, a random `JWT_SECRET` of at least 32 characters, and `IMAGE_ENCRYPTION_KEY` (Base64 encoding of exactly 32 random bytes) before startup. Keep the encryption key stable and backed up; files stored with it cannot be decrypted without it. Optional settings: `DB_URL`, `DB_USERNAME`, `IMAGE_STORAGE_DIR` (defaults to `../storage/images` relative to `backend`). The MySQL database `imageseal` must exist and be reachable. JPA `update` creates and updates `users`, `images`, `verifications`, `image_versions`, `duplicate_uploads`, and `audit_logs` on startup.

From this directory:

```powershell
$env:DB_PASSWORD = "your-local-mysql-password"
$env:JWT_SECRET = "replace-with-a-random-secret-of-at-least-32-characters"
$env:IMAGE_ENCRYPTION_KEY = [Environment]::GetEnvironmentVariable("IMAGE_ENCRYPTION_KEY", "User")
mvn spring-boot:run
```

Generate and save the AES key once in PowerShell, then reuse it on every restart:

```powershell
$imageSealKey = [Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
[Environment]::SetEnvironmentVariable("IMAGE_ENCRYPTION_KEY", $imageSealKey, "User")
Remove-Variable imageSealKey
```

Keep this key stable and backed up; a new key makes previously stored image files unreadable.

API routes:

- `POST /api/auth/register`, `POST /api/auth/login`
- `POST /api/images` multipart field `file`; `GET /api/images`; `GET /api/images/{id}`; `GET /api/images/{id}/file`; `DELETE /api/images/{id}`
- `POST /api/images/{id}/verify` multipart field `file`; `GET /api/verifications`; `DELETE /api/verifications/{id}`; `DELETE /api/verifications`
- `GET /api/images/{id}/versions`; `POST /api/images/{id}/versions`; `DELETE /api/images/{id}/versions/{versionId}`
- `GET /api/duplicates`; `GET /api/audit-logs`; authenticated image files via `GET /api/files/{id}`

All routes other than auth require `Authorization: Bearer <token>`. Uploaded JPG/PNG files are limited to 15 MB. Image version management, duplicate-upload tracking, verification history, and audit-log retrieval are also backed by MySQL.

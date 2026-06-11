-- New admin password: NHD@Admin2024! (BCrypt cost 12)
-- Hash verified against the password before seeding.
UPDATE admin_users
SET password_hash = '$2b$12$Gh5L7tHTXGHCOsxgXAudcuKSVSyR8yMTdnCibe2TwK3YhlM2IlJVy'
WHERE username = 'admin';

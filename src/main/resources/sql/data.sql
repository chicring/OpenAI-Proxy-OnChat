INSERT INTO User (username, password, role)
SELECT * FROM (
                  SELECT 'admin' AS username, 'admin' AS password, 'ADMIN' AS role
              ) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM User
);

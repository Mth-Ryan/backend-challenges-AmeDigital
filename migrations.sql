-- Drops
DROP TABLE IF EXISTS planets;

-- Migrate
CREATE TABLE IF NOT EXISTS planets (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  climate TEXT NOT NULL,
  terrain TEXT NOT NULL
);


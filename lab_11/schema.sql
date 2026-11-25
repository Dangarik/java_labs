CREATE TABLE IF NOT EXISTS departments (
    id INTEGER PRIMARY KEY,
    name CHAR(30) NOT NULL CHECK (length(trim(name)) BETWEEN 1 AND 30),
    phone CHAR(15) CHECK (phone IS NULL OR length(phone) <= 15)
);

CREATE TABLE IF NOT EXISTS employees (
    id INTEGER PRIMARY KEY,
    last_name VARCHAR(20) NOT NULL CHECK (length(trim(last_name)) BETWEEN 1 AND 20),
    first_name VARCHAR(10) NOT NULL CHECK (length(trim(first_name)) BETWEEN 1 AND 10),
    position VARCHAR(20) CHECK (position IS NULL OR length(position) <= 20),
    department_id INTEGER NOT NULL REFERENCES departments(id)
);

CREATE TABLE IF NOT EXISTS tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    description VARCHAR(50) NOT NULL CHECK (length(trim(description)) BETWEEN 1 AND 50),
    employee_id INTEGER NOT NULL REFERENCES employees(id) ON DELETE CASCADE
);

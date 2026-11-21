CREATE TABLE IF NOT EXISTS event_registrations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_name TEXT NOT NULL,
    register_number TEXT NOT NULL,
    department TEXT NOT NULL,
    email TEXT NOT NULL,
    phone TEXT NOT NULL,
    event_name TEXT NOT NULL,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (register_number, event_name)
);

CREATE INDEX IF NOT EXISTS idx_event_registrations_register_number
    ON event_registrations (register_number);

CREATE INDEX IF NOT EXISTS idx_event_registrations_event_name
    ON event_registrations (event_name);

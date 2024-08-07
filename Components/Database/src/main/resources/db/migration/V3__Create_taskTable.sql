CREATE TABLE task (
    unique_id TEXT NOT NULL,
    task_type TEXT,
    task_parameters TEXT,
    priority INT,
    routing_key TEXT,
    creation_time TIMESTAMP,
    start_time TIMESTAMP,
    end_time TIMESTAMP
);
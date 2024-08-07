CREATE TABLE task_history (
    unique_id TEXT NOT NULL,
    worker_name TEXT NOT NULL,
    task_type TEXT,
    task_parameters TEXT,
    priority INT,
    routing_key TEXT,
    creation_time TIMESTAMP,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    number_of_items_to_process INT,
    processed INT,
    successful INT,
    success BOOLEAN
);
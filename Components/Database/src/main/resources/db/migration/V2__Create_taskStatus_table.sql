CREATE TABLE task_status (
    task_id TEXT NOT NULL,
    number_of_items_to_process INT,
    processed INT,
    successful INT,
    success BOOLEAN
)
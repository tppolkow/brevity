DROP TABLE IF EXISTS brevity.user_request;

CREATE TABLE IF NOT EXISTS brevity.user_request (
    summary_id INTEGER NOT NULL,
    id varchar(25) NOT NULL,
    PRIMARY KEY (id, summary_id)
);
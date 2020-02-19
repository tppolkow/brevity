CREATE TABLE IF NOT EXISTS `user` (
    user_id INTEGER NOT NULL PRIMARY KEY,
    openID_id INTEGER,
    name VARCHAR(25),
    email VARCHAR(25)
);
CREATE TABLE IF NOT EXISTS `summary` (
    summary_id INTEGER NOT NULL PRIMARY KEY,
    title VARCHAR(25),
    data VARCHAR(25)
);
CREATE TABLE IF NOT EXISTS `user_request` (
    request_id INTEGER NOT NULL PRIMARY KEY,
    user_id INTEGER
);
CREATE TABLE IF NOT EXISTS `user_request_summaries` (
    request_id INTEGER,
    summary_id INTEGER,
    PRIMARY KEY (request_id, summary_id)
)

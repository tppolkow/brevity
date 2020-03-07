SET search_path = brevity;

CREATE SEQUENCE brevity_summary_id_seq AS integer START 1 OWNED BY brevity.summary.summary_id;

ALTER TABLE brevity.summary ALTER COLUMN summary_id SET DEFAULT nextval('brevity_summary_id_seq');
ALTER TABLE brevity.summary ALTER COLUMN title TYPE text;
ALTER TABLE brevity.summary ALTER COLUMN data TYPE text;
ALTER TABLE brevity.summary ADD COLUMN finished boolean;


DROP TABLE IF EXISTS brevity.user_request;

CREATE TABLE IF NOT EXISTS brevity.user_request (
    summary_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, summary_id)
);

DROP TABLE IF EXISTS brevity.user_request_summaries;

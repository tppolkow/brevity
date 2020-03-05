ALTER TABLE brevity.user DROP COLUMN openID_id;
ALTER TABLE brevity.user RENAME COLUMN user_id TO id;
ALTER TABLE brevity.user ALTER COLUMN id TYPE varchar(25), ALTER COLUMN email TYPE varchar(254);
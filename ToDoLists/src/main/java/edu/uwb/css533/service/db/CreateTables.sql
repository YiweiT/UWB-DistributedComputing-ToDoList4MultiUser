CREATE TABLE IF NOT EXISTS USERS_INFO (
    USERNAME VARCHAR(255) PRIMARY KEY,
    PASSWORD VARCHAR (255) NOT NULL,
    LISTIDS TEXT[]
    );

CREATE TABLE IF NOT EXISTS LISTS (
                                     LISTID SERIAL PRIMARY KEY,
                                     LISTNAME VARCHAR (255) NOT NULL,
    LIST_TYPE VARCHAR (20) DEFAULT 'individual',
    LAST_MODIFIED_DATE TIMESTAMPTZ NOT NULL DEFAULT NOW();
    );

CREATE TABLE IF NOT EXISTS TASKS (
                                     TASKID SERIAL PRIMARY KEY,
                                     TASKNAME VARCHAR (255) NOT NULL,
    CONTENT TEXT,
    STATUS VARCHAR (20) NOT NULL DEFAULT 'Not Started',
    LISTID SERIAL NOT NULL,
    LAST_MODIFIED_DATE TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    FOREIGN KEY (LISTID) REFERENCES LISTS (LISTID)
    );
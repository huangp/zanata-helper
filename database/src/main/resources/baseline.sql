CREATE TABLE IF NOT EXISTS Sync_Work_Config_table (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    description VARCHAR(50),
    createdDate TIMESTAMP,
    yaml CLOB NOT NULL
);

CREATE TABLE IF NOT EXISTS Job_Status_table (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    workId BIGINT NOT NULL,
    jobType VARCHAR(20) NOT NULL,
    jobStatusType VARCHAR(20) NOT NULL,
    startTime TIMESTAMP,
    endTime TIMESTAMP,
    nextStartTime TIMESTAMP,
    FOREIGN KEY (workId) REFERENCES Sync_Work_Config_table (id)
);

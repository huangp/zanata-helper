CREATE TABLE IF NOT EXISTS Sync_Work_Config_table (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    description VARCHAR(50),
    createdDate TIMESTAMP,
    yaml CLOB NOT NULL
)

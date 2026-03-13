-- Performance maintenance / optional upgrades for existing databases.
-- Safe to run multiple times (uses IF EXISTS / IF NOT EXISTS guards).
--
-- Notes:
-- - This project uses DriverManager connections in the app; we optimize by queries + indexes.
-- - Full-text search is optional and will be enabled only if SQL Server FullText is installed.
-- - Run this script manually in SQL Server (SSMS) against the ReviewPlatform database.

-- 1) Index changes (low risk)
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_Alerts_ReviewId' AND object_id = OBJECT_ID('dbo.Alerts'))
BEGIN
    CREATE INDEX IX_Alerts_ReviewId ON dbo.Alerts(review_id);
END

-- The original IX_Products_Category_Status is usually not worth the memory for this workload:
-- findByCategory now avoids LOWER(category), and the status+created index + small dataset is sufficient.
IF EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'IX_Products_Category_Status' AND object_id = OBJECT_ID('dbo.Products'))
BEGIN
    DROP INDEX IX_Products_Category_Status ON dbo.Products;
END

-- 2) Optional: Full-text search for Products (accelerates keyword search)
IF (SERVERPROPERTY('IsFullTextInstalled') = 1)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM sys.fulltext_catalogs WHERE name = 'FTC_ReviewPlatform')
    BEGIN
        CREATE FULLTEXT CATALOG FTC_ReviewPlatform;
    END

    -- Find a suitable unique key index name for FULLTEXT KEY INDEX
    DECLARE @keyIndex sysname;
    SELECT TOP (1) @keyIndex = i.name
    FROM sys.indexes i
    WHERE i.object_id = OBJECT_ID('dbo.Products')
      AND i.is_unique = 1
      AND i.is_disabled = 0
    ORDER BY i.is_primary_key DESC, i.name ASC;

    IF (@keyIndex IS NOT NULL)
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM sys.fulltext_indexes WHERE object_id = OBJECT_ID('dbo.Products'))
        BEGIN
            DECLARE @sql nvarchar(max) =
                N'CREATE FULLTEXT INDEX ON dbo.Products(' +
                N'  name LANGUAGE 1066, ' +         -- Vietnamese
                N'  category LANGUAGE 1066, ' +
                N'  description LANGUAGE 1066 ' +
                N') KEY INDEX ' + QUOTENAME(@keyIndex) +
                N' WITH CHANGE_TRACKING AUTO;';

            EXEC sp_executesql @sql;
        END
    END
END
GO

-- 3) Index audit: sizes and usage (use this to decide what to drop)
-- If you run this on SQL Server, it will show "unused" indexes (seeks/scans/lookups = 0).
SELECT
    OBJECT_SCHEMA_NAME(i.object_id) AS [schema_name],
    OBJECT_NAME(i.object_id) AS [table_name],
    i.name AS [index_name],
    i.type_desc,
    s.user_seeks,
    s.user_scans,
    s.user_lookups,
    s.user_updates,
    p.rows,
    (a.total_pages * 8) AS size_kb
FROM sys.indexes i
LEFT JOIN sys.dm_db_index_usage_stats s
    ON s.object_id = i.object_id
   AND s.index_id = i.index_id
   AND s.database_id = DB_ID()
LEFT JOIN (
    SELECT object_id, index_id, SUM(rows) AS rows
    FROM sys.partitions
    GROUP BY object_id, index_id
) p ON p.object_id = i.object_id AND p.index_id = i.index_id
LEFT JOIN (
    SELECT object_id, index_id, SUM(total_pages) AS total_pages
    FROM sys.allocation_units au
    JOIN sys.partitions pa ON au.container_id = pa.hobt_id
    GROUP BY object_id, index_id
) a ON a.object_id = i.object_id AND a.index_id = i.index_id
WHERE i.object_id IN (
    OBJECT_ID('dbo.Users'),
    OBJECT_ID('dbo.Products'),
    OBJECT_ID('dbo.Reviews'),
    OBJECT_ID('dbo.Alerts'),
    OBJECT_ID('dbo.AlertReasons'),
    OBJECT_ID('dbo.AlertEvidences'),
    OBJECT_ID('dbo.AuditLog'),
    OBJECT_ID('dbo.ReviewEdits')
)
  AND i.name IS NOT NULL
ORDER BY size_kb DESC, [table_name], [index_name];
GO

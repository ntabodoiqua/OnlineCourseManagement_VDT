-- Migration script to remove videoUrl and attachmentUrl fields from lesson table
-- Execute this if you want to manually remove these columns

-- Step 1: Backup existing data (optional)
-- CREATE TABLE lesson_backup_video_attachment AS 
-- SELECT id, title, video_url, attachment_url 
-- FROM lesson 
-- WHERE video_url IS NOT NULL OR attachment_url IS NOT NULL;

-- Step 2: Remove the columns (Hibernate will do this automatically with ddl-auto: update)
-- ALTER TABLE lesson DROP COLUMN video_url;
-- ALTER TABLE lesson DROP COLUMN attachment_url;

-- Note: Since the project uses ddl-auto: update, these changes will be applied automatically
-- when you restart the Spring Boot application.

-- If you want to check which lessons had video or attachment URLs before removal:
-- SELECT id, title, video_url, attachment_url 
-- FROM lesson 
-- WHERE video_url IS NOT NULL OR attachment_url IS NOT NULL; 
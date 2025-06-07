-- Migration để thêm field description vào table lesson
-- Chạy script này trước khi deploy code mới

-- Kiểm tra xem column description đã tồn tại chưa
-- Nếu chưa thì thêm vào
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'lesson' 
        AND column_name = 'description'
    ) THEN
        ALTER TABLE lesson ADD COLUMN description VARCHAR(500);
        RAISE NOTICE 'Column description đã được thêm vào table lesson';
    ELSE
        RAISE NOTICE 'Column description đã tồn tại trong table lesson';
    END IF;
END $$;

-- Có thể thêm index nếu cần tìm kiếm theo description
-- CREATE INDEX IF NOT EXISTS idx_lesson_description ON lesson USING gin(to_tsvector('english', description)); 
# Test Lesson Description Field

## Các thay đổi đã thực hiện:

### 1. Entity Lesson
- ✅ Thêm field `String description;` vào `Lesson.java`

### 2. DTOs
- ✅ Thêm field `description` vào `LessonRequest.java`
- ✅ Thêm field `description` vào `LessonResponse.java`
- ✅ Thêm field `description` vào `LessonUpdateRequest.java`

### 3. Database Migration
- ✅ Tạo file `migration_add_lesson_description.sql`

### 4. Service & Mapper
- ✅ `LessonMapper` sẽ tự động map field description
- ✅ `LessonService` sử dụng mapper nên không cần thay đổi

## Test Cases:

### 1. Tạo lesson mới với description
```bash
POST /lms/lessons
{
  "title": "Bài học test",
  "description": "Đây là mô tả của bài học test",
  "content": "Nội dung chi tiết của bài học..."
}
```

### 2. Cập nhật lesson với description
```bash
PUT /lms/lessons/{lessonId}
{
  "title": "Bài học đã cập nhật",
  "description": "Mô tả đã được cập nhật",
  "content": "Nội dung đã được cập nhật..."
}
```

### 3. Lấy lesson và kiểm tra description trong response
```bash
GET /lms/lessons/{lessonId}
```

Expected Response:
```json
{
  "code": 1000,
  "result": {
    "id": "lesson-id",
    "title": "Lesson Title",
    "description": "Lesson Description",
    "content": "Lesson Content",
    "createdAt": "...",
    "updatedAt": "...",
    "createdBy": {...},
    "courseCount": 1
  }
}
```

### 4. Lấy danh sách lessons trong course và kiểm tra description
```bash
GET /lms/courses/{courseId}/lessons
```

Expected: Mỗi `CourseLesson` object sẽ có `lesson.description`

## Deployment Notes:

1. **Trước khi deploy code mới**: Chạy script migration
   ```sql
   \i migration_add_lesson_description.sql
   ```

2. **Hoặc**: Nếu sử dụng JPA auto-update, chỉ cần restart application

3. **Kiểm tra database**: Verify column description đã được thêm
   ```sql
   \d lesson
   ```

## Rollback (nếu cần):
```sql
ALTER TABLE lesson DROP COLUMN description;
``` 
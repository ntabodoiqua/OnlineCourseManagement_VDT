# Quiz Attempt Enrollment Context - Bug Fix

## Vấn đề (Problem)
Quiz attempts được lưu trữ chỉ dựa trên `quiz_id` và `student_id`, không phân biệt theo enrollment/course context. Điều này dẫn đến việc khi một bài học có thể nằm trong nhiều khóa học khác nhau, trạng thái quiz attempt sẽ được chia sẻ giữa các khóa học.

### Ví dụ tình huống:
1. Lesson "JavaScript Basics" có Quiz "JS Quiz 1"
2. Lesson này được sử dụng trong Course A và Course B
3. Student làm quiz trong Course A và đạt 90%
4. Khi student học Course B, quiz đã hiển thị completed với score 90%

## Giải pháp (Solution)
Thêm `enrollment_id` vào `QuizAttempt` entity để tách biệt quiz attempts theo từng enrollment (course context).

## Các thay đổi đã thực hiện

### 1. Database Schema Changes

#### Entity Updates:
- **QuizAttempt.java**: Thêm field `enrollment` với quan hệ ManyToOne với Enrollment
- **QuizAttemptResponse.java**: Thêm field `enrollment` 

#### Migration Files:
- **V13__Add_enrollment_to_quiz_attempt.sql**: Thêm cột `enrollment_id` và constraints
- **V14__Populate_quiz_attempt_enrollment_id.sql**: Populate data cho records hiện có

### 2. Repository Updates
- **QuizAttemptRepository.java**: Thêm các method query với enrollment context:
  - `findByQuizIdAndStudentIdAndEnrollmentIdOrderByAttemptNumberDesc()`
  - `findByQuizIdAndStudentIdAndEnrollmentIdAndStatus()`
  - `countByQuizIdAndStudentIdAndEnrollmentId()`
  - etc.

### 3. Service Layer Updates
- **QuizAttemptService.java**: 
  - Thêm method `getCurrentEnrollmentForQuiz()` để tìm enrollment context
  - Cập nhật tất cả quiz attempt operations để sử dụng enrollment context
  - Methods updated:
    - `startQuizAttempt()`
    - `getCurrentAttempt()`
    - `getStudentAttemptHistory()`
    - `getBestScore()`
    - `submitQuiz()`

### 4. Key Changes in Logic:

#### Before:
```java
// Tìm attempt chỉ dựa trên quiz + student
Optional<QuizAttempt> attempt = quizAttemptRepository
    .findByQuizIdAndStudentIdAndStatus(quizId, studentId, AttemptStatus.IN_PROGRESS);
```

#### After:
```java
// Tìm enrollment context trước
Enrollment enrollment = getCurrentEnrollmentForQuiz(quiz, student);

// Tìm attempt theo quiz + student + enrollment
Optional<QuizAttempt> attempt = quizAttemptRepository
    .findByQuizIdAndStudentIdAndEnrollmentIdAndStatus(
        quizId, studentId, enrollment.getId(), AttemptStatus.IN_PROGRESS);
```

## Tác động (Impact)

### Positive:
- ✅ Quiz attempts được tách biệt theo từng khóa học
- ✅ Student có thể làm cùng một quiz nhiều lần trong các khóa học khác nhau
- ✅ Progress tracking chính xác theo context của từng course
- ✅ Không breaking changes cho frontend API

### Considerations:
- ⚠️ Existing quiz attempt data cần được populate với enrollment_id
- ⚠️ Cần test kỹ migration script với data hiện có
- ⚠️ Performance: thêm JOIN với enrollment table

## Testing Scenarios

### Test Case 1: Same lesson in multiple courses
1. Tạo Lesson A với Quiz X
2. Add Lesson A vào Course 1 và Course 2  
3. Student enroll cả 2 courses
4. Student làm Quiz X trong Course 1 → score 80%
5. Student vào Course 2, Quiz X should show as not attempted
6. Student làm Quiz X trong Course 2 → score 90%
7. Verify scores are separate for each course

### Test Case 2: Data migration
1. Có quiz attempts cũ trong database
2. Run migration V13 và V14
3. Verify all quiz attempts có enrollment_id
4. Verify quiz attempts functionality still works

## Rollback Plan
Nếu cần rollback:
1. Remove foreign key constraint
2. Drop enrollment_id column
3. Revert service logic changes
4. Deploy previous version

## Deployment Steps
1. Deploy backend với migration files
2. Monitor logs for any errors
3. Verify quiz functionality works correctly
4. Test with multiple courses scenario 
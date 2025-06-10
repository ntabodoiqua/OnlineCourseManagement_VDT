# Báo cáo chi tiết về Backend - Hệ thống Quản lý Khóa học Trực tuyến

### Giới thiệu bài toán

**Phát biểu bài toán và động lực thực hiện dự án**

Dự án tập trung vào việc xây dựng một hệ thống backend mạnh mẽ và linh hoạt cho một nền tảng quản lý khóa học trực tuyến. Động lực chính xuất phát từ nhu cầu ngày càng tăng về giáo dục trực tuyến, đòi hỏi một hệ thống có khả năng quản lý hiệu quả các khóa học, người dùng và nội dung học tập.

**Nhu cầu/mục tiêu cần phải đáp ứng**

Hệ thống cần đáp ứng các nhu cầu sau:

*   **Người dùng:** Đăng ký, đăng nhập, quản lý thông tin cá nhân, tìm kiếm và tham gia khóa học, theo dõi tiến độ học tập, đánh giá khóa học.
*   **Giảng viên (Instructor):** Tạo và quản lý khóa học (thêm, sửa, xóa), tải lên tài liệu, quản lý học viên trong khóa học của mình.
*   **Quản trị viên (Admin):** Quản lý toàn bộ hệ thống, bao gồm người dùng, khóa học, danh mục, và xem các thống kê tổng quan.

### Phân tích yêu cầu bài toán và các nghiên cứu liên quan

**Phân tích yêu cầu bài toán**

Dựa trên mã nguồn, hệ thống được thiết kế để giải quyết các yêu cầu chính sau:

1.  **Authentication & Authorization:**
    *   Sử dụng JWT (JSON Web Token) để xác thực người dùng. Có cơ chế xử lý token hết hạn và đăng xuất (`InvalidatedToken`).
    *   Phân quyền rõ ràng giữa 3 vai trò: `STUDENT`, `INSTRUCTOR`, `ADMIN` bằng Spring Security với `@PreAuthorize`.

2.  **Quản lý Khóa học (Course Management):**
    *   Giảng viên có thể tạo, cập nhật, xóa khóa học.
    *   Mỗi khóa học có đầy đủ thông tin: tên, mô tả, mô tả chi tiết, ảnh đại diện, trạng thái, ngày bắt đầu/kết thúc, giảng viên, danh mục.
    *   Khóa học được liên kết với các bài học (`Lesson`), tài liệu (`CourseDocument`).

3.  **Tham gia khóa học:**
    *   Học viên có thể duyệt và đăng ký khóa học.
    *   Hệ thống theo dõi tiến độ học tập thông qua thực thể `Enrollment` và `Progress`.

4.  **Đánh giá và nhận xét:**
    *   Học viên có thể để lại đánh giá (sao) và nhận xét cho khóa học thông qua thực thể `CourseReview`.

5.  **Quản trị viên:**
    *   Có các API để quản lý người dùng và khóa học.
    *   Có API thống kê (`StatisticController`) để lấy dữ liệu tổng quan.

**Làm rõ các kết quả tìm hiểu từ các giải pháp/nghiên cứu khác liên quan đến bài toán**

Hệ thống áp dụng các mẫu thiết kế và công nghệ phổ biến trong ngành, tương tự như các nền tảng LMS (Learning Management System) khác như Moodle, Coursera, Udemy.

*   **Kiến trúc:** Sử dụng kiến trúc phân lớp (Layered Architecture) rõ ràng với các tầng `Controller`, `Service`, `Repository`. Đây là một kiến trúc phổ biến, dễ phát triển và bảo trì.
*   **Cơ sở dữ liệu:** Dựa vào các thực thể JPA, hệ thống có thể hoạt động với các CSDL quan hệ như PostgreSQL hoặc MySQL.
*   **API:** Xây dựng RESTful API, một tiêu chuẩn cho các hệ thống hiện đại, giúp dễ dàng tích hợp với các client (web, mobile).

**Các điểm đạt được/chưa đạt được của các giải pháp/nghiên cứu đã tìm hiểu**

*   **Điểm đạt được:**
    *   Hệ thống đã triển khai đầy đủ các chức năng cốt lõi của một hệ thống quản lý khóa học.
    *   Cấu trúc code được tổ chức tốt, dễ hiểu.
    *   Sử dụng các công nghệ hiện đại và phổ biến (Spring Boot, JPA, JWT).
    *   Có xử lý phân quyền chi tiết, đảm bảo an toàn cho hệ thống.

*   **Điểm có thể cải thiện:**
    *   Mặc dù cấu trúc tốt, dự án chưa hoàn toàn tuân thủ theo `Clean Architecture` hay `Hexagonal Architecture` như đề bài gợi ý. Việc này có thể được cải thiện bằng cách tách biệt rõ ràng hơn nữa giữa business logic và application logic, ví dụ như sử dụng các `use case` riêng biệt.
    *   Thiếu các bài kiểm thử (unit test, integration test), điều này quan trọng để đảm bảo chất lượng và sự ổn định của hệ thống khi có thay đổi.

### Giải pháp đề xuất

**Tổng quan giải pháp đề xuất và các điểm cải tiến hơn so với các giải pháp/nghiên cứu đang có**

Giải pháp được xây dựng dựa trên Spring Boot, một framework mạnh mẽ và phổ biến để tạo các ứng dụng Java.

*   **Kiến trúc phân lớp:**
    *   `Controller`: Tiếp nhận request từ client, gọi đến `Service` tương ứng.
    *   `Service`: Chứa toàn bộ logic nghiệp vụ của ứng dụng.
    *   `Repository`: Tương tác với cơ sở dữ liệu thông qua Spring Data JPA.
    *   `Entity`: Định nghĩa các đối tượng được ánh xạ xuống CSDL.
    *   `DTO` và `Mapper`: Dùng để truyền dữ liệu giữa các lớp một cách an toàn và tránh lộ cấu trúc của `Entity`.

*   **Điểm cải tiến:**
    *   **Bảo mật:** Sử dụng Spring Security với JWT và phân quyền chi tiết dựa trên vai trò. Có cơ chế chống vét cạn mật khẩu (`loginFailCount`).
    *   **Linh hoạt:** Việc sử dụng `Specification` cho phép lọc dữ liệu một cách linh hoạt và hiệu quả.
    *   **API nhất quán:** Sử dụng lớp `ApiResponse` để trả về một định dạng JSON đồng nhất cho tất cả các API.
    *   **Xử lý file:** Có module riêng (`FileStorageService`) để xử lý việc tải lên và lưu trữ file, giúp tách biệt logic và dễ dàng thay đổi cơ chế lưu trữ sau này.

**Chi tiết các giải pháp từ nghiệp vụ đến kỹ thuật, công nghệ áp dụng**

1.  **Công nghệ sử dụng:**
    *   **Framework:** Spring Boot 3
    *   **ORM:** Spring Data JPA / Hibernate
    *   **Bảo mật:** Spring Security, JSON Web Token (JWT)
    *   **API Documentation:** (Dự kiến) Swagger/OpenAPI (cần kiểm tra trong `pom.xml` hoặc `build.gradle` để xác nhận)
    *   **Database:** (Dự kiến) MySQL/PostgreSQL (dựa trên cấu hình trong `application.properties`)

2.  **Luồng hoạt động chính:**
    *   **Đăng ký/Đăng nhập:** `AuthenticationController` -> `AuthenticationService` -> `UserRepository` -> JWT được tạo và trả về.
    *   **Tạo khóa học (Instructor):** Client gửi request (có JWT của Instructor) đến `CourseController` -> `CourseService` kiểm tra quyền, xử lý logic, lưu thông tin vào `CourseRepository`.
    *   **Xem danh sách khóa học (Student):** Client gửi request đến `CourseController` -> `CourseService` lấy dữ liệu từ `CourseRepository` (chỉ các khóa học `active`) -> `CourseMapper` chuyển đổi `Entity` thành `DTO` -> trả về cho client.

### Kết quả thực hiện

**Kết quả chương trình**

Hệ thống backend đã hoàn thành và cung cấp một bộ RESTful API đầy đủ để quản lý một nền tảng khóa học trực tuyến. Các chức năng chính đã được triển khai và hoạt động, bao gồm:

*   Quản lý người dùng và phân quyền.
*   Quản lý toàn diện khóa học, bài học và tài liệu.
*   Ghi danh và theo dõi tiến độ của học viên.
*   Hệ thống đánh giá, nhận xét.
*   API thống kê cho quản trị viên.

**Phân tích các kết quả thu được dựa theo giải pháp đề xuất**

*   Hệ thống đáp ứng tốt các yêu cầu chức năng đã đề ra.
*   Cấu trúc phân lớp giúp cho việc phát triển và bảo trì trở nên dễ dàng hơn.
*   Việc sử dụng Spring Security đã đảm bảo tính bảo mật và phân quyền chặt chẽ cho hệ thống.
*   API được thiết kế theo chuẩn REST, dễ dàng cho việc tích hợp với các ứng dụng frontend.

### Kết luận

**Hiệu quả đạt được và các điểm tồn tại**

*   **Hiệu quả:**
    *   Xây dựng thành công một backend hoàn chỉnh, sẵn sàng để tích hợp với frontend.
    *   Hệ thống có khả năng mở rộng trong tương lai.
    *   Mã nguồn được tổ chức tốt, dễ đọc và dễ tiếp cận.

*   **Điểm tồn tại:**
    *   Thiếu vắng hệ thống test tự động.
    *   Cần cấu hình Swagger UI để việc kiểm thử và tài liệu hóa API được thuận tiện hơn.
    *   Có thể tối ưu hóa một số câu truy vấn CSDL để cải thiện hiệu năng khi dữ liệu lớn.

**Nêu rõ đóng góp của cá nhân**

(Phần này bạn sẽ tự điền dựa trên đóng góp thực tế của mình vào dự án)
Ví dụ:
*   Phân tích và thiết kế cơ sở dữ liệu.
*   Xây dựng module quản lý khóa học và bài học.
*   Triển khai hệ thống xác thực và phân quyền bằng JWT.
*   ... 
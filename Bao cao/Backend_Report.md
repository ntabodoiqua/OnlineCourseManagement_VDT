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

---

### Phân tích chuyên sâu và Đề xuất cải tiến

Phần này sẽ đi sâu vào các quyết định thiết kế, các luồng nghiệp vụ phức tạp và đưa ra các đề xuất cải tiến cho tương lai.

#### 1. Lý do đằng sau Thiết kế Cơ sở dữ liệu

Thiết kế CSDL được xây dựng theo hướng tiếp cận chuẩn hóa để đảm bảo tính toàn vẹn dữ liệu, giảm thiểu sự trùng lặp và dễ dàng mở rộng.

*   **`User`, `Role`, `Permission`**: Mô hình này triển khai **Kiểm soát truy cập dựa trên vai trò (RBAC)**.
    *   Một `User` có thể có nhiều `Role` (`@ManyToMany`).
    *   Một `Role` có thể có nhiều `Permission` (quan hệ này chưa được thể hiện rõ trong entity `Role` nhưng có thể được định nghĩa trong logic của Spring Security).
    *   **Lý do**: Thiết kế này rất linh hoạt. Thay vì gán quyền trực tiếp cho từng người dùng, ta gán quyền cho vai trò. Khi cần thay đổi quyền, ta chỉ cần sửa ở `Role` thay vì phải sửa cho hàng loạt `User`.

*   **`Course`, `Category`, `User (Instructor)`**:
    *   Một `Course` thuộc về một `Category` (`@ManyToOne`) và được tạo bởi một `User` có vai trò `Instructor` (`@ManyToOne`).
    *   **Lý do**: Giúp phân loại, tổ chức khóa học một cách khoa học. Việc liên kết trực tiếp đến `User` giúp xác định rõ người chịu trách nhiệm về nội dung khóa học.

*   **`Course`, `Lesson`, `CourseLesson`**:
    *   Mối quan hệ giữa `Course` và `Lesson` là `ManyToMany`, được thể hiện qua bảng trung gian `CourseLesson`.
    *   **Lý do**: Thiết kế này cho phép một `Lesson` có thể được tái sử dụng trong nhiều `Course` khác nhau, tiết kiệm thời gian và công sức cho giảng viên khi tạo nội dung có liên quan.

*   **`Enrollment`**: Đây là thực thể trung tâm, kết nối `User` (học viên) và `Course`.
    *   Nó không chỉ ghi lại việc ai đã đăng ký khóa học nào, mà còn lưu trữ các thông tin quan trọng như ngày đăng ký (`enrollmentDate`), trạng thái (`approvalStatus`), tiến độ (`progress`), và ngày hoàn thành (`completionDate`).
    *   **Lý do**: Tách riêng logic ghi danh ra một bảng riêng giúp cho bảng `User` và `Course` không bị "phình to" với các thông tin không cốt lõi. Nó cũng giúp cho việc truy vấn các thông tin liên quan đến việc học trở nên hiệu quả hơn.

*   **Hệ thống Quiz (`Quiz`, `QuizQuestion`, `QuizAnswer`, `QuizAttempt`)**:
    *   Đây là một module được thiết kế chi tiết, cho thấy sự đầu tư vào tính năng tương tác.
    *   **Lý do**: Cấu trúc này cho phép tạo ra các bài kiểm tra trắc nghiệm đa dạng, lưu lại lịch sử làm bài của học viên (`QuizAttempt`), và cung cấp khả năng chấm điểm tự động.

#### 2. Phân tích các Luồng nghiệp vụ quan trọng

**a. Luồng Đăng ký và Xác thực (Authentication & Authorization)**

1.  **Đăng ký**: Người dùng cung cấp thông tin. Mật khẩu được mã hóa bằng `BCryptPasswordEncoder` trước khi lưu vào CSDL để đảm bảo an toàn.
2.  **Đăng nhập**:
    *   Người dùng gửi `username` và `password`.
    *   `AuthenticationService` sử dụng `AuthenticationManager` của Spring Security để xác thực.
    *   Nếu thành công, `JwtTokenProvider` sẽ tạo ra một JWT. Token này chứa thông tin về `username` và `roles` của người dùng.
    *   Token được trả về cho client.
3.  **Yêu cầu truy cập tài nguyên**:
    *   Client gửi JWT trong header `Authorization`.
    *   `JwtAuthenticationFilter` (một bộ lọc custom) sẽ chặn request, xác thực token.
    *   Nếu token hợp lệ, bộ lọc sẽ tạo một đối tượng `Authentication` và lưu vào `SecurityContextHolder`.
    *   Spring Security dựa vào thông tin trong `SecurityContextHolder` và các annotation `@PreAuthorize` để quyết định request có được phép thực thi hay không.
4.  **Đăng xuất**: JWT là stateless, vì vậy không thể "xóa" nó ở server. Giải pháp của dự án là lưu token vào bảng `InvalidatedToken` khi người dùng đăng xuất. `JwtAuthenticationFilter` sẽ kiểm tra xem token có nằm trong bảng này không trước khi xác thực.

**b. Luồng Tạo và Quản lý Khóa học (Instructor)**

1.  **Tạo khóa học**:
    *   Instructor gửi request `POST /courses` kèm dữ liệu (JSON) và ảnh thumbnail (MultipartFile).
    *   `CourseController` nhận request. `@PreAuthorize("hasRole('INSTRUCTOR')")` đảm bảo chỉ Instructor mới có thể gọi API này.
    *   `CourseService` được gọi. Nó kiểm tra xem `Category` có tồn tại không, `title` có trùng không.
    *   Lấy thông tin Instructor từ `SecurityContextHolder` để gán vào khóa học.
    *   Ảnh thumbnail được xử lý bởi `FileStorageService` và lưu vào hệ thống file, đường dẫn được lưu vào CSDL.
    *   Khóa học được lưu vào CSDL.
2.  **Cập nhật/Xóa**:
    *   Tương tự luồng tạo, nhưng trước khi thực hiện, `CourseService` có một hàm `checkCoursePermission` quan trọng. Hàm này đảm bảo rằng chỉ có `ADMIN` hoặc chính `Instructor` đã tạo ra khóa học đó mới có quyền sửa/xóa. Đây là một lớp bảo mật nghiệp vụ quan trọng.

#### 3. Các Điểm đã làm thêm/cải tiến so với yêu cầu ban đầu

Dựa trên yêu cầu ban đầu, dự án không chỉ hoàn thành mà còn phát triển thêm nhiều tính năng giá trị:

*   **Hệ thống Quiz hoàn chỉnh**: Yêu cầu ban đầu chỉ nói về "danh sách bài học". Dự án đã xây dựng một hệ thống kiểm tra kiến thức đầy đủ, đây là một điểm cộng lớn cho tính tương tác của nền tảng.
*   **Quản lý tài liệu chi tiết**: Triển khai `CourseDocument` và `LessonDocument`, cho phép đính kèm nhiều loại tài liệu khác nhau cho từng khóa học và bài học, thay vì chỉ là một mô tả đơn thuần.
*   **Cơ chế phê duyệt khóa học**: Trường `requiresApproval` trong `Course` và `approvalStatus` trong `Enrollment` cho thấy hệ thống có thể hỗ trợ cả mô hình khóa học trả phí/cần xét duyệt, linh hoạt hơn yêu cầu ban đầu.
*   **Bảo mật nâng cao**: Ngoài JWT và phân quyền, dự án còn có cơ chế chống brute-force đơn giản (`loginFailCount`) và xử lý đăng xuất cho JWT (`InvalidatedToken`), những điều này không được đề cập trong yêu cầu.
*   **API Thống kê**: Xây dựng các endpoint thống kê riêng (`StatisticController`, `getPopularCourses`) là một sự chủ động, đáp ứng nhu cầu phân tích của quản trị viên mà yêu cầu ban đầu chỉ nêu chung chung.

#### 4. Đề xuất Giải pháp Cải tiến trong Tương lai

1.  **Tích hợp Swagger/OpenAPI**:
    *   **Vấn đề**: Hiện tại, việc khám phá và test API phải thực hiện thủ công.
    *   **Giải pháp**: Thêm dependency `springdoc-openapi-ui`. Spring Boot sẽ tự động tạo ra một giao diện Swagger UI tại `/swagger-ui.html`, cung cấp tài liệu API trực quan và một môi trường để test API trực tiếp.

2.  **Viết Test tự động**:
    *   **Vấn đề**: Thiếu test làm tăng rủi ro khi thay đổi code.
    *   **Giải pháp**:
        *   **Unit Test**: Sử dụng **JUnit 5** và **Mockito** để test logic trong các lớp `Service` một cách độc lập (mock các `Repository`).
        *   **Integration Test**: Sử dụng **Testcontainers** để khởi tạo một database thật (trong Docker container) khi chạy test. Điều này cho phép kiểm tra sự tương tác từ `Controller` xuống đến CSDL một cách toàn diện.

3.  **Tối ưu hóa Hiệu năng (Performance Tuning)**:
    *   **Vấn đề**: Khi dữ liệu lớn, các truy vấn CSDL, đặc biệt là các join phức tạp, có thể trở nên chậm chạp.
    *   **Giải pháp**:
        *   **Caching**: Tích hợp **Redis** hoặc **Caffeine** để cache các dữ liệu ít thay đổi nhưng được truy cập thường xuyên (VD: danh sách Categories, thông tin các khóa học phổ biến).
        *   **Phân trang (Pagination)**: Đã được áp dụng tốt, cần tiếp tục duy trì.
        *   **Tối ưu truy vấn**: Sử dụng **JPA Entity Graphs** để giải quyết vấn đề N+1 select khi truy vấn các thực thể có quan hệ `@...ToMany`.

4.  **Chuyển đổi sang Kiến trúc Clean/Hexagonal**:
    *   **Vấn đề**: Hiện tại logic nghiệp vụ (`Service`) vẫn còn phụ thuộc trực tiếp vào Spring Data (`Repository`).
    *   **Giải pháp**:
        *   Tạo một tầng `domain` hoặc `usecase` ở giữa `Controller` và `Service`.
        *   Định nghĩa các `interfaces` cho repository trong tầng `domain`.
        *   Lớp `Service` (giờ đóng vai trò là tầng `infrastructure`) sẽ implement các interface này.
        *   Điều này giúp logic nghiệp vụ cốt lõi hoàn toàn độc lập với framework, dễ dàng thay đổi CSDL hoặc các thành phần khác.

5.  **Xử lý bất đồng bộ (Asynchronous Processing)**:
    *   **Vấn đề**: Các tác vụ tốn thời gian như gửi email thông báo, xử lý video bài học... nếu được xử lý đồng bộ sẽ làm tăng thời gian phản hồi của API.
    *   **Giải pháp**: Sử dụng **RabbitMQ** hoặc **Kafka**. Khi có một sự kiện (VD: học viên đăng ký), thay vì xử lý ngay, hệ thống sẽ đẩy một message vào queue. Một service khác (worker) sẽ lắng nghe và xử lý tác vụ đó một cách bất đồng bộ. 
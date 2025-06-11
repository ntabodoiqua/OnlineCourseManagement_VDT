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
    *   **API Documentation:** Swagger/OpenAPI (thông qua `springdoc-openapi`) đã được tích hợp. Giao diện Swagger UI có sẵn để khám phá, tài liệu hóa và kiểm thử API một cách trực quan.
    *   **Database:** MySQL (dựa trên `pom.xml`)

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
    *   Thiếu vắng hệ thống test tự động (Unit Test, Integration Test).
    *   Cần làm giàu tài liệu API trên Swagger UI bằng các annotations chi tiết (`@Operation`, `@ApiResponse`...).
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

1.  **Hoàn thiện và Tận dụng Swagger/OpenAPI**:
    *   **Tình trạng**: Hệ thống đã tích hợp `springdoc-openapi-ui`, giúp tự động tạo tài liệu API và giao diện tương tác tại `/swagger-ui.html`.
    *   **Giải pháp cải tiến**:
        *   **Làm giàu tài liệu**: Bổ sung các annotation của Swagger (`@Operation`, `@Parameter`, `@ApiResponse`, `@SecurityRequirement`...) vào các lớp `Controller` để mô tả chi tiết hơn về chức năng của từng API, ý nghĩa tham số, các định dạng response và các lỗi có thể xảy ra.
        *   **Cấu hình bảo mật**: Cấu hình `SecurityScheme` trong Springdoc để người dùng có thể thực hiện xác thực (gửi JWT) trực tiếp trên giao diện Swagger, giúp việc kiểm thử các API yêu cầu quyền truy cập trở nên dễ dàng.
        *   **Tùy chỉnh giao diện**: Tùy chỉnh các thông tin chung của API trên Swagger UI (tiêu đề, mô tả, thông tin liên hệ) để tăng tính chuyên nghiệp.

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

---

### Phân tích Chi tiết các Luồng nghiệp vụ và Thiết kế API

Phần này sẽ đi sâu vào các luồng nghiệp vụ cho từng vai trò người dùng, giải thích lý do đằng sau thiết kế API, đồng thời phân tích điểm mạnh, điểm yếu và đề xuất các cải tiến.

#### 1. Người dùng không xác thực (Guest)

Luồng nghiệp vụ của người dùng không xác thực tập trung vào việc khám phá và tìm hiểu các khóa học có sẵn trên nền tảng.

**a. Luồng: Xem danh sách khóa học và thông tin giảng viên**

*   **Luồng nghiệp vụ**:
    1.  Người dùng truy cập trang web/ứng dụng.
    2.  Hệ thống hiển thị danh sách các khóa học công khai, có thể kèm theo các khóa học nổi bật (`popular`) hoặc mới nhất.
    3.  Người dùng có thể sử dụng bộ lọc (theo danh mục, giá tiền,...) để tìm kiếm khóa học phù hợp.
    4.  Người dùng có thể xem thông tin chi tiết của một khóa học cụ thể.
    5.  Người dùng cũng có thể xem danh sách các giảng viên và thông tin công khai của họ.

*   **Thiết kế API và Lý do**:
    *   `GET /courses/public`: Cung cấp danh sách khóa học đã được phân trang và cho phép lọc. Tên gọi `public` làm rõ đây là API không cần xác thực.
    *   `GET /courses/public/{courseId}`: Lấy thông tin chi tiết của một khóa học. Sử dụng path variable (`{courseId}`) là một thiết kế RESTful chuẩn để định danh một tài nguyên cụ thể.
    *   `GET /instructors/public`: Lấy danh sách giảng viên.
    *   `GET /instructors/public/{instructorId}`: Xem hồ sơ công khai của giảng viên.
    *   **Lý do chung**: Các API này sử dụng phương thức `GET` vì chúng chỉ dùng để truy xuất dữ liệu, không làm thay đổi trạng thái hệ thống. Việc tách riêng các endpoint `public` giúp quản lý bảo mật dễ dàng hơn ở tầng gateway hoặc Spring Security, cho phép các request này đi qua mà không cần kiểm tra JWT.

*   **Điểm mạnh**:
    *   API rõ ràng, dễ hiểu và tuân thủ các nguyên tắc REST.
    *   Cung cấp đầy đủ các bộ lọc cần thiết để người dùng có thể tìm kiếm hiệu quả.

*   **Điểm yếu và Hướng cải tiến**:
    *   **Điểm yếu**: Hiện tại, API `GET /courses` và `GET /courses/public` có vẻ gọi cùng một logic. Nếu không có sự khác biệt về dữ liệu trả về cho người dùng đã đăng nhập và người dùng công khai, điều này có thể gây nhầm lẫn.
    *   **Cải tiến**: Nếu có sự khác biệt, cần làm rõ trong logic của service. Ví dụ, `GET /courses` (cho người dùng đã đăng nhập) có thể trả về thêm thông tin về việc người dùng đã đăng ký khóa học này hay chưa. Nếu không, nên xem xét loại bỏ một trong hai để tránh trùng lặp.

#### 2. Học viên (Student)

Sau khi đăng ký và đăng nhập, học viên có các luồng nghiệp vụ liên quan đến việc tham gia và học tập.

**a. Luồng: Ghi danh vào một khóa học**

*   **Luồng nghiệp vụ**:
    1.  Học viên chọn một khóa học và nhấn nút "Ghi danh".
    2.  Hệ thống kiểm tra các điều kiện: học viên đã đăng nhập chưa, đã ghi danh vào khóa học này trước đó chưa, khóa học có yêu cầu phê duyệt không.
    3.  Nếu hợp lệ, hệ thống tạo một bản ghi `Enrollment` mới, liên kết giữa học viên và khóa học.
    4.  Hệ thống trả về thông báo ghi danh thành công.

*   **Thiết kế API và Lý do**:
    *   `POST /enrollments`: Đây là một thiết kế RESTful tốt. Thay vì coi việc ghi danh là một hành động trên `Course` (VD: `POST /courses/{id}/enroll`), hệ thống xem `Enrollment` (sự ghi danh) là một tài nguyên riêng biệt. Điều này giúp cho việc quản lý ghi danh (xem, hủy) trở nên rõ ràng hơn.

*   **Điểm mạnh**:
    *   Thiết kế API tài nguyên hóa (`resource-oriented`) giúp hệ thống dễ mở rộng.
    *   Logic kiểm tra điều kiện trong service đảm bảo tính toàn vẹn dữ liệu.

*   **Điểm yếu và Hướng cải tiến**:
    *   **Điểm yếu**: API có thể yêu cầu client phải gửi toàn bộ đối tượng `Enrollment`. Điều này không an toàn và không hiệu quả.
    *   **Cải tiến**: Nên sử dụng một `EnrollmentRequestDTO` chỉ chứa `courseId`. Thông tin `userId` nên được lấy từ `SecurityContextHolder` trong service để đảm bảo học viên chỉ có thể tự ghi danh cho chính mình.

**b. Luồng: Theo dõi tiến độ và học bài**

*   **Luồng nghiệp vụ**:
    1.  Học viên vào trang "Khóa học của tôi".
    2.  Hệ thống hiển thị danh sách các khóa học đã ghi danh.
    3.  Khi vào một khóa học, hệ thống hiển thị danh sách các bài học và đánh dấu các bài đã hoàn thành.
    4.  Khi học viên hoàn thành một bài học (VD: xem hết video, làm quiz), họ nhấn "Đánh dấu hoàn thành".
    5.  Hệ thống cập nhật bản ghi `Progress`, liên kết với `Enrollment` và `Lesson`.

*   **Thiết kế API và Lý do**:
    *   `GET /courses/my`: Lấy danh sách khóa học của riêng người dùng đang đăng nhập. Tên gọi `my` rất trực quan.
    *   `POST /progress`: Tạo hoặc cập nhật một bản ghi `Progress`. Client sẽ gửi `lessonId` và `enrollmentId`. Hệ thống sẽ đánh dấu bài học này là hoàn thành.

*   **Điểm mạnh**:
    *   Việc tách `Progress` ra một tài nguyên riêng giúp theo dõi chi tiết và linh hoạt.
    *   Luồng logic rõ ràng, dễ dàng cho client tương tác.

*   **Điểm yếu và Hướng cải tiến**:
    *   **Cải tiến**: Cần có cơ chế xác thực mạnh mẽ trong service của `POST /progress` để đảm bảo một học viên không thể cập nhật tiến độ cho một `enrollment` không thuộc về mình.

#### 3. Giảng viên (Instructor)

Giảng viên chịu trách nhiệm tạo và quản lý nội dung khóa học.

**a. Luồng: Tạo và quản lý khóa học**

*   **Luồng nghiệp vụ**:
    1.  Giảng viên truy cập vào trang quản lý khóa học.
    2.  Nhấn "Tạo khóa học mới", điền các thông tin (tên, mô tả, danh mục,...) và tải lên ảnh thumbnail.
    3.  Hệ thống nhận thông tin, xác thực quyền (`INSTRUCTOR`), kiểm tra tính hợp lệ của dữ liệu (VD: tên khóa học không trùng).
    4.  Lưu thông tin khóa học và lưu file ảnh vào hệ thống lưu trữ.
    5.  Tương tự với các hành động cập nhật, xóa khóa học. Hệ thống phải kiểm tra quyền sở hữu: chỉ giảng viên tạo ra khóa học (hoặc Admin) mới có quyền sửa/xóa.

*   **Thiết kế API và Lý do**:
    *   `POST /courses`, `PUT /courses/{courseId}`, `DELETE /courses/{courseId}`: Sử dụng các phương thức HTTP chuẩn (`POST`, `PUT`, `DELETE`) cho các hoạt động CRUD, đây là một thực hành tốt nhất của REST.
    *   Sử dụng `@PreAuthorize("hasRole('INSTRUCTOR')")` ở tầng controller là một cách hiệu quả để phân quyền, chặn ngay các truy cập không hợp lệ.
    *   API nhận `multipart/form-data` cho phép gửi cả dữ liệu JSON và file trong một request, rất tiện lợi.

*   **Điểm mạnh**:
    *   Phân quyền rõ ràng và bảo mật ở cấp độ API.
    *   Kiểm tra quyền sở hữu trong service là một lớp bảo mật nghiệp vụ quan trọng, ngăn chặn giảng viên này sửa khóa học của giảng viên khác.
    *   Xử lý file và dữ liệu trong cùng một giao dịch giúp đảm bảo tính nhất quán.

*   **Điểm yếu và Hướng cải tiến**:
    *   **Điểm yếu**: Endpoint `PATCH /{courseId}/toggle-status` để bật/tắt khóa học hiện tại không có annotation `@PreAuthorize`. Đây là một lỗ hổng bảo mật tiềm tàng, vì bất kỳ người dùng xác thực nào cũng có thể gọi nó.
    *   **Cải tiến**: Bổ sung ngay `@PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")` cho endpoint này. Đồng thời, logic trong service cũng cần kiểm tra quyền sở hữu tương tự như khi xóa/sửa khóa học.

#### 4. Quản trị viên (Admin)

Admin có quyền cao nhất, quản lý toàn bộ hệ thống.

**a. Luồng: Quản lý người dùng**

*   **Luồng nghiệp vụ**:
    1.  Admin vào trang quản lý người dùng.
    2.  Hệ thống hiển thị danh sách người dùng với bộ lọc và phân trang.
    3.  Admin có thể thực hiện các hành động: xem chi tiết, cập nhật thông tin, thay đổi mật khẩu, vô hiệu hóa/kích hoạt tài khoản, hoặc xóa người dùng.
    4.  Hệ thống có logic để ngăn Admin tự xóa hoặc vô hiệu hóa chính tài khoản của mình.

*   **Thiết kế API và Lý do**:
    *   `GET, PUT, DELETE /admin/manage-users/{userId}`: Toàn bộ các API quản lý người dùng được nhóm dưới một đường dẫn chung `/admin/manage-users`, giúp việc áp dụng quy tắc bảo mật chung cho toàn bộ nhóm chức năng này trở nên đơn giản.
    *   Việc Admin có thể thay đổi mật khẩu của người dùng khác (`/{userId}/change-password`) là một chức năng cần thiết cho việc hỗ trợ người dùng khi họ quên mật khẩu và không thể tự reset.

*   **Điểm mạnh**:
    *   Cung cấp bộ công cụ quản lý người dùng toàn diện cho Admin.
    *   Có các biện pháp bảo vệ quan trọng (không cho phép tự khóa tài khoản).
    *   Cấu trúc API được tổ chức tốt theo tài nguyên.

*   **Điểm yếu và Hướng cải tiến**:
    *   **Cải tiến**: Nên có một hệ thống ghi log (Audit Log) chi tiết. Mỗi khi Admin thực hiện một hành động quan trọng (thay đổi mật khẩu, xóa người dùng), hệ thống nên tự động ghi lại hành động đó (ai làm, làm gì, lúc nào) để phục vụ cho việc kiểm tra và truy vết sau này.

**b. Luồng: Quản lý danh mục và bảo trì hệ thống**

*   **Luồng nghiệp vụ**:
    1.  Admin vào trang quản lý danh mục, có thể thêm, sửa, xóa các danh mục khóa học.
    2.  Admin có thể cần thực hiện các tác vụ bảo trì, ví dụ như đồng bộ lại số bài học của tất cả các khóa học.

*   **Thiết kế API và Lý do**:
    *   `POST, PUT, DELETE /categories`: Các API CRUD cho danh mục, được bảo vệ bằng quyền Admin.
    *   `POST /courses/admin/sync-all-total-lessons`: Đây là một API chuyên dụng cho tác vụ bảo trì. Đặt nó trong `CourseController` nhưng với tiền tố `/admin` và bảo vệ bằng `@PreAuthorize("hasRole('ADMIN')")` là một giải pháp hợp lý, giữ cho logic liên quan đến course nằm trong controller tương ứng.

*   **Điểm mạnh**:
    *   Tách biệt rõ ràng các chức năng chỉ dành cho Admin.
    *   API cho các tác vụ bảo trì được thiết kế để không ảnh hưởng đến người dùng thông thường.

*   **Điểm yếu và Hướng cải tiến**:
    *   **Cải tiến**: Các tác vụ tốn nhiều thời gian như `sync-all-total-lessons` có thể làm block luồng xử lý chính nếu số lượng khóa học lớn. Nên xem xét chuyển chúng thành các tác vụ bất đồng bộ (asynchronous) bằng cách sử dụng `@Async` của Spring hoặc một hàng đợi tin nhắn (Message Queue) như RabbitMQ.

### So sánh với các giải pháp hiện có và Đánh giá

**Điểm mới và khác biệt so với các giải pháp tiêu chuẩn:**

*   **Cơ chế xử lý JWT khi đăng xuất (`InvalidatedToken`)**: Nhiều hệ thống đơn giản chỉ dựa vào thời gian hết hạn của JWT. Việc triển khai một blacklist cho các token đã đăng xuất là một bước tiến về bảo mật, giúp vô hiệu hóa ngay lập-tức các token có thể đã bị lộ.
*   **API bảo trì chuyên dụng (`sync-total-lessons`)**: Việc cung cấp các API để bảo trì và đồng bộ dữ liệu một cách chủ động cho thấy sự trưởng thành trong thiết kế, giải quyết các vấn đề về tính nhất quán dữ liệu có thể phát sinh trong quá trình hoạt động.
*   **Kiểm tra quyền sở hữu ở tầng service**: Ngoài việc phân quyền bằng role ở controller, hệ thống còn cẩn thận kiểm tra "chủ sở hữu" của tài nguyên (VD: giảng viên chỉ được sửa khóa học của mình). Đây là một lớp logic nghiệp vụ quan trọng mà không phải hệ thống nào cũng triển khai kỹ lưỡng.

**Những điểm chưa ổn hoặc cần cân nhắc:**

*   **Thiếu Audit Log**: Như đã đề cập, việc thiếu một hệ thống ghi vết các hành động quan trọng của Admin là một thiếu sót lớn về mặt quản trị và an ninh.
*   **Xử lý tác vụ dài hơi một cách đồng bộ**: Các tác vụ có khả năng chạy lâu (đồng bộ dữ liệu, xử lý file lớn) đang được xử lý đồng bộ, có thể ảnh hưởng đến hiệu năng và trải nghiệm người dùng. Đây là một điểm yếu phổ biến trong nhiều ứng dụng nhưng cần được khắc phục khi hệ thống phát triển lớn hơn.
*   **Thiết kế DTO**: Ở một số nơi, có thể API vẫn còn chấp nhận các đối tượng Entity trực tiếp thay vì các DTO chuyên dụng (Request DTOs), điều này có thể dẫn đến các vấn đề về bảo mật (mass assignment vulnerability) và làm lộ cấu trúc bên trong của CSDL. Cần rà soát và đảm bảo tất cả các endpoint đều sử dụng DTO cho input.

Nhìn chung, giải pháp hiện tại đã rất mạnh mẽ, an toàn và có cấu trúc tốt. Các điểm yếu chủ yếu là những vấn đề liên quan đến việc tối ưu hóa hiệu năng và hoàn thiện các tính năng ở quy mô lớn, vốn có thể được cải tiến trong các phiên bản tiếp theo. 
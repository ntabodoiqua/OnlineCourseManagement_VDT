# Hướng Dẫn Chuyển Đổi Hệ Thống từ Monolithic sang Microservices

## 📋 Tổng Quan

Tài liệu này hướng dẫn chi tiết việc chuyển đổi hệ thống **Online Course Management System** từ kiến trúc monolithic hiện tại sang kiến trúc microservices.

### Hệ Thống Hiện Tại
- **Kiến trúc**: Spring Boot Monolithic Application
- **Database**: MySQL (Single Database)
- **Authentication**: JWT với Spring Security
- **File Storage**: Local File System
- **API**: RESTful APIs với Swagger Documentation

### Mục Tiêu Chuyển Đổi
- Tách biệt các domain thành các microservice độc lập
- Cải thiện khả năng mở rộng (scalability)
- Tăng tính linh hoạt trong development và deployment
- Hỗ trợ CI/CD và containerization

---

## 🎯 Phân Tích Domain và Thiết Kế Microservices

### 1. Xác Định Bounded Context

Dựa trên phân tích code hiện tại, hệ thống có thể được chia thành các microservice sau:

#### 🔐 **User Service (Identity Service)**
- **Chức năng**: Quản lý người dùng, xác thực, phân quyền
- **Entities**: User, Role, Permission, InvalidatedToken
- **Endpoints**: /auth/*, /users/*, /roles/*, /permissions/*
- **Database**: user_service_db

#### 📚 **Course Service**
- **Chức năng**: Quản lý khóa học, bài học, danh mục
- **Entities**: Course, Lesson, Category, CourseLesson
- **Endpoints**: /courses/*, /lessons/*, /categories/*
- **Database**: course_service_db

#### 📝 **Quiz Service**
- **Chức năng**: Quản lý bài thi, câu hỏi, kết quả
- **Entities**: Quiz, QuizQuestion, QuizAnswer, QuizAttempt, QuizAttemptAnswer
- **Endpoints**: /quizzes/*, /quiz-attempts/*
- **Database**: quiz_service_db

#### 🎓 **Enrollment Service**
- **Chức năng**: Quản lý đăng ký khóa học, tiến độ học tập
- **Entities**: Enrollment, Progress
- **Endpoints**: /enrollments/*, /progress/*
- **Database**: enrollment_service_db

#### 📄 **Document Service**
- **Chức năng**: Quản lý tài liệu, file đính kèm
- **Entities**: CourseDocument, LessonDocument, UploadedFile, DocumentView
- **Endpoints**: /documents/*, /files/*
- **Database**: document_service_db

#### ⭐ **Review Service**
- **Chức năng**: Quản lý đánh giá khóa học
- **Entities**: CourseReview
- **Endpoints**: /reviews/*
- **Database**: review_service_db

#### 📊 **Analytics Service**
- **Chức năng**: Thống kê, báo cáo
- **Endpoints**: /statistics/*, /analytics/*
- **Database**: analytics_service_db

#### 🌐 **API Gateway**
- **Chức năng**: Routing, Load Balancing, Rate Limiting
- **Technology**: Spring Cloud Gateway hoặc Kong

---

## 🛣️ Roadmap Chuyển Đổi

### Phase 1: Chuẩn Bị Infrastructure (2-3 tuần)

#### 1.1 Setup Containerization
```bash
# Tạo Dockerfile cho ứng dụng hiện tại
FROM openjdk:21-jdk-slim
VOLUME /tmp
COPY target/online-course-management-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

#### 1.2 Setup Docker Compose cho Development
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: identity
    ports:
      - "3306:3306"
    
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/identity
```

#### 1.3 Setup Service Discovery
- **Tool**: Eureka Server hoặc Consul
- **Purpose**: Service registration và discovery

#### 1.4 Setup Configuration Management
- **Tool**: Spring Cloud Config Server
- **Purpose**: Centralized configuration management

### Phase 2: Tách Service đầu tiên - User Service (3-4 tuần)

#### 2.1 Tạo User Service Module

```
user-service/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/ntabodoiqua/userservice/
│       │       ├── UserServiceApplication.java
│       │       ├── controller/
│       │       ├── service/
│       │       ├── repository/
│       │       ├── entity/
│       │       └── dto/
│       └── resources/
│           └── application.yml
├── Dockerfile
└── pom.xml
```

#### 2.2 Database Migration
```sql
-- Tạo database cho User Service
CREATE DATABASE user_service_db;

-- Migrate tables: User, Role, Permission, InvalidatedToken
-- Sử dụng Flyway hoặc Liquibase cho version control
```

#### 2.3 API Gateway Configuration
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/users/**, /api/auth/**, /api/roles/**
```

#### 2.4 Inter-Service Communication
- **Đồng bộ**: REST API với Feign Client
- **Bất đồng bộ**: Apache Kafka cho events

### Phase 3: Tách Course Service (3-4 tuần)

#### 3.1 Data Migration Strategy
```sql
-- Xử lý foreign key references
-- Course.instructor_id -> User Service lookup
-- Sử dụng event-driven approach cho data consistency
```

#### 3.2 Event-Driven Architecture
```java
// Course Service publishes events
@EventListener
public void handleCourseCreated(CourseCreatedEvent event) {
    // Notify other services
    kafkaTemplate.send("course-events", event);
}
```

#### 3.3 Shared Data Handling
- **User information caching** trong Course Service
- **Database replication** cho read-only data

### Phase 4: Tách các Service còn lại (6-8 tuần)

#### 4.1 Quiz Service
- Migrate quiz-related entities
- Implement quiz attempt tracking
- Handle complex business logic

#### 4.2 Enrollment Service
- Handle course enrollment workflow
- Implement progress tracking
- Manage enrollment states

#### 4.3 Document Service
- File storage abstraction
- Support multiple storage backends (S3, MinIO)
- CDN integration

#### 4.4 Review Service
- Course review management
- Rating calculation
- Review moderation

#### 4.5 Analytics Service
- Data aggregation from other services
- Real-time analytics
- Reporting APIs

---

## 🔧 Triển Khai Chi Tiết

### 1. Service Template

#### 1.1 Cấu Trúc Project Standard
```
service-name/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ntabodoiqua/servicename/
│   │   │       ├── ServiceNameApplication.java
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       ├── repository/
│   │   │       ├── entity/
│   │   │       ├── dto/
│   │   │       ├── mapper/
│   │   │       ├── exception/
│   │   │       └── event/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

#### 1.2 Base Dependencies (pom.xml)
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>
</dependencies>
```

### 2. Configuration Management

#### 2.1 Service Configuration Template
```yaml
server:
  port: ${SERVICE_PORT:8081}

spring:
  application:
    name: ${SERVICE_NAME:user-service}
  
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:user_service_db}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}
  
  jpa:
    hibernate:
      ddl-auto: ${DDL_AUTO:update}
    show-sql: ${SHOW_SQL:false}
  
  kafka:
    bootstrap-servers: ${KAFKA_SERVERS:localhost:9092}
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: ${SERVICE_NAME:user-service}
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_URL:http://localhost:8761/eureka}
  instance:
    prefer-ip-address: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

### 3. Database Migration Strategy

#### 3.1 Shared Data Strategy
```sql
-- Tạo view cho shared data
CREATE VIEW user_basic_info AS
SELECT id, username, firstName, lastName, email, avatarUrl
FROM user;

-- Replicate cần thiết data sang services khác
-- Sử dụng CDC (Change Data Capture) với Debezium
```

#### 3.2 Distributed Transaction Management
```java
// Sử dụng Saga Pattern
@SagaOrchestrationStart
public void createCourse(CreateCourseCommand command) {
    // Step 1: Validate user exists
    choreographer.choreography()
        .step("validate-user")
        .invokeParticipant(UserService.class)
        .step("create-course")
        .invokeParticipant(CourseService.class)
        .step("notify-enrollment")
        .invokeParticipant(EnrollmentService.class)
        .execute();
}
```

### 4. Service Communication

#### 4.1 Synchronous Communication (Feign Client)
```java
@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/api/users/{userId}")
    UserResponse getUserById(@PathVariable String userId);
    
    @GetMapping("/api/users/{userId}/basic")
    UserBasicInfo getUserBasicInfo(@PathVariable String userId);
}
```

#### 4.2 Asynchronous Communication (Kafka)
```java
// Event Publisher
@Component
public class CourseEventPublisher {
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    public void publishCourseCreated(CourseCreatedEvent event) {
        kafkaTemplate.send("course-events", event);
    }
}

// Event Consumer
@KafkaListener(topics = "course-events")
public void handleCourseCreated(CourseCreatedEvent event) {
    // Update local cache or trigger business logic
    log.info("Course created: {}", event.getCourseId());
}
```

### 5. API Gateway Configuration

#### 5.1 Route Configuration
```yaml
spring:
  cloud:
    gateway:
      routes:
        # User Service Routes
        - id: user-service-auth
          uri: lb://user-service
          predicates:
            - Path=/api/auth/**
          filters:
            - StripPrefix=1
            
        - id: user-service-users
          uri: lb://user-service
          predicates:
            - Path=/api/users/**
          filters:
            - StripPrefix=1
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
        
        # Course Service Routes
        - id: course-service
          uri: lb://course-service
          predicates:
            - Path=/api/courses/**, /api/lessons/**, /api/categories/**
          filters:
            - StripPrefix=1
            
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins: "*"
            allowedMethods: "*"
            allowedHeaders: "*"
```

#### 5.2 Security Filter
```java
@Component
public class JwtAuthenticationFilter implements GatewayFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, 
                            GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Extract and validate JWT token
        String token = extractToken(request);
        if (isValidToken(token)) {
            return chain.filter(exchange);
        }
        
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
}
```

---

## 🏗️ Infrastructure và DevOps

### 1. Containerization Strategy

#### 1.1 Multi-stage Dockerfile
```dockerfile
# Build stage
FROM maven:3.8.4-openjdk-21-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 1.2 Docker Compose for Development
```yaml
version: '3.8'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  eureka-server:
    image: springcloud/eureka:latest
    ports:
      - "8761:8761"

  mysql-user:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: user_service_db
    ports:
      - "3307:3306"

  mysql-course:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: course_service_db
    ports:
      - "3308:3306"

  redis:
    image: redis:alpine
    ports:
      - "6379:6379"

  api-gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    depends_on:
      - eureka-server
    environment:
      EUREKA_URL: http://eureka-server:8761/eureka

  user-service:
    build: ./user-service
    ports:
      - "8081:8081"
    depends_on:
      - mysql-user
      - kafka
      - eureka-server
    environment:
      DB_HOST: mysql-user
      KAFKA_SERVERS: kafka:9092
      EUREKA_URL: http://eureka-server:8761/eureka

  course-service:
    build: ./course-service
    ports:
      - "8082:8082"
    depends_on:
      - mysql-course
      - kafka
      - eureka-server
    environment:
      DB_HOST: mysql-course
      KAFKA_SERVERS: kafka:9092
      EUREKA_URL: http://eureka-server:8761/eureka
```

### 2. Kubernetes Deployment

#### 2.1 Service Deployment Template
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: user-service
  template:
    metadata:
      labels:
        app: user-service
    spec:
      containers:
      - name: user-service
        image: ntabodoiqua/user-service:latest
        ports:
        - containerPort: 8081
        env:
        - name: DB_HOST
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: host
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 10

---
apiVersion: v1
kind: Service
metadata:
  name: user-service
spec:
  selector:
    app: user-service
  ports:
  - port: 8081
    targetPort: 8081
  type: ClusterIP
```

### 3. CI/CD Pipeline

#### 3.1 Jenkins Pipeline
```groovy
pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'your-registry.com'
        KUBE_NAMESPACE = 'microservices'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Docker Build') {
            steps {
                script {
                    def services = ['user-service', 'course-service', 'quiz-service']
                    services.each { service ->
                        sh """
                            cd ${service}
                            docker build -t ${DOCKER_REGISTRY}/${service}:${BUILD_NUMBER} .
                            docker push ${DOCKER_REGISTRY}/${service}:${BUILD_NUMBER}
                        """
                    }
                }
            }
        }
        
        stage('Deploy to K8s') {
            steps {
                script {
                    sh """
                        kubectl set image deployment/user-service \
                        user-service=${DOCKER_REGISTRY}/user-service:${BUILD_NUMBER} \
                        -n ${KUBE_NAMESPACE}
                    """
                }
            }
        }
    }
    
    post {
        always {
            junit 'target/surefire-reports/*.xml'
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'Code Coverage Report'
            ])
        }
    }
}
```

---

## 📊 Monitoring và Observability

### 1. Distributed Tracing

#### 1.1 Sleuth Configuration
```yaml
spring:
  sleuth:
    sampler:
      probability: 1.0
    zipkin:
      base-url: http://zipkin:9411
```

#### 1.2 Custom Tracing
```java
@NewSpan("course-service")
@Component
public class CourseService {
    
    @Autowired
    private Tracer tracer;
    
    public CourseResponse createCourse(CreateCourseRequest request) {
        Span span = tracer.nextSpan()
            .name("create-course")
            .tag("course.title", request.getTitle())
            .start();
        
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            // Business logic
            return courseRepository.save(course);
        } finally {
            span.end();
        }
    }
}
```

### 2. Metrics Collection

#### 2.1 Micrometer Configuration
```java
@Configuration
public class MetricsConfig {
    
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
    
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
            .commonTags("application", "course-management");
    }
}
```

#### 2.2 Custom Metrics
```java
@Component
public class CourseMetrics {
    
    private final Counter courseCreationCounter;
    private final Timer courseProcessingTimer;
    
    public CourseMetrics(MeterRegistry meterRegistry) {
        this.courseCreationCounter = Counter.builder("courses.created")
            .description("Number of courses created")
            .register(meterRegistry);
            
        this.courseProcessingTimer = Timer.builder("courses.processing.time")
            .description("Course processing time")
            .register(meterRegistry);
    }
    
    public void incrementCourseCreation() {
        courseCreationCounter.increment();
    }
    
    public Timer.Sample startTimer() {
        return Timer.start(courseProcessingTimer);
    }
}
```

### 3. Centralized Logging

#### 3.1 Logback Configuration
```xml
<configuration>
    <springProfile name="!local">
        <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
            <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
                <providers>
                    <timestamp/>
                    <logLevel/>
                    <loggerName/>
                    <message/>
                    <mdc/>
                    <pattern>
                        <pattern>
                            {
                                "traceId": "%X{traceId:-}",
                                "spanId": "%X{spanId:-}",
                                "service": "user-service"
                            }
                        </pattern>
                    </pattern>
                </providers>
            </encoder>
        </appender>
    </springProfile>
    
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

---

## 🔒 Security trong Microservices

### 1. JWT Token Management

#### 1.1 Service-to-Service Authentication
```java
@Component
public class ServiceAuthenticationFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String serviceToken = httpRequest.getHeader("X-Service-Token");
        
        if (isValidServiceToken(serviceToken)) {
            // Set service authentication context
            SecurityContextHolder.getContext()
                .setAuthentication(new ServiceAuthentication(serviceToken));
        }
        
        chain.doFilter(request, response);
    }
}
```

#### 1.2 Token Relay Configuration
```yaml
spring:
  cloud:
    gateway:
      default-filters:
        - TokenRelay
      routes:
        - id: course-service
          uri: lb://course-service
          predicates:
            - Path=/api/courses/**
          filters:
            - StripPrefix=1
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
```

### 2. Secret Management

#### 2.1 Kubernetes Secrets
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
type: Opaque
data:
  jwt-secret: $(echo -n 'your-jwt-secret' | base64)
  db-password: $(echo -n 'your-db-password' | base64)
```

#### 2.2 Vault Integration
```java
@Configuration
@EnableVaultRepositories
public class VaultConfig {
    
    @Bean
    public VaultTemplate vaultTemplate() {
        return new VaultTemplate(vaultEndpoint(), clientAuthentication());
    }
    
    @Value("${app.database.password}")
    private String databasePassword; // Auto-injected from Vault
}
```

---

## ⚡ Performance Optimization

### 1. Caching Strategy

#### 1.1 Multi-level Caching
```java
@Service
public class CourseService {
    
    @Cacheable(value = "courses", key = "#courseId")
    public CourseResponse getCourse(String courseId) {
        return courseRepository.findById(courseId)
            .map(courseMapper::toResponse)
            .orElseThrow(() -> new CourseNotFoundException(courseId));
    }
    
    @CacheEvict(value = "courses", key = "#courseId")
    public void updateCourse(String courseId, UpdateCourseRequest request) {
        // Update logic
    }
}
```

#### 1.2 Distributed Cache Configuration
```yaml
spring:
  cache:
    type: redis
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-wait: -1ms
        max-idle: 8
        min-idle: 0
```

### 2. Database Optimization

#### 2.1 Connection Pooling
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 20000
      idle-timeout: 300000
      max-lifetime: 1200000
      leak-detection-threshold: 60000
```

#### 2.2 Read Replicas
```java
@Configuration
public class DatabaseConfig {
    
    @Bean
    @Primary
    public DataSource writeDataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:mysql://master-db:3306/course_service_db")
            .build();
    }
    
    @Bean
    public DataSource readDataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:mysql://read-replica:3306/course_service_db")
            .build();
    }
    
    @Bean
    public DataSource routingDataSource() {
        RoutingDataSource routingDataSource = new RoutingDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("write", writeDataSource());
        dataSourceMap.put("read", readDataSource());
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(writeDataSource());
        return routingDataSource;
    }
}
```

---

## 🚨 Error Handling và Resilience

### 1. Circuit Breaker Pattern

#### 1.1 Resilience4j Configuration
```java
@Component
public class CourseServiceClient {
    
    @CircuitBreaker(name = "course-service", fallbackMethod = "fallbackGetCourse")
    @TimeLimiter(name = "course-service")
    @Retry(name = "course-service")
    public CompletableFuture<CourseResponse> getCourseAsync(String courseId) {
        return CompletableFuture.supplyAsync(() -> 
            restTemplate.getForObject("/courses/" + courseId, CourseResponse.class));
    }
    
    public CompletableFuture<CourseResponse> fallbackGetCourse(String courseId, Exception ex) {
        return CompletableFuture.completedFuture(
            CourseResponse.builder()
                .id(courseId)
                .title("Course Unavailable")
                .description("Service temporarily unavailable")
                .build());
    }
}
```

#### 1.2 Configuration Properties
```yaml
resilience4j:
  circuitbreaker:
    instances:
      course-service:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        permittedNumberOfCallsInHalfOpenState: 3
        waitDurationInOpenState: 5s
        failureRateThreshold: 50
        slowCallRateThreshold: 50
        slowCallDurationThreshold: 2s
  
  retry:
    instances:
      course-service:
        maxAttempts: 3
        waitDuration: 1s
        retryExceptions:
          - java.net.ConnectException
          - java.net.SocketTimeoutException
  
  timelimiter:
    instances:
      course-service:
        timeoutDuration: 3s
```

### 2. Global Error Handling

#### 2.1 Centralized Exception Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailable(
            ServiceUnavailableException ex) {
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.SERVICE_UNAVAILABLE.value())
            .error("Service Unavailable")
            .message(ex.getMessage())
            .path(getCurrentPath())
            .traceId(getCurrentTraceId())
            .build();
            
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {
        
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Resource Not Found")
            .message(ex.getMessage())
            .path(getCurrentPath())
            .traceId(getCurrentTraceId())
            .build();
            
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```

---

## 📈 Testing Strategy

### 1. Testing Pyramid

#### 1.1 Unit Tests
```java
@ExtendWith(MockitoExtension.class)
class CourseServiceTest {
    
    @Mock
    private CourseRepository courseRepository;
    
    @Mock
    private UserServiceClient userServiceClient;
    
    @InjectMocks
    private CourseService courseService;
    
    @Test
    void shouldCreateCourse_WhenValidRequest() {
        // Given
        CreateCourseRequest request = CreateCourseRequest.builder()
            .title("Spring Boot Course")
            .description("Learn Spring Boot")
            .instructorId("instructor-1")
            .build();
            
        when(userServiceClient.getUserById("instructor-1"))
            .thenReturn(UserResponse.builder().id("instructor-1").build());
        
        // When
        CourseResponse response = courseService.createCourse(request);
        
        // Then
        assertThat(response.getTitle()).isEqualTo("Spring Boot Course");
        verify(courseRepository).save(any(Course.class));
    }
}
```

#### 1.2 Integration Tests
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class CourseControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @MockBean
    private UserServiceClient userServiceClient;
    
    @Test
    void shouldCreateCourse_WhenValidRequest() {
        // Given
        CreateCourseRequest request = CreateCourseRequest.builder()
            .title("Integration Test Course")
            .description("Test description")
            .instructorId("instructor-1")
            .build();
            
        when(userServiceClient.getUserById("instructor-1"))
            .thenReturn(UserResponse.builder().id("instructor-1").build());
        
        // When
        ResponseEntity<CourseResponse> response = restTemplate.postForEntity(
            "/api/courses", request, CourseResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getTitle()).isEqualTo("Integration Test Course");
    }
}
```

#### 1.3 Contract Tests
```java
@ExtendWith(PactVerificationInvocationContextProvider.class)
@Provider("course-service")
@PactFolder("pacts")
class CourseServiceContractTest {
    
    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }
    
    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", 8082));
    }
}
```

### 2. End-to-End Testing

#### 2.1 TestContainers Setup
```java
@SpringBootTest
@Testcontainers
class CourseServiceE2ETest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_course_db")
            .withUsername("test")
            .withPassword("test");
    
    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }
    
    @Test
    void shouldProcessCourseWorkflow() {
        // Test complete course creation workflow
    }
}
```

---

## 📝 Migration Checklist

### Phase 1: Infrastructure Setup ✅
- [ ] Setup Docker và Docker Compose
- [ ] Setup Service Discovery (Eureka/Consul)
- [ ] Setup Configuration Server
- [ ] Setup API Gateway
- [ ] Setup Message Broker (Kafka)
- [ ] Setup Monitoring Stack (Prometheus, Grafana, ELK)

### Phase 2: User Service Migration ✅
- [ ] Create User Service project structure
- [ ] Migrate User, Role, Permission entities
- [ ] Implement Authentication endpoints
- [ ] Setup JWT token management
- [ ] Implement inter-service communication
- [ ] Write unit and integration tests
- [ ] Setup CI/CD pipeline
- [ ] Deploy to staging environment
- [ ] Performance testing
- [ ] Production deployment

### Phase 3: Course Service Migration ✅
- [ ] Create Course Service project structure
- [ ] Migrate Course, Lesson, Category entities
- [ ] Handle User reference via API calls
- [ ] Implement event publishing for course operations
- [ ] Setup caching for frequently accessed data
- [ ] Write comprehensive tests
- [ ] Update API Gateway routes
- [ ] Deploy and monitor

### Phase 4: Remaining Services Migration ✅
- [ ] Quiz Service migration
- [ ] Enrollment Service migration
- [ ] Document Service migration
- [ ] Review Service migration
- [ ] Analytics Service migration

### Phase 5: Optimization và Production Readiness ✅
- [ ] Performance optimization
- [ ] Security hardening
- [ ] Monitoring và alerting
- [ ] Disaster recovery planning
- [ ] Documentation completion
- [ ] Team training
- [ ] Production deployment
- [ ] Post-migration monitoring

---

## 🎯 Kết Luận

Việc chuyển đổi từ kiến trúc monolithic sang microservices là một quá trình phức tạp đòi hỏi:

1. **Kế hoạch chi tiết** và thực hiện từng bước
2. **Đầu tư vào infrastructure** và tooling
3. **Training team** về các công nghệ mới
4. **Monitoring chặt chẽ** trong quá trình migration
5. **Backup plan** cho các tình huống khẩn cấp

### Lợi Ích Đạt Được:
- **Scalability**: Có thể scale từng service độc lập
- **Reliability**: Fault isolation giữa các service
- **Flexibility**: Sử dụng technology stack khác nhau cho từng service
- **Team Autonomy**: Các team có thể develop và deploy độc lập
- **Faster Development**: Parallel development của các service

### Thách Thức:
- **Complexity**: Increased operational complexity
- **Network Latency**: Inter-service communication overhead
- **Data Consistency**: Distributed transaction management
- **Testing**: More complex testing strategies required
- **Monitoring**: Need for sophisticated observability tools

**Thời gian ước tính**: 6-8 tháng cho việc migration hoàn chỉnh với team 4-6 developers.

**Budget ước tính**: Cần đầu tư vào infrastructure, tools, và training (~20-30% tăng cost trong năm đầu).
package com.ntabodoiqua.online_course_management.service;

import com.ntabodoiqua.online_course_management.dto.request.auth.AuthenticationRequest;
import com.ntabodoiqua.online_course_management.dto.request.auth.IntrospectRequest;
import com.ntabodoiqua.online_course_management.dto.request.auth.LogoutRequest;
import com.ntabodoiqua.online_course_management.dto.request.auth.RefreshRequest;
import com.ntabodoiqua.online_course_management.dto.response.auth.AuthenticationResponse;
import com.ntabodoiqua.online_course_management.dto.response.auth.IntrospectResponse;
import com.ntabodoiqua.online_course_management.entity.InvalidatedToken;
import com.ntabodoiqua.online_course_management.entity.User;
import com.ntabodoiqua.online_course_management.exception.AppException;
import com.ntabodoiqua.online_course_management.exception.ErrorCode;
import com.ntabodoiqua.online_course_management.repository.InvalidatedTokenRepository;
import com.ntabodoiqua.online_course_management.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationService {
    final UserRepository userRepository;
    final InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;
    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @Value("${recaptcha.secret}")
    protected String recaptchaSecret;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            var signedJWT = verifyToken(token, false);
            String username = signedJWT.getJWTClaimsSet().getSubject();

            // Kiểm tra người dùng có bị vô hiệu hóa không
            var user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

            if (!user.isEnabled()) {
                isValid = false;
            }
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    private boolean verifyRecaptcha(String recaptchaToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("secret", recaptchaSecret);
            params.add("response", recaptchaToken);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            String url = "https://www.google.com/recaptcha/api/siteverify";
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

            return (Boolean) response.get("success");
        } catch (Exception e) {
            return false;
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        // Lấy người dùng theo username
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Nếu tài khoản đã bị khóa
        if (!user.isEnabled()) {
            throw new AppException(ErrorCode.USER_DISABLED_DUE_TO_TOO_MANY_ATTEMPTS);
        }

        // Nếu sai >= 5 lần → yêu cầu xác minh reCAPTCHA
        if (user.getLoginFailCount() >= 5) {
            if (request.getRecaptchaToken() == null || !verifyRecaptcha(request.getRecaptchaToken())) {
                throw new AppException(ErrorCode.INVALID_RECAPTCHA);
            }
        }

        // Kiểm tra mật khẩu
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!authenticated) {
            // Tăng số lần sai
            user.setLoginFailCount(user.getLoginFailCount() + 1);

            // Khóa tài khoản nếu sai quá nhiều
            if (user.getLoginFailCount() >= 10) {
                user.setEnabled(false);
            }

            userRepository.save(user);

            // Nếu từ 5–9 → yêu cầu xác minh phía frontend
            if (user.getLoginFailCount() >= 5) {
                throw new AppException(ErrorCode.TOO_MANY_ATTEMPTS);
            }

            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Đăng nhập đúng → reset fail count
        user.setLoginFailCount(0);
        userRepository.save(user);

        // Sinh JWT
        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    // Service xử lý đăng xuất
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            // Nếu token còn trong thời gian refresh, vẫn đưa xuống csdl
            var signToken = verifyToken(request.getToken(), true);
            // lấy ra jwt id token và ngày hết hạn
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryDate(expiryTime)
                    .build();
            // Lưu vào cơ sở dữ liệu
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.info("Token already expired");
        }
    }

    // làm mới token
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        // Kiểm tra hiệu lực token
        var signJWT = verifyToken(request.getToken(), true);

        // Nếu token còn hiệu lực
        // Thực hiện logout token cũ
        var jit = signJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryDate(expiryTime)
                .build();
        // Lưu vào cơ sở dữ liệu
        invalidatedTokenRepository.save(invalidatedToken);

        // Issue token mới
        var username = signJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(username).orElseThrow(
                // không tìm thấy username
                () -> new AppException(ErrorCode.UNAUTHENTICATED)
        );
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    // Xác thực chữ ký JWT
    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        // Nếu đúng, tức là dùng để refresh token
        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                    .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!verified && expiryTime.after(new Date()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        // Kiểm tra JIT còn hiệu lực hay không
        // Nếu không ném ra lỗi.
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("innolearn-edu.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");

         if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions()
                            .forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }

}

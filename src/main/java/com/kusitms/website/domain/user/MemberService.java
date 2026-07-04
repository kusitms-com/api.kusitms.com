package com.kusitms.website.domain.user;

import com.kusitms.website.domain.file.S3Service;
import com.kusitms.website.domain.user.dto.request.SignInRequest;
import com.kusitms.website.domain.user.dto.request.SignUpRequest;
import com.kusitms.website.domain.user.dto.response.CurrentCardinalResponse;
import com.kusitms.website.domain.user.dto.response.SignInResponse;
import com.kusitms.website.global.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MemberService {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^010\\d{8}$");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png");

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final CurrentCardinalRepository currentCardinalRepository;
    private final S3Service s3Service;

    @Transactional
    public Long signup(SignUpRequest request, MultipartFile profileImage, MultipartFile certificateImage) {
        validateSignUpRequest(request, certificateImage);

        if (memberRepository.existsById(request.getId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        validateImageFile(profileImage, "프로필 사진");
        if (certificateImage != null && !certificateImage.isEmpty()) {
            validateImageFile(certificateImage, "수료증");
        }

        Integer currentCardinal = getCurrentCardinalValue();
        MemberRole role = request.getCardinal() >= currentCardinal ? MemberRole.YB : MemberRole.OB;

        String profileImageUrl = s3Service.uploadFile(profileImage, "profile");
        String certificateImageUrl = null;
        if (certificateImage != null && !certificateImage.isEmpty()) {
            certificateImageUrl = s3Service.uploadFile(certificateImage, "certificate");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        Member member = Member.builder()
                .id(request.getId())
                .password(encodedPassword)
                .name(request.getName())
                .phone(normalizePhone(request.getPhone()))
                .cardinal(request.getCardinal())
                .part(Part.valueOf(request.getPart()))
                .profileImageUrl(profileImageUrl)
                .certificateImageUrl(certificateImageUrl)
                .email(request.getEmail())
                .status(MemberStatus.PENDING)
                .role(role)
                .build();

        memberRepository.save(member);
        return member.getUserId();
    }

    @Transactional
    public SignInResponse signin(SignInRequest request) {
        Member member = memberRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (member.getStatus() == MemberStatus.PENDING) {
            throw new IllegalArgumentException("관리자 승인 대기 중인 계정입니다. 승인 후 로그인해 주세요.");
        }
        if (member.getStatus() == MemberStatus.WITHDRAWN) {
            throw new IllegalArgumentException("이미 탈퇴한 계정입니다.");
        }

        if (!bCryptPasswordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        // 기수 변경 시 역할 재판별
        MemberRole role = member.getRole() != null ? member.getRole() : MemberRole.OB;
        if (member.getCardinal() != null) {
            Integer currentCardinal = getCurrentCardinalValue();
            role = member.getCardinal() >= currentCardinal ? MemberRole.YB : MemberRole.OB;
            if (member.getRole() != role) {
                member.updateRole(role);
            }
        }

        String accessToken = jwtTokenProvider.makeJwtToken(member.getUserId());
        String refreshToken = jwtTokenProvider.makeRefreshToken(member.getUserId());
        member.updateRefreshToken(hashToken(refreshToken));

        String redirectPath = role == MemberRole.YB ? "/mypage" : "/mypage/ob/profile";

        return new SignInResponse(accessToken, refreshToken, role, redirectPath);
    }

    public Map<String, Object> checkIdDuplicate(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("아이디를 먼저 입력해 주세요.");
        }
        boolean isDuplicate = memberRepository.existsById(id);
        String message = isDuplicate ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다.";
        return Map.of("available", !isDuplicate, "message", message);
    }

    @Transactional
    public SignInResponse refreshToken(String refreshToken) {
        String hashedToken = hashToken(refreshToken);
        Member member = memberRepository.findByRefreshToken(hashedToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));

        try {
            jwtTokenProvider.validateToken(refreshToken);
        } catch (Exception e) {
            member.clearRefreshToken();
            throw new IllegalArgumentException("리프레시 토큰이 만료되었습니다. 다시 로그인해 주세요.");
        }

        String newAccessToken = jwtTokenProvider.makeJwtToken(member.getUserId());
        String newRefreshToken = jwtTokenProvider.makeRefreshToken(member.getUserId());
        member.updateRefreshToken(hashToken(newRefreshToken));

        String redirectPath = member.getRole() == MemberRole.YB ? "/mypage" : "/mypage/ob/profile";

        return new SignInResponse(newAccessToken, newRefreshToken, member.getRole(), redirectPath);
    }

    public CurrentCardinalResponse getCurrentCardinal() {
        Integer cardinal = getCurrentCardinalValue();
        return new CurrentCardinalResponse(cardinal);
    }

    @Transactional
    public void logout(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        member.clearRefreshToken();
    }

    public List<Member> getPendingMembers() {
        return memberRepository.findAllByStatus(MemberStatus.PENDING);
    }

    private Integer getCurrentCardinalValue() {
        return currentCardinalRepository.findById(1L)
                .map(CurrentCardinal::getCardinal)
                .orElse(33);
    }

    private void validateSignUpRequest(SignUpRequest request, MultipartFile certificateImage) {
        if (request.getId() == null || request.getId().isBlank()) {
            throw new IllegalArgumentException("아이디를 입력해 주세요.");
        }
        if (request.getPassword() == null || !PASSWORD_PATTERN.matcher(request.getPassword()).matches()) {
            throw new IllegalArgumentException("영문, 숫자, 특수문자를 포함해 8자 이상이어야 합니다.");
        }
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("이름을 입력해 주세요.");
        }
        if (request.getPhone() == null || !PHONE_PATTERN.matcher(normalizePhone(request.getPhone())).matches()) {
            throw new IllegalArgumentException("올바른 휴대폰 번호를 입력해 주세요.");
        }
        if (request.getCardinal() == null) {
            throw new IllegalArgumentException("활동 기수를 선택해 주세요.");
        }
        if (request.getPart() == null || request.getPart().isBlank()) {
            throw new IllegalArgumentException("파트를 선택해 주세요.");
        }
        try {
            Part.valueOf(request.getPart());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("파트를 선택해 주세요.");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("이메일을 입력해 주세요.");
        }

        if (request.getCardinal() <= 33 && (certificateImage == null || certificateImage.isEmpty())) {
            throw new IllegalArgumentException("33기 이하 회원은 수료증을 업로드해 주세요.");
        }
    }

    private void validateImageFile(MultipartFile file, String fieldName) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(fieldName + "을(를) 업로드해 주세요.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("10MB 이하의 이미지만 업로드 가능합니다.");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("이미지 파일(jpg, png)만 업로드 가능합니다.");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("이미지 파일(jpg, png)만 업로드 가능합니다.");
        }
    }

    private String normalizePhone(String phone) {
        return phone.replaceAll("[^0-9]", "");
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

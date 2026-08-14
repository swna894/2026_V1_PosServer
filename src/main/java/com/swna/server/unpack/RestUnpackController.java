package com.swna.server.unpack;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.common.response.ApiResponse;
import com.swna.server.unpack.dto.UnpackDto;
import com.swna.server.unpack.service.UnpackService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * ApiResponse<T> + BusinessException/ErrorCode 예외 체계 적용 리팩토링.
 * - 모든 응답을 ApiResponse.success(data)로 감싸서 일관된 응답 포맷 제공.
 * - "찾을 수 없음" 등 비즈니스 예외는 ExceptionUtils로 BusinessException을 던지면
 *   기존에 정의된 GlobalExceptionHandler + ErrorResponseFactory가 ApiResponse.error로 변환한다.
 * - @Valid를 추가해 UnpackDto의 @NotBlank(invoice) 검증이 실제로 동작하도록 했다.
 * - Product 바코드 동기화 비즈니스 로직(isNewProduct)은 동작 변경 없이 그대로 유지.
 */
@RestController
@RequestMapping("/api/unpacks")
@RequiredArgsConstructor
public class RestUnpackController {

    private final UnpackService unpackService;

    @PostMapping
    public ResponseEntity<ApiResponse<UnpackDto>> post(@Valid @RequestBody UnpackDto dto) {
        UnpackDto result = unpackService.createUnpack(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<List<UnpackDto>>> put(@Valid @RequestBody List<UnpackDto> dtos) {
        List<UnpackDto> result = unpackService.updateUnpacks(dtos);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UnpackDto>>> getLocalDate(
            @RequestParam Map<String, String> parameter) {
        List<UnpackDto> result = unpackService.getUnpacksByPeriod(parameter);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deletes(@RequestBody List<UnpackDto> dtos) {
        unpackService.deleteUnpacks(dtos);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
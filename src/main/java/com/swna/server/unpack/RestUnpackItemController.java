package com.swna.server.unpack;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.common.response.ApiResponse;
import com.swna.server.unpack.dto.UnpackItemDto;
import com.swna.server.unpack.service.UnpackItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * ApiResponse<T> + BusinessException/ErrorCode 예외 체계 적용 리팩토링.
 * - 모든 응답을 ApiResponse.success(data)로 감싸서 일관된 응답 포맷 제공.
 * - 기존에는 대상이 없으면 null을 그대로 반환했는데, 이제 ExceptionUtils.resourceNotFound(...)로
 *   BusinessException(ErrorCode.RESOURCE_NOT_FOUND)을 던지고 GlobalExceptionHandler가
 *   404 + ApiResponse.error로 변환한다 (동작이 "조용한 null 반환"에서 "명시적 에러 응답"으로
 *   바뀌는 부분이니 클라이언트 쪽 null 체크 로직이 있다면 확인이 필요하다).
 * - @Valid로 UnpackItemDto의 @NotBlank(barcode)/@NotNull(qty) 검증이 실제로 동작하도록 했다.
 * - Product 동기화/생성 비즈니스 로직은 동작 변경 없이 그대로 유지.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RestUnpackItemController {

    private final UnpackItemService unpackItemService;

    @PutMapping("/unpackItem")
    public ResponseEntity<ApiResponse<UnpackItemDto>> put(@Valid @RequestBody UnpackItemDto dto) {
        UnpackItemDto result = unpackItemService.updateUnpackItem(dto);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/unpackItems")
    public ResponseEntity<ApiResponse<List<UnpackItemDto>>> puts(@Valid @RequestBody List<UnpackItemDto> dtos) {
        List<UnpackItemDto> result = unpackItemService.updateUnpackItems(dtos);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
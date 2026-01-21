package com.logisticstudy.api.masterdata.location.web;

import com.logisticstudy.api.masterdata.location.app.LocationAppService;
import com.logisticstudy.api.masterdata.location.web.dto.CreateLocationRequest;
import com.logisticstudy.api.masterdata.location.web.dto.LocationResponse;
import com.logisticstudy.api.masterdata.location.web.dto.UpdateLocationRequest;
import com.logisticstudy.api.shared.web.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationAppService locationAppService;

    // TODO: 실제 사용자 인증 정보에서 가져오도록 수정
    private static final String CURRENT_USER = "system";

    /**
     * 새로운 로케이션을 생성합니다.
     * @param request 생성 요청 DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createLocation(@Valid @RequestBody CreateLocationRequest request) {
        locationAppService.createLocation(request, CURRENT_USER);
    }

    /**
     * ID로 로케이션 정보를 조회합니다.
     * @param id 로케이션 ID
     * @return 조회된 로케이션 응답 DTO
     */
    @GetMapping("/{id}")
    public LocationResponse getLocation(@PathVariable Long id) {
        return locationAppService.getLocation(id);
    }

    /**
     * 로케이션 정보를 업데이트합니다.
     * @param id 로케이션 ID
     * @param request 업데이트 요청 DTO
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLocation(@PathVariable Long id, @Valid @RequestBody UpdateLocationRequest request) {
        if (!id.equals(request.id())) {
            throw new IllegalArgumentException("경로 변수 ID와 요청 본문의 ID가 일치하지 않습니다.");
        }
        locationAppService.updateLocation(request, CURRENT_USER);
    }

    /**
     * 페이징 처리된 로케이션 목록을 조회합니다.
     * @param page 조회할 페이지 번호 (기본값 0)
     * @param size 페이지당 로케이션 수 (기본값 10)
     * @return 페이징 처리된 로케이션 응답 DTO 목록
     */
    @GetMapping
    public PageResponse<LocationResponse> getLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return locationAppService.getLocations(page, size);
    }
}

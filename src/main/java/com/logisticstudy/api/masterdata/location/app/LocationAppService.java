package com.logisticstudy.api.masterdata.location.app;

import com.logisticstudy.api.masterdata.location.domain.Location;
import com.logisticstudy.api.masterdata.location.domain.contract.LocationRepository;
import com.logisticstudy.api.masterdata.location.web.dto.CreateLocationRequest;
import com.logisticstudy.api.masterdata.location.web.dto.LocationResponse;
import com.logisticstudy.api.masterdata.location.web.dto.UpdateLocationRequest;
import com.logisticstudy.api.masterdata.location.web.LocationDtoMapper;
import com.logisticstudy.api.shared.domain.page.PageResult;
import com.logisticstudy.api.shared.web.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationAppService {
    private final LocationRepository locationRepository;
    private final LocationIdGenerator locationIdGenerator;
    private final LocationDtoMapper locationDtoMapper;

    /**
     * 새로운 로케이션을 생성합니다.
     * @param request 생성 요청 DTO
     * @param creator 생성자 ID
     */
    public void createLocation(CreateLocationRequest request, String creator) {
        Long newId = locationIdGenerator.next();
        Location location = locationDtoMapper.toDomain(request, newId, creator);
        locationRepository.save(location);
    }

    /**
     * ID로 로케이션 정보를 조회합니다.
     * @param id 로케이션 ID
     * @return 조회된 로케이션 응답 DTO
     * @throws NoSuchElementException 해당 ID의 로케이션을 찾을 수 없을 경우
     */
    @Transactional(readOnly = true)
    public LocationResponse getLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ID가 " + id + "인 로케이션을 찾을 수 없습니다."));
        return locationDtoMapper.toResponse(location);
    }

    /**
     * 로케이션 정보를 업데이트합니다.
     * @param request 업데이트 요청 DTO
     * @param updater 수정자 ID
     * @throws NoSuchElementException 해당 ID의 로케이션을 찾을 수 없을 경우
     */
    public void updateLocation(UpdateLocationRequest request, String updater) {
        Location existingLocation = locationRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("ID가 " + request.id() + "인 로케이션을 찾을 수 없습니다."));

        Location updatedLocation = locationDtoMapper.toDomain(request, existingLocation, updater);
        locationRepository.save(updatedLocation);
    }

    /**
     * 페이징 처리된 로케이션 목록을 조회합니다.
     * @param page 조회할 페이지 번호
     * @param size 페이지당 로케이션 수
     * @return 페이징 처리된 로케이션 응답 DTO 목록
     */
    @Transactional(readOnly = true)
    public PageResponse<LocationResponse> getLocations(int page, int size) {
        PageResult<Location> locations = locationRepository.findPage(page, size);
        var response = locations.content().stream().map(locationDtoMapper::toResponse).toList();
        return new PageResponse<>(response, locations.page(), locations.size(), locations.totalElements(), locations.totalPages(), locations.hasNext());
    }
}
package com.swna.server.environment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnvironmentService {

    private final EnvironmentRepository environmentRepository;

    @Transactional
    public EnvironmentDto createEnvironment(EnvironmentDto requestDto) {
        EnvironmentEntity entity = requestDto.toEntity();
        EnvironmentEntity savedEntity = environmentRepository.save(entity);
        return EnvironmentDto.from(savedEntity);
    }

    public EnvironmentDto getEnvironmentById(Long id) {
        EnvironmentEntity entity = findEntityOrThrow(id);
        return EnvironmentDto.from(entity);
    }

    public List<EnvironmentDto> getAllEnvironments() {
        return environmentRepository.findAll().stream()
                .map(EnvironmentDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public EnvironmentDto updateEnvironment(Long id, EnvironmentDto requestDto) {
        EnvironmentEntity entity = findEntityOrThrow(id);
        // record는 getter 이름이 getBackupFolder()가 아니라 backupFolder() 형태입니다.
        entity.update(requestDto.backupFolder(), requestDto.backupExe(), requestDto.reportFolder());
        return EnvironmentDto.from(entity);
    }

    @Transactional
    public void deleteEnvironment(Long id) {
        EnvironmentEntity entity = findEntityOrThrow(id);
        environmentRepository.delete(entity);
    }

    private EnvironmentEntity findEntityOrThrow(Long id) {
        return environmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 환경 설정을 찾을 수 없습니다. ID: " + id));
    }
}
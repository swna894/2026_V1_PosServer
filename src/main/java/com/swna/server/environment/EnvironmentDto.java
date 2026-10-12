package com.swna.server.environment;


public record EnvironmentDto(
        Long id,
        String backupFolder,
        String backupExe,
        String reportFolder
) {
    // Entity -> DTO 변환 메서드
    public static EnvironmentDto from(EnvironmentEntity entity) {
        return new EnvironmentDto(
                entity.getId(),
                entity.getBackupFolder(),
                entity.getBackupExe(),
                entity.getReportFolder()
        );
    }

    // DTO -> Entity 변환 메서드 (신규 등록용)
    public EnvironmentEntity toEntity() {
        return EnvironmentEntity.builder()
                .backupFolder(backupFolder)
                .backupExe(backupExe)
                .reportFolder(reportFolder)
                .build();
    }
}

package com.swna.server.environment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "environments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnvironmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String backupFolder;

    @Column(nullable = false)
    private String backupExe;

    @Column(nullable = false)
    private String reportFolder;

    @Builder
    public EnvironmentEntity(String backupFolder, String backupExe, String reportFolder) {
        this.backupFolder = backupFolder;
        this.backupExe = backupExe;
        this.reportFolder = reportFolder;
    }

    public void update(String backupFolder, String backupExe, String reportFolder) {
        this.backupFolder = backupFolder;
        this.backupExe = backupExe;
        this.reportFolder = reportFolder;
    }
}
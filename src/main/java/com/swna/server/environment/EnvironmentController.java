package com.swna.server.environment;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/environments")
@RequiredArgsConstructor
public class EnvironmentController {

    private final EnvironmentService environmentService;

    @PostMapping
    public ResponseEntity<EnvironmentDto> createEnvironment(@RequestBody EnvironmentDto requestDto) {
        EnvironmentDto created = environmentService.createEnvironment(requestDto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnvironmentDto> getEnvironmentById(@PathVariable("id") Long id) {
        EnvironmentDto environment = environmentService.getEnvironmentById(id);
        return ResponseEntity.ok(environment);
    }

    @GetMapping
    public ResponseEntity<List<EnvironmentDto>> getAllEnvironments() {
        List<EnvironmentDto> environments = environmentService.getAllEnvironments();
        return ResponseEntity.ok(environments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnvironmentDto> updateEnvironment(
            @PathVariable("id") Long id,
            @RequestBody EnvironmentDto requestDto
    ) {
        EnvironmentDto updated = environmentService.updateEnvironment(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnvironment(@PathVariable("id") Long id) {
        environmentService.deleteEnvironment(id);
        return ResponseEntity.ok().build();
    }
}
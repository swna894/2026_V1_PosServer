package com.swna.server.unpack.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swna.server.unpack.model.UnpackItem;

public interface UnpackItemRepository extends JpaRepository <UnpackItem, Long>{
}

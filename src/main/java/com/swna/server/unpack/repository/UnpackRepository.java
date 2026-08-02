package com.swna.server.unpack.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swna.server.unpack.model.Unpack;


public interface UnpackRepository extends JpaRepository <Unpack, Long>{

//	@Modifying
//	@Transactional
//	void deleteByReceipt(String receipt);

	List<Unpack> findByUpdatedBetweenOrderByUpdatedDesc(LocalDateTime start, LocalDateTime end);

	List<Unpack> findByUpdatedBetweenOrderByUnpackedDesc(LocalDateTime start, LocalDateTime end);

	void deleteByInvoice(String invoice);

	Unpack findByInvoice(String invoice);
}

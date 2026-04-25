package com.swna.server.supplier.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swna.server.supplier.entity.Supplier;

public interface  SupplierRepository extends JpaRepository<Supplier, Long>{
   
}

package com.yeonieum.productservice.domain.product.repository;

import com.yeonieum.productservice.domain.product.entity.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceStatusRepository extends JpaRepository<ServiceStatus, Long> {
    ServiceStatus findByStatusName(String statusName);
}

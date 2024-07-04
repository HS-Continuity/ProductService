package com.yeonieum.productservice.domain.customer.repository;

import com.yeonieum.productservice.domain.customer.entity.CustomerMember;
import com.yeonieum.productservice.domain.customer.entity.CustomerMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerMemberRepository extends JpaRepository<CustomerMember, CustomerMemberId> {
}

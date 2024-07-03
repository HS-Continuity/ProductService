package com.yeonieum.productservice.infrastructure.persistance.repository.customer;

import com.yeonieum.productservice.infrastructure.persistance.entity.customer.CustomerMember;
import com.yeonieum.productservice.infrastructure.persistance.entity.customer.CustomerMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerMemberRepository extends JpaRepository<CustomerMember, CustomerMemberId> {
}

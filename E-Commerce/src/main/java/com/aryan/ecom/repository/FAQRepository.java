package com.aryan.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aryan.ecom.model.FAQ;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long>{
	List<FAQ> findAllByProductId(Long productId);
}

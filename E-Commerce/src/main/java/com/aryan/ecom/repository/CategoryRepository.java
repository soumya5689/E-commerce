package com.aryan.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aryan.ecom.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

}

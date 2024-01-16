package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Order;
import com.mexico.sas.admin.api.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNum(String orderNum);
    List<Order> findByProject(Project project);

}

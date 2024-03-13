package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Order;
import com.mexico.sas.admin.api.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {

    List<Order> findByActiveIsTrueAndEliminateIsFalseOrderByOrderNumAsc();

    List<Order> findByProjectOrderByOrderNumAscOrderDateAsc(Project project);

    @Transactional
    @Modifying
    @Query("update Order o set o.eliminate = :eliminate, o.active = :active, o.status = :status, o.requisitionStatus = :status where o.orderNum = :orderNum")
    void deleteLogic(@Param(value = "orderNum") String orderNum, @Param(value = "status") Long status,
                     @Param(value = "eliminate") Boolean eliminate, @Param(value = "active") Boolean active);

}

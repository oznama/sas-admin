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
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findByActiveIsTrueAndEliminateIsFalse();

    Optional<Order> findByOrderNum(String orderNum);
    List<Order> findByProjectOrderByOrderDateDesc(Project project);

    @Transactional
    @Modifying
    @Query("update Order o set o.eliminate = :eliminate, o.active = :active, o.status = :status, o.requisitionStatus = :status where o.id = :id")
    void deleteLogic(@Param(value = "id") Long id, @Param(value = "status") Long status,
                     @Param(value = "eliminate") Boolean eliminate, @Param(value = "active") Boolean active);

}

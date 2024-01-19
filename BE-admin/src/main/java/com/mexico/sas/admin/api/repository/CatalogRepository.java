package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


/**
 * @author Oziel Naranjo
 */
@Repository
public interface CatalogRepository extends JpaRepository<Catalog, Long> {
    List<Catalog> findByCatalogParentIsNullAndIdNotInOrderByIdAsc(List<Long> ids);
    List<Catalog> findByCatalogParentIsNullAndStatusIsNotAndInternalIsFalseAndIdNotInOrderByIdAsc(Long status, List<Long> ids);
    List<Catalog> findByCatalogParentOrderByIdAsc(Catalog catalogParent);
    List<Catalog> findByCatalogParentAndCompanyIdAndStatusIsNotOrderByIdAsc(Catalog catalogParent, Long companyId, Long status);
    Optional<Catalog> findById(Long id);
    Optional<Catalog> findByIdAndCatalogParent(Long id, Catalog catalogParent);
    Optional<Catalog> findFirstByCatalogParentIsNullOrderByIdDesc();
    Optional<Catalog> findFirstByCatalogParentOrderByIdDesc(Catalog catalogParent);
    @Transactional
    @Modifying
    @Query("update Catalog c set c.status = :status where c.id = :id")
    void updateStatus(@Param(value = "id") Long id, @Param(value = "status") Long status);
}

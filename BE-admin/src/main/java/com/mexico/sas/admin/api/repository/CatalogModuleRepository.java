package com.mexico.sas.admin.api.repository;

import com.mexico.sas.admin.api.model.CatalogModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Oziel Naranjo
 */
@Repository
public interface CatalogModuleRepository extends JpaRepository<CatalogModule, Long> {

    List<CatalogModule> findByCatalogId(Long catalogId);

    @Transactional
    @Modifying
    @Query("delete from CatalogModule cm where cm.catalogId = :catalogId")
    void deleteByCatalogId(@Param(value = "catalogId") Long catalogId);

    @Transactional
    @Modifying
    @Query("delete from CatalogModule cm where cm.catalogModuleId = :catalogModuleId and cm.catalogId = :catalogId")
    void deleteByCatalogModuleIdAndCatalogId(@Param(value = "catalogModuleId") Long catalogModuleId, @Param(value = "catalogId") Long catalogId);
}

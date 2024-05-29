package com.mexico.sas.nativequeries.api.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class CatalogRepository extends BaseRepository {


    @Value("${query.catalog.email}")
    private String queryCatalogEmail;

    public List<String> getEmails() {
        return getForList(queryCatalogEmail, String.class);
    }


}

package com.selenium.pricecollector.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.UUID;
@Repository
public interface EntryDataRepository extends JpaRepository<EntryData, UUID> {

    @Transactional
    void deleteByCompanyAndDataCollectionDatetimeAfter(String company, Timestamp dataCollectionDatetime);
}

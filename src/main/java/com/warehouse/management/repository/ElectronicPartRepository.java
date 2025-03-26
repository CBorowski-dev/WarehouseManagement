package com.warehouse.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.warehouse.management.model.ElectronicPart;

@Repository
public interface ElectronicPartRepository extends JpaRepository<ElectronicPart, Long> {
    
    // Custom search query with dynamic conditions
    @Query("SELECT e FROM ElectronicPart e WHERE " +
           "(:type IS NULL OR e.partType.name LIKE %:type%) AND " +
           "(:name IS NULL OR e.name LIKE %:name%) AND " +
           "(:properties IS NULL OR e.properties LIKE %:properties%) AND " +
           "(:location IS NULL OR e.storageLocation.name LIKE %:location%)")
    List<ElectronicPart> searchParts(
            @Param("type") String type,
            @Param("name") String name,
            @Param("properties") String properties,
            @Param("location") String location);
}

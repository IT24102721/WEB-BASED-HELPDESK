package org.example.se2030helpdesk.repository;

import org.example.se2030helpdesk.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository  extends JpaRepository<Resource, Long> {
    List<Resource> findByStatus(Resource.Status status);
    List<Resource>  findByCategory(String category);
    List<Resource> findByLocation(String location);
    List<Resource> findByCategoryAndStatus(String category, Resource.Status status);
    List<Resource> findByLocationAndStatus(String location, Resource.Status status);
    
    @Query("SELECT DISTINCT r.category FROM Resource r ORDER BY r.category")
    List<String> findDistinctCategories();
    
    @Query("SELECT DISTINCT r.location FROM Resource r ORDER BY r.location")
    List<String> findDistinctLocations();
    
    @Query("SELECT r FROM Resource r WHERE r.name LIKE %:search% OR r.description LIKE %:search% OR r.serialNumber LIKE %:search%")
    List<Resource> searchResources(@Param("search") String search);
    
    Optional<Resource> findBySerialNumber(String serialNumber);
}

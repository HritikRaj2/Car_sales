package com.carsales.carsales.Repository;


import com.carsales.carsales.Entity.CarsDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarDetailRepository extends JpaRepository<CarsDetail, Long> {

}

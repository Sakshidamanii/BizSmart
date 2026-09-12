package com.bizsmart.repositories;

import com.bizsmart.models.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByBillNumber(String billNumber);
    List<Bill> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<Bill> findAllByOrderByCreatedAtDesc();

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b WHERE CAST(b.createdAt AS DATE) = CURRENT_DATE")
    BigDecimal calculateTodaySales();

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b WHERE MONTH(b.createdAt) = MONTH(CURRENT_DATE) AND YEAR(b.createdAt) = YEAR(CURRENT_DATE)")
    BigDecimal calculateCurrentMonthSales();
}

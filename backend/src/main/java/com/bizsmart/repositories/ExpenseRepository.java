package com.bizsmart.repositories;

import com.bizsmart.models.Expense;
import com.bizsmart.models.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByExpenseDateBetweenOrderByExpenseDateDesc(LocalDate startDate, LocalDate endDate);
    List<Expense> findByCategoryOrderByExpenseDateDesc(ExpenseCategory category);
    List<Expense> findAllByOrderByExpenseDateDesc();

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE MONTH(e.expenseDate) = MONTH(CURRENT_DATE) AND YEAR(e.expenseDate) = YEAR(CURRENT_DATE)")
    BigDecimal calculateCurrentMonthExpenses();

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e")
    BigDecimal calculateTotalExpenses();
}

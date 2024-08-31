package com.sontung.eproject_springboot.repository;

import com.sontung.eproject_springboot.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IOrderRepository extends JpaRepository<Order, String> {
    @Query("SELECT COUNT(o) FROM Order o ")
    long countOrder();

    @Query("SELECT o FROM Order o WHERE o.orderDate = :filterDate")
    Page<Order> findByOrderDateOrder(@Param("filterDate") LocalDate filterDate, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.orderDate = :filterDate")
    List<Order> findByOrderDateCombo(@Param("filterDate") LocalDate filterDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate = :filterDate")
    long countOrderByFilterDate(@Param("filterDate") LocalDate filterDate);



    @Query("SELECT o FROM Order o WHERE o.totalAmount < 100000")
    Page<Order> findOrdersUnder100K(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.totalAmount >= 100000 AND o.totalAmount <= 200000")
    Page<Order> findOrdersBetween100KAnd200K(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.totalAmount > 200000 AND o.totalAmount <= 300000")
    Page<Order> findOrdersBetween200KAnd300K(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.totalAmount > 300000 AND o.totalAmount <= 500000")
    Page<Order> findOrdersBetween300KAnd500K(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.totalAmount > 500000")
    Page<Order> findOrdersOver500K(Pageable pageable);

    // Đếm số lượng Order có totalAmount dưới 100,000
    long countByTotalAmountLessThan(BigDecimal amount);

    // Đếm số lượng Order có totalAmount trong khoảng từ amount1 đến amount2
    long countByTotalAmountBetween(BigDecimal amount1, BigDecimal amount2);

    // Đếm số lượng Order có totalAmount lớn hơn hoặc bằng 400,000
    long countByTotalAmountGreaterThanEqual(BigDecimal amount);
}

package com.sontung.eproject_springboot.repository.impl;

import com.sontung.eproject_springboot.entity.*;
import com.sontung.eproject_springboot.entity.Order;
import com.sontung.eproject_springboot.enums.OrderStatus;
import com.sontung.eproject_springboot.repository.SearchRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SearchRepositoryImpl implements SearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void getAllProductWithSortByColoumAndSearch(int pageNo, int pageSize, String search, String sortBy) {
        // query ra list product
        StringBuilder sqlQuery = new StringBuilder("select p from Product p where p.status=1");
        // Search
        if (StringUtils.hasLength(search)) {
            sqlQuery.append(" and lower(p.productName) like lower(:name)");
            //sqlQuery.append(" or lower(p.description) like lower(:description)");
        }
        // Sort
        if (StringUtils.hasLength(sortBy)) {
            sqlQuery.append(" order by p.order_date " + sortBy);
        }
        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        // Pagination
        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);
        if (StringUtils.hasLength(search)) {
            selectQuery.setParameter("name", String.format("%%%s%%", search)); //"%" + search + "%" thay the bang String.format("%%%s%%", search)
            //  selectQuery.setParameter("description", "%" + search + "%");
        }

        List products = selectQuery.getResultList();

        System.out.println(products);

        // query so record
        StringBuilder sqlCountQuery = new StringBuilder("select count(*) from Product p where p.status=1 ");
        if (StringUtils.hasLength(search)) {
            sqlCountQuery.append(" and lower(p.productName) like lower(?1)"); // thay vì đặt :name thì đánh số thứ tự 1
        }
        Query selectCountQuery = entityManager.createQuery(sqlCountQuery.toString());
        if (StringUtils.hasLength(search)) {
            selectCountQuery.setParameter(1, String.format("%%%s%%", search)); // truyền tham số vào vị trí 1
        }
        Long totalElement = (Long) selectCountQuery.getSingleResult();


    }


    /**
     * @Summary:
     * @Description: Func này được thực hiện bằng cách dùng HQL, đa chưa phải là cách tôí ưu.
     * Nhưng để phục v muc đích hoc tap, can thuc hiẹn cho biết
     * @Param:
     * @Return:
     **/
    @Override
    public Page<Order> getAllOrderWithSortByColoumAndSearch(int pageNo, int pageSize, String search, String sortBy, String status, String accountId) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        // Query chính để lấy danh sách Order
        StringBuilder hqlQuery = new StringBuilder("SELECT o FROM Order o where o.account.accountId = :accountId");

        // Thêm điều kiện tìm kiếm
        if (StringUtils.hasLength(search)) {
            hqlQuery.append(" and lower(o.code) like lower(:code)");
            hqlQuery.append(" or lower(o.shippingPhone) like lower(:shipping_phone)");
            hqlQuery.append(" or lower(o.email) like lower(:email)");
            hqlQuery.append(" or lower(o.firstName) like lower(:first_name)");
            hqlQuery.append(" or lower(o.lastName) like lower(:last_name)");
        }

        // Thêm điều kiẹện lọc status
        if (StringUtils.hasLength(status)) {
            hqlQuery.append(" AND o.status = :status");
        }

        // Thêm điều kiện sắp xếp
        if (StringUtils.hasLength(sortBy)) {
            hqlQuery.append(" order by o.orderDate asc");
        } else {
            // Mặc định sắp xếp theo ngày đặt hàng.
            hqlQuery.append(" order by o.orderDate desc");
        }

        // Tạo truy vấn
        Query selectQuery = entityManager.createQuery(hqlQuery.toString());
        selectQuery.setParameter("accountId", accountId);

        // Gans giá tr tham số tìm kiếm nếu có.
        if (StringUtils.hasLength(search)) {
            selectQuery.setParameter("code", String.format("%%%s%%", search)); // Spring Boot 2: ==> ":code"
            selectQuery.setParameter("shipping_phone", String.format("%%%s%%", search));
            selectQuery.setParameter("email", String.format("%%%s%%", search));
            selectQuery.setParameter("first_name", String.format("%%%s%%", search));
            selectQuery.setParameter("last_name", String.format("%%%s%%", search));
        }

        // Gans giá trị tham số 'status' nếu có.
        if (StringUtils.hasLength(status)) {
            selectQuery.setParameter("status", status);
        }

        // Phân trang
        selectQuery.setFirstResult((int) pageable.getOffset());
        selectQuery.setMaxResults(pageable.getPageSize());

        // Lấy kết quả
        List orders = selectQuery.getResultList();

        // Query để đếm số bản ghi
        StringBuilder sqlCountQuery = new StringBuilder("select count(*) from Order o where 1=1");
        if (StringUtils.hasLength(search)) {
            sqlCountQuery.append(" AND (LOWER(o.code) LIKE LOWER(:search) ");
            sqlCountQuery.append(" OR LOWER(o.shippingPhone) LIKE LOWER(:search) ");
            sqlCountQuery.append(" OR LOWER(o.email) LIKE LOWER(:search) ");
            sqlCountQuery.append(" OR LOWER(o.firstName) LIKE LOWER(:search) ");
            sqlCountQuery.append(" OR LOWER(o.lastName) LIKE LOWER(:search))");
        }
        Query selectCountQuery = entityManager.createQuery(sqlCountQuery.toString());
        if (StringUtils.hasLength(search)) {
            selectCountQuery.setParameter("search", String.format("%%%s%%", search));
        }

        Long totalElement = (Long) selectCountQuery.getSingleResult();

        return new PageImpl<>(orders, pageable, totalElement);
    }

    /**
     * @Summary:
     * @Description: Func này được thực hiện bằng cách dùng CriteriaBuider, đay chưa phải là cách tôí ưu.
     * Nhưng để phục v muc đích hoc tap, can thuc hiẹn cho biết
     * @Param:
     * @Return:
     **/
    @Override
    public Page<Order> getAllOrderWithSortByColoumAndSearchCriteriaBuider(int pageNo, int pageSize, String search, String status, String sortBy, String accountId) {
        // Khởi tạo CriteriaBuilder và CriteriaQuery
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> root = cq.from(Order.class);

        // Join với đối tượng liên kết (account)
        Join<Order, Account> accountJoin = root.join("account");

        // Xây dựng điều kiện tìm kiếm
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(accountJoin.get("accountId"), accountId));

        // Điều kiện tìm kiếm chính xác
        Predicate exactMatchPredicate = null;
        if (StringUtils.hasLength(search)) {
            String searchPattern = "%" + search.toLowerCase() + "%";

            // Điều kiện tìm kiếm chính xác
            exactMatchPredicate = cb.or(
                    cb.like(cb.lower(root.get("code")), searchPattern),
                    cb.like(cb.lower(root.get("shippingAddress")), searchPattern),
                    cb.like(cb.lower(root.get("email")), searchPattern),
                    cb.like(cb.lower(root.get("firstName")), searchPattern),
                    cb.like(cb.lower(root.get("lastName")), searchPattern)
            );
            predicates.add(cb.or(exactMatchPredicate));

        }

        // Dieu kien loc status
        if (StringUtils.hasLength(status)) {
            int statusValue = OrderStatus.valueOf(status).ordinal();
            predicates.add(cb.equal(root.get("status"), statusValue));
        }

//            // Điều kiện tìm kiếm không chính xác
//            char[] searchChars = search.toLowerCase().toCharArray();
//            List<Predicate> fuzzyPredicates = new ArrayList<>();
//            for (char searchChar : searchChars) {
//                String charPattern = "%" + searchChar + "%";
//                fuzzyPredicates.add(cb.like(cb.lower(root.get("code")), charPattern));
//                fuzzyPredicates.add(cb.like(cb.lower(root.get("shippingAddress")), charPattern));
//                fuzzyPredicates.add(cb.like(cb.lower(root.get("email")), charPattern));
//                fuzzyPredicates.add(cb.like(cb.lower(root.get("firstName")), charPattern));
//                fuzzyPredicates.add(cb.like(cb.lower(root.get("lastName")), charPattern));
//            }
//
//            // Kết hợp cả hai điều kiện
//            predicates.add(cb.or(exactMatchPredicate, cb.or(fuzzyPredicates.toArray(new Predicate[0]))));

        // Kết hợp tất cả điều kiện bằng AND
        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        // Sắp xếp kết quả, ưu tiên những kết quả khớp chính xác trước
//        if (StringUtils.hasLength(sortBy)) {
//            Expression<Object> exactMatchOrder = cb.selectCase()
//                    .when(cb.or(
//                            cb.like(cb.lower(root.get("code")), search.toLowerCase()),
//                            cb.like(cb.lower(root.get("shippingAddress")), search.toLowerCase()),
//                            cb.like(cb.lower(root.get("email")), search.toLowerCase()),
//                            cb.like(cb.lower(root.get("firstName")), search.toLowerCase()),
//                            cb.like(cb.lower(root.get("lastName")), search.toLowerCase())
//                    ), 1)
//                    .otherwise(0);
//
//            // Thêm vào điều kiện sắp xếp
//            cq.orderBy(cb.desc(exactMatchOrder), cb.asc(root.get(sortBy)));
//        }

        // Thực hiện truy vấn trước để lấy toàn bộ dữ liệu
        List<Order> orders = entityManager.createQuery(cq).getResultList();

        // Loại bỏ các bản ghi trùng lặp
        List<Order> uniqueOrders = orders.stream().distinct().collect(Collectors.toList());

        // Áp dụng phân trang trên kết quả sau khi đã loại bỏ trùng lặp
        int start = Math.min(pageNo * pageSize, uniqueOrders.size());
        int end = Math.min((pageNo + 1) * pageSize, uniqueOrders.size());
        List<Order> paginatedOrders = uniqueOrders.subList(start, end);

        // Tính toán tổng số bản ghi
        long totalElements = uniqueOrders.size();

        // Trả về kết quả dưới dạng Page
        return new PageImpl<>(paginatedOrders, PageRequest.of(pageNo, pageSize), totalElements);
    }

    @Override
    public Page<Order> getAllOrderWithFilterDateAmongPriceAndSearchCriteriaBuider(int pageNo, int pageSize, String search, int amongPrice, String status, LocalDateTime filterDate, LocalDateTime filterDate2) {
        // Khởi tạo CriteriaBuilder và CriteriaQuery
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> root = cq.from(Order.class);

    // Join với bảng OrderDetail
    Join<Order, OrderDetail> orderDetailJoin = root.join("orderDetails", JoinType.INNER);

        // Join với bảng Product từ OrderDetail
        Join<OrderDetail, Product> productJoin = orderDetailJoin.join("product", JoinType.INNER);


        // Join với bảng Combo từ OrderDetail
        Join<OrderDetail, Combo>  comboJoin = orderDetailJoin.join("combo", JoinType.LEFT);

        // Join với comboDetail từt Combo
        Join<Combo,ComboDetail> comboDetailJoin = comboJoin.join("comboDetails", JoinType.LEFT);
        // Join với bảng product từ comboDetail
        Join<ComboDetail, Product> productJoinCombodetail= comboDetailJoin.join("product", JoinType.LEFT);

        // Xây dựng điều kiện tìm kiếm
        List<Predicate> predicates = new ArrayList<>();

        // Điều kiện tìm kiếm chính xác
        if (StringUtils.hasLength(search)) {
            String searchPattern = "%" + search.toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("code")), searchPattern),
                    cb.like(cb.lower(root.get("shippingAddress")), searchPattern),
                    cb.like(cb.lower(root.get("shippingPhone")), searchPattern),
                    cb.like(cb.lower(root.get("email")), searchPattern),
                    cb.like(cb.lower(root.get("firstName")), searchPattern),
                    cb.like(cb.lower(root.get("lastName")), searchPattern),
                    cb.like(cb.lower(productJoin.get("productName")), searchPattern),
                    cb.like(cb.lower(productJoinCombodetail.get("productName")), searchPattern)
            ));
        }

        // Điều kiện lọc theo status
        if (StringUtils.hasLength(status)) {
            int statusValue = OrderStatus.valueOf(status).ordinal();
            predicates.add(cb.equal(root.get("status"), statusValue));
        }

        // Điều kiện lọc theo ngày
        if (filterDate != null && filterDate2 != null) {
            predicates.add(cb.between(root.get("orderDate").as(LocalDateTime.class), filterDate, filterDate2));
        }

        // Điều kiện lọc theo giá (giả sử amongPrice là giá lớn nhất)
        switch (amongPrice) {
            case 1:
                // <=100.000
                predicates.add(cb.lessThanOrEqualTo(root.get("totalAmount"), 100000));
                break;
            case 2:
                // 100.000 - 200.000
                predicates.add(cb.between(root.get("totalAmount"), 100000,200000));
                break;
            case 3:
                // 200.000 -300.000
                predicates.add(cb.between(root.get("totalAmount"), 200000,300000));

                break;
            case 4:
                // 300.000 - 500.000
                predicates.add(cb.between(root.get("totalAmount"), 300000,500000));

                break;
            case 5:
                // > 500.000
                predicates.add(cb.greaterThanOrEqualTo(root.get("totalAmount"), 500000));
                break;
        }


        // Kết hợp tất cả điều kiện bằng AND
        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        // Thêm sắp xếp nếu cần
        cq.orderBy(cb.desc(root.get("orderDate")));

        // Áp dụng phân trang và thực hiện truy vấn
        TypedQuery<Order> query = entityManager.createQuery(cq);
        query.setFirstResult(pageNo * pageSize);
        query.setMaxResults(pageSize);

        // Thực hiện truy vấn và trả về kết quả đã phân trang
        List<Order> orders = query.getResultList();

        // Tính toán tổng số bản ghi
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Order> countRoot = countQuery.from(Order.class);
        countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[0])));
        Long totalElements;
        try {
            totalElements = entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            totalElements = 0L;
        }

        return new PageImpl<>(orders, PageRequest.of(pageNo, pageSize), totalElements);
    }


}

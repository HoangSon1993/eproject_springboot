package com.sontung.eproject_springboot.repository.impl;

import com.sontung.eproject_springboot.entity.Account;
import com.sontung.eproject_springboot.entity.Order;
import com.sontung.eproject_springboot.entity.Product;
import com.sontung.eproject_springboot.enums.OrderStatus;
import com.sontung.eproject_springboot.repository.SearchRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Override
    public Page<Product> getAllProductWithSortByColumAndSearch(int pageNo, int pageSize, String search, int amongPrice, String status, String sortBy, LocalDate timeStart, LocalDate timeEnd) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        // Query chính để lấy danh sách Product
        StringBuilder hqlQuery = new StringBuilder("SELECT p FROM Product p WHERE 1 = 1");

        // Thêm điều kiện tìm kiếm
        if (StringUtils.hasLength(search)) {
            hqlQuery.append(" AND LOWER(p.productName) LIKE LOWER (:productName)");
            //  hqlQuery.append(" AND LOWER(p.description) like lower (:discription)");
        }

        // Thêm điều kiện lọc status
        if (StringUtils.hasLength(status)) {
            hqlQuery.append(" AND p.status = :status");
        }

        // Thêm điều kiện lọc theo khoảng thời gian
        if (timeEnd != null && timeStart != null) {
            hqlQuery.append(" AND p.createdDate BETWEEN :timeStart AND :timeEnd");
        }

        // Điều kiện lọc theo gias
        switch (amongPrice) {
            case 1:
                // <= 100.000
                hqlQuery.append(" AND p.price <= 100000");
                break;
            case 2:
                // 100.000 - 200.000
                hqlQuery.append(" AND p.price BETWEEN 100000 AND 200000");
                break;
            case 3:
                // 200.000 -300.000
                hqlQuery.append(" AND p.price BETWEEN 200000 AND 300000");
                break;
            case 4:
                // 300.000 - 500.000
                hqlQuery.append(" AND p.price BETWEEN 300000 AND 400000");
                break;
            case 5:
                // > 500.000
                hqlQuery.append(" AND p.price > 500000");
                break;
        }

        // Thêm điều kiện sắp xếp
        if (StringUtils.hasLength(sortBy)) {
            hqlQuery.append(" order by p.createdDate asc");
        } else {
            hqlQuery.append(" order by p.createdDate desc");
        }

        // Tạo truy vấn
        Query selectQuery = entityManager.createQuery(hqlQuery.toString());

        // Phân trang
        selectQuery.setFirstResult((int) pageable.getOffset());
        selectQuery.setMaxResults(pageable.getPageSize());

        // Gán giá trị vào tham số tìm kiếm nếu có.
        if (StringUtils.hasLength(search)) {
            selectQuery.setParameter("productName", String.format("%%%s%%", search));
            // selectQuery.setParameter("description", String.format("%%%s%%", search));
        }

        // Gán gía trị vào tham số 'status' nếu có.
        if (StringUtils.hasLength(status)) {
            selectQuery.setParameter("status", status);
        }

        // Gán giá trị vào tham số 'timeStart' và 'timeEnd' nếu có.
        if (timeEnd != null && timeStart != null) {
            selectQuery.setParameter("timeStart", timeStart); // String.format("%%%s%%", timeStart)
            selectQuery.setParameter("timeEnd", timeEnd); // String.format("%%%s%%", timeEnd)
        }

        // Lấy kết quả.
        List orders = selectQuery.getResultList();

        // ====== QUERY ĐỂ ĐẾM SỐ BẢN GHI ======
        // Query chính để đếm số bản ghi.
        StringBuilder sqlCountQuery = new StringBuilder("select count(*) from Product p where 1=1");

        // Thêm điều kiện tìm kiếm
        if (StringUtils.hasLength(search)) {
            sqlCountQuery.append(" AND LOWER(p.productName) like lower (:search)");
        }

        if (StringUtils.hasLength(status)) {
            sqlCountQuery.append(" AND p.status = :status");
        }

        // Thêm điều kiện lọc theo khoảng thời gian.
        if (timeEnd != null && timeStart != null) {
            sqlCountQuery.append(" AND p.createdDate BETWEEN :timeStart AND :timeEnd");
        }

        // Thêm điều kiện lọc theo giá
        switch (amongPrice) {
            case 1:
                sqlCountQuery.append(" AND p.price <= 100000");
                break;
            case 2:
                sqlCountQuery.append(" AND p.price BETWEEN 100000 AND 200000");
                break;
            case 3:
                sqlCountQuery.append(" AND p.price BETWEEN 200000 AND 300000");
                break;
            case 4:
                sqlCountQuery.append(" AND p.price BETWEEN 300000 AND 400000");
                break;
            case 5:
                sqlCountQuery.append(" AND p.price > 500000");
                break;
        }

        //Tạo truy vấn.
        Query selectCountQuery = entityManager.createQuery(sqlCountQuery.toString());

        // Gán giá trị vào tham số tìm kiếm nếu có.
        if (StringUtils.hasLength(search)) {
            selectCountQuery.setParameter("search", String.format("%%%s%%", search));
        }

        if (StringUtils.hasLength(status)) {
            selectCountQuery.setParameter("status", status);
        }

        if (timeEnd != null && timeStart != null) {
            selectCountQuery.setParameter("timeStart", timeStart); // String.format("%%%s%%", timeStart)
            selectCountQuery.setParameter("timeEnd", timeEnd); //String.format("%%%s%%", timeEnd)
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
            String searchPattern = "%" + removeDiacritics(search).toLowerCase() + "%";

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
    public Page<Order> getAllOrderWithFilterDateAmongPriceAndSearchCriteriaBuider(
            int pageNo,
            int pageSize,
            String search,
            int amongPrice,
            String status,
            LocalDateTime filterDate,
            LocalDateTime filterDate2) {
        // Khởi tạo CriteriaBuilder và CriteriaQuery
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> query = criteriaBuilder.createQuery(Order.class);
        Root<Order> root = query.from(Order.class);

        // Join với bảng OrderDetail
        //Join<Order, OrderDetail> orderDetailJoin = root.join("orderDetails", JoinType.RIGHT);

        // Join với bảng Product từ OrderDetail
        //  Join<OrderDetail, Product> productJoin = orderDetailJoin.join("product", JoinType.RIGHT);


        // Join với bảng Combo từ OrderDetail
        //Join<OrderDetail, Combo> comboJoin = orderDetailJoin.join("combo", JoinType.RIGHT);

        // Join với comboDetail từt Combo
        //Join<Combo, ComboDetail> comboDetailJoin = comboJoin.join("comboDetails", JoinType.RIGHT);
        // Join với bảng product từ comboDetail
        //Join<ComboDetail, Product> productJoinCombodetail = comboDetailJoin.join("product", JoinType.RIGHT);

        // Xây dựng điều kiện tìm kiếm
        List<Predicate> predicates = new ArrayList<>();

        // Điều kiện tìm kiếm chính xác
        if (StringUtils.hasLength(search)) {
            String searchPattern = "%" + removeDiacritics(search).toLowerCase() + "%";
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("shippingAddress")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("shippingPhone")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), searchPattern)
//                    criteriaBuilder.like(criteriaBuilder.lower(productJoin.get("productName")), searchPattern),
//                    criteriaBuilder.like(criteriaBuilder.lower(productJoinCombodetail.get("productName")), searchPattern)
            ));
        }

        // Điều kiện lọc theo status
        if (StringUtils.hasLength(status)) {
            int statusValue = OrderStatus.valueOf(status).ordinal();
            predicates.add(criteriaBuilder.equal(root.get("status"), statusValue));
        }

        // Điều kiện lọc theo ngày
        if (filterDate != null && filterDate2 != null) {
            predicates.add(criteriaBuilder.between(root.get("orderDate").as(LocalDateTime.class), filterDate, filterDate2));
        }

        // Điều kiện lọc theo giá (giả sử amongPrice là giá lớn nhất)
        switch (amongPrice) {
            case 1:
                // <=100.000
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("totalAmount"), 100000));
                break;
            case 2:
                // 100.000 - 200.000
                predicates.add(criteriaBuilder.between(root.get("totalAmount"), 100000, 200000));
                break;
            case 3:
                // 200.000 -300.000
                predicates.add(criteriaBuilder.between(root.get("totalAmount"), 200000, 300000));

                break;
            case 4:
                // 300.000 - 500.000
                predicates.add(criteriaBuilder.between(root.get("totalAmount"), 300000, 500000));

                break;
            case 5:
                // > 500.000
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalAmount"), 500000));
                break;
        }


        // Kết hợp tất cả điều kiện bằng AND
        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Thêm sắp xếp nếu cần
        query.orderBy(criteriaBuilder.desc(root.get("orderDate")));

        // Áp dụng phân trang và thực hiện truy vấn
        List<Order> orders = entityManager.createQuery(query)
                // Todo: pageNo * pageSize
                .setFirstResult(pageNo * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

//        TypedQuery<Order> query = entityManager.createQuery(query);
//        query.setFirstResult(pageNo * pageSize);
//        query.setMaxResults(pageSize);
//        // Thực hiện truy vấn và trả về kết quả đã phân trang
//        List<Order> orders = query.getResultList();


        // Tính toán tổng số bản ghi
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Order> countRoot = countQuery.from(Order.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        Long totalElements;
        try {
            totalElements = entityManager.createQuery(countQuery).getSingleResult();
        } catch (Exception e) {
            totalElements = 0L;
        }
        return new PageImpl<>(orders, PageRequest.of(pageNo, pageSize), totalElements);
    }


    @Override
    public Page<Product> findByStatusAndProductNameContainingAndPriceFilter(
            int status, String categoryId, String search, int amongPrice, Pageable pageable) {
        // Tạo truy vấn cơ bản
        StringBuilder hqlQuery = new StringBuilder("SELECT p FROM Product p WHERE p.status = :status");

        if (StringUtils.hasLength(categoryId)) {
            hqlQuery.append(" AND p.category.id = :categoryId");
        }

        if (StringUtils.hasLength(search)) {
            hqlQuery.append(" AND LOWER(p.productName) LIKE LOWER(:search)");
        }

        // Điều kiện lọc theo amongPrice
        if (amongPrice > 0) {
            hqlQuery.append(" AND p.price <= :amongPrice");
        }

        // Áp dụng sắp xếp dựa trên Pageable
        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            hqlQuery.append(" ORDER BY ");
            sort.forEach(order -> {
                hqlQuery.append("p.")
                        .append(order.getProperty())
                        .append(" ")
                        .append(order.getDirection().name())
                        .append(", ");
            });
            // Xóa dấu phẩy cuối cùng
            hqlQuery.setLength(hqlQuery.length() - 2);
        }

        // Tạo query với EntityManager
        Query query = entityManager.createQuery(hqlQuery.toString());
        query.setParameter("status", status);

        if (StringUtils.hasLength(search)) {
            query.setParameter("search", String.format("%%%s%%", removeDiacritics(search)));
        }

        if (StringUtils.hasLength(categoryId)) {
            query.setParameter("categoryId", categoryId);
        }

        if (amongPrice > 0) {
            query.setParameter("amongPrice", BigDecimal.valueOf(amongPrice * 1000));
        }

        // Phân trang: Thiết lập vị trí bắt đầu và số lượng kết quả tối đa
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        List<Product> resultList = query.getResultList();

        // Đếm tổng số sản phẩm phù hợp
        long total = countTotalProducts(status, categoryId, search, amongPrice);

        return new PageImpl<>(resultList, pageable, total);
    }

    private long countTotalProducts(int status, String categoryId, String search, int amongPrice) {
        StringBuilder countQuery = new StringBuilder("SELECT COUNT(p) FROM Product p WHERE p.status = :status");

        if (StringUtils.hasLength(categoryId)) {
            countQuery.append(" AND p.category.id = :categoryId");
        }

        if (StringUtils.hasLength(search)) {
            countQuery.append(" AND LOWER(p.productName) LIKE LOWER(:search)");
        }

        if (amongPrice > 0) {
            countQuery.append(" AND p.price <= :amongPrice");
        }

        Query query = entityManager.createQuery(countQuery.toString());
        query.setParameter("status", status);

        if (StringUtils.hasLength(search)) {
            query.setParameter("search", String.format("%%%s%%", removeDiacritics(search)));
        }

        if (StringUtils.hasLength(categoryId)) {
            query.setParameter("categoryId", categoryId);
        }

        if (amongPrice > 0) {
            query.setParameter("amongPrice", BigDecimal.valueOf(amongPrice * 1000));
        }

        return (long) query.getSingleResult();
    }

    /**
     * @Summary: Chuyển đổi chuỗi sang chuỗi Unicode
     * @Description: Dùng để loại bỏ ký tự có dấu trong tiếng việt trước khi search.
     * @Param: String s
     */
    public static String removeDiacritics(String s) {
        return java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}

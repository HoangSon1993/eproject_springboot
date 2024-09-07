package com.sontung.eproject_springboot.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public class SearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

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
            sqlQuery.append(" order by p.productName " + sortBy);
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
}

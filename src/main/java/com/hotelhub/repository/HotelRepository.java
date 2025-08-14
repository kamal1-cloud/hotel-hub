package com.hotelhub.repository;

import com.hotelhub.dto.HotelDto;
import com.hotelhub.dto.HotelStatisticsDto;
import com.hotelhub.dto.PagedResult;
import com.hotelhub.entity.Hotel;
import com.hotelhub.mapper.HotelMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;


@ApplicationScoped
public class HotelRepository implements PanacheRepository<Hotel> {

    @Inject
    HotelMapper hotelMapper;


    public Hotel findByCupidId(Long cupidId) {
        return find("cupidId", cupidId).firstResult();
    }

    public PagedResult<HotelDto> findWithFilters(int page, int size, String city, String countryCode,
                                                 BigDecimal minRating, BigDecimal maxRating, Integer minStars, Integer maxStars) {
        StringBuilder queryBuilder = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        if (city != null && !city.trim().isEmpty()) {
            queryBuilder.append(" AND LOWER(city) LIKE LOWER(:city)");
            params.put("city", "%" + city + "%");
        }
        if (countryCode != null && !countryCode.trim().isEmpty()) {
            queryBuilder.append(" AND LOWER(countryCode) = LOWER(:countryCode)");
            params.put("countryCode", countryCode);
        }
        if (minRating != null) {
            queryBuilder.append(" AND rating >= :minRating");
            params.put("minRating", minRating);
        }
        if (maxRating != null) {
            queryBuilder.append(" AND rating <= :maxRating");
            params.put("maxRating", maxRating);
        }
        if (minStars != null) {
            queryBuilder.append(" AND stars >= :minStars");
            params.put("minStars", minStars);
        }
        if (maxStars != null) {
            queryBuilder.append(" AND stars <= :maxStars");
            params.put("maxStars", maxStars);
        }

        var query = find(queryBuilder.toString(), Sort.by("rating").descending(), params);
        var pagedQuery = query.page(Page.of(page, size));
        var hotels = pagedQuery.list().stream()
                .map(h -> hotelMapper.toDto(h))
                .collect(Collectors.toList());

        long totalCount = query.count();
        return new PagedResult<>(hotels, page, size, totalCount);
    }

    public PagedResult<HotelDto> searchHotels(String query, int page, int size) {
        String searchQuery = "LOWER(name) LIKE LOWER(:query) OR LOWER(description) LIKE LOWER(:query) OR LOWER(address) LIKE LOWER(:query) OR LOWER(city) LIKE LOWER(:query)";
        Map<String, Object> params = Map.of("query", "%" + query + "%");

        var hotelQuery = find(searchQuery, Sort.by("rating").descending(), params);
        var pagedQuery = hotelQuery.page(Page.of(page, size));
        var hotels = pagedQuery.list().stream()
                .map(h -> hotelMapper.toDto(h))
                .collect(Collectors.toList());

        long totalCount = hotelQuery.count();
        return new PagedResult<>(hotels, page, size, totalCount);
    }


    public HotelStatisticsDto getHotelStatistics() {
        var stats = new HotelStatisticsDto();

        stats.totalHotels = count();

        var avgRating = getEntityManager().createQuery(
                        "SELECT AVG(h.rating) FROM Hotel h WHERE h.rating IS NOT NULL", Double.class)
                .getSingleResult();
        stats.averageRating = avgRating;

        stats.maxStars = getEntityManager().createQuery(
                        "SELECT MAX(h.stars) FROM Hotel h WHERE h.stars IS NOT NULL", Integer.class)
                .getSingleResult();

        stats.minStars = getEntityManager().createQuery(
                        "SELECT MIN(h.stars) FROM Hotel h WHERE h.stars IS NOT NULL", Integer.class)
                .getSingleResult();


        stats.totalReviews = 0L;

        stats.hotelsByCountry = getEntityManager().createQuery(
                        "SELECT h.countryCode, COUNT(h) FROM Hotel h WHERE h.countryCode IS NOT NULL GROUP BY h.countryCode",
                        Object[].class)
                .getResultList().stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> ((Number) arr[1]).longValue()
                ));

        stats.hotelsByCity = getEntityManager().createQuery(
                        "SELECT h.city, COUNT(h) FROM Hotel h WHERE h.city IS NOT NULL " +
                                "GROUP BY h.city ORDER BY COUNT(h) DESC",
                        Object[].class)
                .setMaxResults(10)
                .getResultList().stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> ((Number) arr[1]).longValue(),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        stats.hotelsByStars = getEntityManager().createQuery(
                        "SELECT h.stars, COUNT(h) FROM Hotel h WHERE h.stars IS NOT NULL " +
                                "GROUP BY h.stars ORDER BY h.stars",
                        Object[].class)
                .getResultList().stream()
                .collect(Collectors.toMap(
                        arr -> (Integer) arr[0],
                        arr -> ((Number) arr[1]).longValue(),
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return stats;
    }
}
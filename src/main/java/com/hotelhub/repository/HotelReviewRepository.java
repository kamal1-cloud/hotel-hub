package com.hotelhub.repository;

import com.hotelhub.client.dto.CupidReviewDto;
import com.hotelhub.dto.HotelReviewDto;
import com.hotelhub.dto.PagedResult;
import com.hotelhub.entity.Hotel;
import com.hotelhub.entity.HotelReview;
import com.hotelhub.mapper.HotelReviewMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class HotelReviewRepository implements PanacheRepository<HotelReview> {

    @Inject
    EntityManager entityManager;

    @Inject
    HotelReviewMapper reviewMapper;

    public PagedResult<HotelReviewDto> getHotelReviews(Long hotelId, int page, int size) {
        long totalCount = entityManager.createQuery(
                        "SELECT COUNT(r) FROM HotelReview r WHERE r.hotel.id = :hid", Long.class)
                .setParameter("hid", hotelId)
                .getSingleResult();

        var reviews = entityManager.createQuery(
                        "SELECT r FROM HotelReview r WHERE r.hotel.id = :hid ORDER BY r.createdAt DESC", HotelReview.class)
                .setParameter("hid", hotelId)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList()
                .stream()
                .map(r -> reviewMapper.toDto(r))
                .collect(Collectors.toList());

        return new PagedResult<>(reviews, page, size, totalCount);
    }

    @Transactional
    public void deleteByHotel(Hotel hotel) {
        entityManager.createQuery("DELETE FROM HotelReview r WHERE r.hotel = :hotel")
                .setParameter("hotel", hotel)
                .executeUpdate();
    }

    public long getTotalReviewsCount() {
        return entityManager.createQuery("SELECT COUNT(r) FROM HotelReview r", Long.class)
                .getSingleResult();
    }

    public List<HotelReview> findByHotel(Hotel hotel) {
        return entityManager.createQuery("SELECT r FROM HotelReview r WHERE r.hotel = :hotel", HotelReview.class)
                .setParameter("hotel", hotel)
                .getResultList();
    }

    @Transactional
    public void updateHotelReviews(Hotel hotel, List<CupidReviewDto> reviewDtos) {
        hotel = entityManager.getReference(Hotel.class, hotel.id);

        for (var existing : List.copyOf(hotel.reviews)) {
            hotel.reviews.remove(existing);
            entityManager.remove(existing);
        }
        entityManager.flush();

        if (reviewDtos == null) return;

        for (var rd : reviewDtos) {
            var r = new HotelReview();
            r.id = null;  // important
            r.hotel = hotel;
            r.cupidReviewId = rd.reviewId;
            r.averageScore = rd.averageScore != null ? BigDecimal.valueOf(rd.averageScore) : null;
            r.country = rd.country;
            r.type = rd.type;
            r.name = rd.name;
            r.date = rd.date;
            r.headline = rd.headline;
            r.language = rd.language;
            r.pros = rd.pros;
            r.cons = rd.cons;
            r.source = rd.source;
            hotel.reviews.add(r);
        }
        entityManager.flush();
    }
}
package com.hotelhub.repository;

import com.hotelhub.client.dto.CupidPropertyDto;
import com.hotelhub.dto.HotelPhotoDto;
import com.hotelhub.entity.Hotel;
import com.hotelhub.entity.HotelPhoto;
import com.hotelhub.mapper.HotelPhotoMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class HotelPhotoRepository implements PanacheRepository<HotelPhoto> {

    @Inject
    EntityManager entityManager;

    @Inject
    HotelPhotoMapper photoMapper;

    public List<HotelPhotoDto> getHotelPhotos(Long hotelId) {
        return entityManager.createQuery(
                        "SELECT p FROM HotelPhoto p WHERE p.hotel.id = :hid " +
                                "ORDER BY p.mainPhoto DESC, p.score DESC", HotelPhoto.class)
                .setParameter("hid", hotelId)
                .getResultList()
                .stream()
                .map(photoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteByHotel(Hotel hotel) {
        entityManager.createQuery("DELETE FROM HotelPhoto p WHERE p.hotel = :hotel")
                .setParameter("hotel", hotel)
                .executeUpdate();
    }

    @Transactional
    public void updateHotelPhotos(Hotel hotel, List<CupidPropertyDto.Photo> photoDtos) {
        hotel = entityManager.getReference(Hotel.class, hotel.id);

        for (var existing : List.copyOf(hotel.photos)) {
            hotel.photos.remove(existing);
            entityManager.remove(existing);
        }
        entityManager.flush();

        if (photoDtos == null) return;

        for (var p : photoDtos) {
            var photo = new HotelPhoto();
            photo.id = null; // IMPORTANT
            photo.hotel = hotel;
            photo.url = p.url;
            photo.hdUrl = p.hdUrl;
            photo.imageDescription = p.imageDescription;
            photo.imageClass1 = p.imageClass1;
            photo.imageClass2 = p.imageClass2;
            photo.mainPhoto = p.mainPhoto;
            photo.score = p.score != null ? BigDecimal.valueOf(p.score) : null;
            photo.classId = p.classId;
            photo.classOrder = p.classOrder;
            hotel.photos.add(photo);
        }

        entityManager.flush();
    }
}
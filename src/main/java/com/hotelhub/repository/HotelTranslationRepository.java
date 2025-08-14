package com.hotelhub.repository;

import com.hotelhub.client.dto.CupidPropertyDto;
import com.hotelhub.dto.HotelTranslationDto;
import com.hotelhub.entity.Hotel;
import com.hotelhub.entity.HotelTranslation;
import com.hotelhub.mapper.HotelTranslationMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class HotelTranslationRepository implements PanacheRepository<HotelTranslation> {

    @Inject
    EntityManager entityManager;

    @Inject
    HotelTranslationMapper translationMapper;

    public HotelTranslation findByHotelAndLanguage(Hotel hotel, String language) {
        return entityManager.createQuery(
                        "SELECT t FROM HotelTranslation t WHERE t.hotel.id = :hid AND t.language = :lang",
                        HotelTranslation.class)
                .setParameter("hid", hotel.id)
                .setParameter("lang", language)
                .getResultStream().findFirst().orElse(null);
    }

    public List<HotelTranslationDto> getHotelTranslations(Long hotelId, String language) {
        List<HotelTranslation> list;
        if (language != null) {
            list = entityManager.createQuery(
                            "SELECT t FROM HotelTranslation t WHERE t.hotel.id = :hid AND t.language = :lang",
                            HotelTranslation.class)
                    .setParameter("hid", hotelId)
                    .setParameter("lang", language)
                    .getResultList();
        } else {
            list = entityManager.createQuery(
                            "SELECT t FROM HotelTranslation t WHERE t.hotel.id = :hid",
                            HotelTranslation.class)
                    .setParameter("hid", hotelId)
                    .getResultList();
        }

        return list.stream().map(translationMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteByHotel(Hotel hotel) {
        entityManager.createQuery("DELETE FROM HotelTranslation t WHERE t.hotel = :hotel")
                .setParameter("hotel", hotel)
                .executeUpdate();
    }

    public List<HotelTranslation> findByHotel(Hotel hotel) {
        return entityManager.createQuery("SELECT t FROM HotelTranslation t WHERE t.hotel = :hotel", HotelTranslation.class)
                .setParameter("hotel", hotel)
                .getResultList();
    }

    @Transactional
    public void updateHotelTranslations(Hotel hotel, CupidPropertyDto fr, CupidPropertyDto es) {
        hotel = entityManager.getReference(Hotel.class, hotel.id);

        if (fr != null) {
            var t = entityManager.createQuery(
                            "SELECT t FROM HotelTranslation t WHERE t.hotel.id = :hid AND t.language = :lang",
                            HotelTranslation.class)
                    .setParameter("hid", hotel.id)
                    .setParameter("lang", "fr")
                    .getResultStream().findFirst().orElse(null);
            if (t == null) {
                t = new HotelTranslation();
                t.id = null;
                t.hotel = hotel;
                t.language = "fr";
                entityManager.persist(t);
            }
            t.translatedName = fr.hotelName;
            t.translatedDescription = fr.description;
            t.translatedAddress = fr.address != null ? fr.address.address : null;
        }

        if (es != null) {
            var t = entityManager.createQuery(
                            "SELECT t FROM HotelTranslation t WHERE t.hotel.id = :hid AND t.language = :lang",
                            HotelTranslation.class)
                    .setParameter("hid", hotel.id)
                    .setParameter("lang", "es")
                    .getResultStream().findFirst().orElse(null);
            if (t == null) {
                t = new HotelTranslation();
                t.id = null;
                t.hotel = hotel;
                t.language = "es";
                entityManager.persist(t);
            }
            t.translatedName = es.hotelName;
            t.translatedDescription = es.description;
            t.translatedAddress = es.address != null ? es.address.address : null;
        }
    }
}
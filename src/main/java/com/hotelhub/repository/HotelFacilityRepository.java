package com.hotelhub.repository;

import com.hotelhub.client.dto.CupidPropertyDto;
import com.hotelhub.dto.HotelFacilityDto;
import com.hotelhub.entity.Hotel;
import com.hotelhub.entity.HotelFacility;
import com.hotelhub.mapper.HotelFacilityMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class HotelFacilityRepository implements PanacheRepository<HotelFacility> {

    @Inject
    EntityManager entityManager;

    @Inject
    HotelFacilityMapper facilityMapper;

    public List<HotelFacilityDto> getHotelFacilities(Long hotelId) {
        return entityManager.createQuery(
                        "SELECT f FROM HotelFacility f WHERE f.hotel.id = :hid ORDER BY f.name ASC",
                        HotelFacility.class)
                .setParameter("hid", hotelId)
                .getResultList()
                .stream()
                .map(facilityMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteByHotel(Hotel hotel) {
        entityManager.createQuery("DELETE FROM HotelFacility f WHERE f.hotel = :hotel")
                .setParameter("hotel", hotel)
                .executeUpdate();
    }

    @Transactional
    public void updateHotelFacilities(Hotel hotel, List<CupidPropertyDto.Facility> facilityDtos) {
        hotel = entityManager.getReference(Hotel.class, hotel.id);

        for (var existing : List.copyOf(hotel.facilities)) {
            hotel.facilities.remove(existing);
            entityManager.remove(existing);
        }
        entityManager.flush();

        if (facilityDtos == null) return;

        for (var f : facilityDtos) {
            var fac = new HotelFacility();
            fac.id = null;
            fac.hotel = hotel;
            fac.facilityId = f.facilityId;
            fac.name = f.name;
            hotel.facilities.add(fac);
        }
        entityManager.flush();
    }
}
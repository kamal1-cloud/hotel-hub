package com.hotelhub.repository;

import com.hotelhub.client.dto.CupidPropertyDto;
import com.hotelhub.entity.Hotel;
import com.hotelhub.entity.HotelPolicy;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class HotelPolicyRepository implements PanacheRepository<HotelPolicy> {

    @Inject
    EntityManager entityManager;

    @Transactional
    public void deleteByHotel(Hotel hotel) {
        entityManager.createQuery("DELETE FROM HotelPolicy p WHERE p.hotel = :hotel")
                .setParameter("hotel", hotel)
                .executeUpdate();
    }

    public List<HotelPolicy> findByHotel(Hotel hotel) {
        return entityManager.createQuery("SELECT p FROM HotelPolicy p WHERE p.hotel = :hotel", HotelPolicy.class)
                .setParameter("hotel", hotel)
                .getResultList();
    }

    @Transactional
    public void updateHotelPolicies(Hotel hotel, List<CupidPropertyDto.Policy> policyDtos) {
        hotel = entityManager.getReference(Hotel.class, hotel.id);

        for (var existing : List.copyOf(hotel.policies)) {
            hotel.policies.remove(existing);
            entityManager.remove(existing);
        }
        entityManager.flush();

        if (policyDtos == null) return;

        for (var p : policyDtos) {
            var pol = new HotelPolicy();
            pol.id = null;
            pol.hotel = hotel;
            pol.policyType = p.policyType;
            pol.name = p.name;
            pol.description = p.description;
            pol.childAllowed = p.childAllowed;
            pol.petsAllowed = p.petsAllowed;
            pol.parking = p.parking;
            hotel.policies.add(pol);
        }
        entityManager.flush();
    }
}
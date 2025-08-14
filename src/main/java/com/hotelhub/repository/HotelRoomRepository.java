package com.hotelhub.repository;

import com.hotelhub.client.dto.CupidPropertyDto;
import com.hotelhub.dto.HotelRoomDto;
import com.hotelhub.entity.Hotel;
import com.hotelhub.entity.HotelRoom;
import com.hotelhub.entity.HotelRoomAmenity;
import com.hotelhub.entity.HotelRoomBedType;
import com.hotelhub.entity.HotelRoomPhoto;
import com.hotelhub.entity.HotelRoomView;
import com.hotelhub.mapper.HotelRoomMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class HotelRoomRepository implements PanacheRepository<HotelRoom> {

    @Inject
    EntityManager entityManager;

    @Inject
    HotelRoomMapper roomMapper;

    public List<HotelRoomDto> getHotelRooms(Long hotelId) {
        var rooms = entityManager.createQuery(
                        "SELECT r FROM HotelRoom r WHERE r.hotel.id = :hid ORDER BY r.roomName ASC",
                        HotelRoom.class)
                .setParameter("hid", hotelId)
                .getResultList();

        return rooms.stream()
                .map(room -> {
                    room.bedTypes = entityManager.createQuery(
                                    "SELECT bt FROM HotelRoomBedType bt WHERE bt.room.id = :rid",
                                    HotelRoomBedType.class)
                            .setParameter("rid", room.id)
                            .getResultList();

                    room.amenities = entityManager.createQuery(
                                    "SELECT a FROM HotelRoomAmenity a WHERE a.room.id = :rid ORDER BY a.sortOrder ASC",
                                    HotelRoomAmenity.class)
                            .setParameter("rid", room.id)
                            .getResultList();

                    room.photos = entityManager.createQuery(
                                    "SELECT p FROM HotelRoomPhoto p WHERE p.room.id = :rid " +
                                            "ORDER BY p.mainPhoto DESC, p.score DESC",
                                    HotelRoomPhoto.class)
                            .setParameter("rid", room.id)
                            .getResultList();

                    room.views = entityManager.createQuery(
                                    "SELECT v FROM HotelRoomView v WHERE v.room.id = :rid",
                                    HotelRoomView.class)
                            .setParameter("rid", room.id)
                            .getResultList();

                    return roomMapper.toDto(room);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateHotelRooms(Hotel hotel, List<CupidPropertyDto.Room> roomDtos) {
        hotel = entityManager.getReference(Hotel.class, hotel.id);

        for (var existing : List.copyOf(hotel.rooms)) {
            hotel.rooms.remove(existing);
            entityManager.remove(existing);
        }
        entityManager.flush();

        if (roomDtos == null || roomDtos.isEmpty()) return;

        for (var dto : roomDtos) {
            HotelRoom room = new HotelRoom();
            room.id = null;
            room.hotel = hotel;
            room.cupidRoomId = dto.id;
            room.roomName = dto.roomName;
            room.description = dto.description;
            room.roomSizeSquare = dto.roomSizeSquare != null ? dto.roomSizeSquare.intValue() : null;
            room.roomSizeUnit = dto.roomSizeUnit;
            room.maxAdults = dto.maxAdults;
            room.maxChildren = dto.maxChildren;
            room.maxOccupancy = dto.maxOccupancy;
            room.bedRelation = dto.bedRelation;

            // bed types
            if (dto.bedTypes != null) {
                for (var b : dto.bedTypes) {
                    var bt = new HotelRoomBedType();
                    bt.id = null;
                    bt.room = room;
                    bt.quantity = b.quantity;
                    bt.bedType = b.bedType;
                    bt.bedSize = b.bedSize;
                    room.bedTypes.add(bt);
                }
            }

            // amenities
            if (dto.roomAmenities != null) {
                for (var a : dto.roomAmenities) {
                    var amenity = new HotelRoomAmenity();
                    amenity.id = null;
                    amenity.room = room;
                    amenity.amenityId = a.amenitiesId;
                    amenity.name = a.name;
                    amenity.sortOrder = a.sort;
                    room.amenities.add(amenity);
                }
            }

            // photos
            if (dto.photos != null) {
                for (var p : dto.photos) {
                    var photo = new HotelRoomPhoto();
                    photo.id = null;
                    photo.room = room;
                    photo.url = p.url;
                    photo.hdUrl = p.hdUrl;
                    photo.imageDescription = p.imageDescription;
                    photo.imageClass1 = p.imageClass1;
                    photo.imageClass2 = p.imageClass2;
                    photo.mainPhoto = p.mainPhoto;
                    photo.score = p.score != null ? BigDecimal.valueOf(p.score) : null;
                    photo.classId = p.classId;
                    photo.classOrder = p.classOrder;
                    room.photos.add(photo);
                }
            }

            // views
            if (dto.views != null) {
                for (var v : dto.views) {
                    var view = new HotelRoomView();
                    view.id = null;
                    view.room = room;
                    view.viewId = v.id;
                    view.view = v.view;
                    room.views.add(view);
                }
            }

            hotel.rooms.add(room);
        }

        entityManager.flush();
    }
}
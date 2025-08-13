package com.hotelhub.repository;

import com.hotelhub.client.dto.CupidPropertyDto;
import com.hotelhub.client.dto.CupidReviewDto;
import com.hotelhub.dto.HotelDto;
import com.hotelhub.dto.HotelFacilityDto;
import com.hotelhub.dto.HotelPhotoDto;
import com.hotelhub.dto.HotelReviewDto;
import com.hotelhub.dto.HotelRoomDto;
import com.hotelhub.dto.HotelStatisticsDto;
import com.hotelhub.dto.HotelTranslationDto;
import com.hotelhub.dto.PagedResult;
import com.hotelhub.entity.Hotel;
import com.hotelhub.entity.HotelFacility;
import com.hotelhub.entity.HotelPhoto;
import com.hotelhub.entity.HotelPolicy;
import com.hotelhub.entity.HotelReview;
import com.hotelhub.entity.HotelRoom;
import com.hotelhub.entity.HotelRoomAmenity;
import com.hotelhub.entity.HotelRoomBedType;
import com.hotelhub.entity.HotelRoomPhoto;
import com.hotelhub.entity.HotelRoomView;
import com.hotelhub.entity.HotelTranslation;
import com.hotelhub.mapper.HotelFacilityMapper;
import com.hotelhub.mapper.HotelMapper;
import com.hotelhub.mapper.HotelPhotoMapper;
import com.hotelhub.mapper.HotelReviewMapper;
import com.hotelhub.mapper.HotelRoomMapper;
import com.hotelhub.mapper.HotelTranslationMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@ApplicationScoped
public class HotelRepository implements PanacheRepository<Hotel> {

    @Inject
    EntityManager entityManager;

    @Inject
    HotelMapper hotelMapper;

    @Inject
    HotelReviewMapper reviewMapper;

    @Inject
    HotelTranslationMapper translationMapper;

    @Inject
    HotelPhotoMapper photoMapper;

    @Inject
    HotelFacilityMapper facilityMapper;

    @Inject
    HotelRoomMapper roomMapper;

    public Hotel findByCupidId(Long cupidId) {
        return find("cupidId", cupidId).firstResult();
    }


    public Hotel findByIdWithRelations(Long id) {
        Hotel hotel = findById(id);
        if (hotel != null) {
            hotel.photos = entityManager.createQuery(
                            "SELECT p FROM HotelPhoto p WHERE p.hotel.id = :id", HotelPhoto.class)
                    .setParameter("id", id)
                    .getResultList();

            hotel.facilities = entityManager.createQuery(
                            "SELECT f FROM HotelFacility f WHERE f.hotel.id = :id", HotelFacility.class)
                    .setParameter("id", id)
                    .getResultList();

            hotel.reviews = entityManager.createQuery(
                            "SELECT r FROM HotelReview r WHERE r.hotel.id = :id", HotelReview.class)
                    .setParameter("id", id)
                    .getResultList();

            hotel.translations = entityManager.createQuery(
                            "SELECT t FROM HotelTranslation t WHERE t.hotel.id = :id", HotelTranslation.class)
                    .setParameter("id", id)
                    .getResultList();
        }
        return hotel;
    }

    public HotelTranslation findByHotelAndLanguage(Hotel hotel, String language) {
        return entityManager.createQuery(
                        "SELECT t FROM HotelTranslation t WHERE t.hotel.id = :hid AND t.language = :lang",
                        HotelTranslation.class)
                .setParameter("hid", hotel.id)
                .setParameter("lang", language)
                .getResultStream().findFirst().orElse(null);
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


    public PagedResult<HotelReviewDto> getHotelReviews(Long hotelId, int page, int size) {
        var hotel = findById(hotelId);
        if (hotel == null) return new PagedResult<>(List.of(), page, size, 0);

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


    public List<HotelTranslationDto> getHotelTranslations(Long hotelId, String language) {
        var hotel = findById(hotelId);
        if (hotel == null) return List.of();

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


    public List<HotelPhotoDto> getHotelPhotos(Long hotelId) {
        var hotel = findById(hotelId);
        if (hotel == null) return List.of();

        return entityManager.createQuery(
                        "SELECT p FROM HotelPhoto p WHERE p.hotel.id = :hid " +
                                "ORDER BY p.mainPhoto DESC, p.score DESC", HotelPhoto.class)
                .setParameter("hid", hotelId)
                .getResultList()
                .stream()
                .map(photoMapper::toDto)
                .collect(Collectors.toList());
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

        stats.totalReviews = getEntityManager().createQuery(
                        "SELECT COUNT(r) FROM HotelReview r", Long.class)
                .getSingleResult();

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

    public List<HotelFacilityDto> getHotelFacilities(Long hotelId) {
        var hotel = findById(hotelId);
        if (hotel == null) return List.of();

        return entityManager.createQuery(
                        "SELECT f FROM HotelFacility f WHERE f.hotel.id = :hid ORDER BY f.name ASC",
                        HotelFacility.class)
                .setParameter("hid", hotelId)
                .getResultList()
                .stream()
                .map(facilityMapper::toDto)
                .collect(Collectors.toList());
    }


    public List<HotelRoomDto> getHotelRooms(Long hotelId) {
        var hotel = findById(hotelId);
        if (hotel == null) return List.of();

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

//    public List<HotelRoomDto> getHotelRooms(Long hotelId) {
//        var hotel = findById(hotelId);
//        if (hotel == null) {
//            return List.of();
//        }
//
//        return HotelRoom.find("hotel.id = ?1", Sort.by("roomName"), hotelId)
//                .list().stream()
//                .map(r -> {
//                    HotelRoom room = (HotelRoom) r;
//                    room.bedTypes = HotelRoomBedType.find("room.id = ?1", room.id).list();
//                    room.amenities = HotelRoomAmenity.find("room.id = ?1", Sort.by("sortOrder"), room.id).list();
//                    room.photos = HotelRoomPhoto.find("room.id = ?1", Sort.by("mainPhoto").descending().and("score").descending(), room.id).list();
//                    room.views = HotelRoomView.find("room.id = ?1", room.id).list();
//                    return roomMapper.toDto(room);
//                })
//                .collect(Collectors.toList());
//    }


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


    @Transactional
    public void updateHotelFacilities(Hotel hotel, List<CupidPropertyDto.Facility> facilityDtos) {
        hotel = entityManager.getReference(Hotel.class, hotel.id);

        // remove existing children while managed
        for (var existing : List.copyOf(hotel.facilities)) {
            hotel.facilities.remove(existing);
            entityManager.remove(existing);
        }
        entityManager.flush();

        if (facilityDtos == null) return;

        for (var f : facilityDtos) {
            var fac = new HotelFacility();
            fac.id = null;             // important: always null for new
            fac.hotel = hotel;
            fac.facilityId = f.facilityId;
            fac.name = f.name;
            hotel.facilities.add(fac);
        }
        entityManager.flush();
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

    @Transactional
    public void updateHotelRooms(Hotel hotel, List<CupidPropertyDto.Room> roomDtos) {
        hotel = entityManager.getReference(Hotel.class, hotel.id);

        // Remove existing rooms in-session (this cascades to photos/amenities/views/bedTypes)
        for (var existing : List.copyOf(hotel.rooms)) {
            hotel.rooms.remove(existing);
            entityManager.remove(existing);
        }
        entityManager.flush();

        if (roomDtos == null || roomDtos.isEmpty()) return;

        for (var dto : roomDtos) {
            HotelRoom room = new HotelRoom();
            room.id = null; // defensive
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
                    bt.id = null; // IMPORTANT
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
                    amenity.id = null; // IMPORTANT
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
                    photo.id = null; // IMPORTANT
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
                    view.id = null; // IMPORTANT
                    view.room = room;
                    view.viewId = v.id;
                    view.view = v.view;
                    room.views.add(view);
                }
            }

            hotel.rooms.add(room); // cascade=ALL will persist whole tree
        }

        // optional: flush here to catch problems early
        entityManager.flush();
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

    @Transactional
    public void updateHotelTranslations(Hotel hotel, CupidPropertyDto fr, CupidPropertyDto es) {
        hotel = entityManager.getReference(Hotel.class, hotel.id);

        if (fr != null) {
            var t = findByHotelAndLanguage(hotel, "fr");
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
            var t = findByHotelAndLanguage(hotel, "es");
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


    // Helper method to map room DTO to entity without persisting child entities
    private HotelRoom mapRoomFromDto(Hotel hotel, CupidPropertyDto.Room roomDto) {
        HotelRoom room = new HotelRoom();
        room.hotel = hotel;
        room.cupidRoomId = roomDto.id;
        room.roomName = roomDto.roomName;
        room.description = roomDto.description;
        room.roomSizeSquare = roomDto.roomSizeSquare != null ? roomDto.roomSizeSquare.intValue() : null;
        room.roomSizeUnit = roomDto.roomSizeUnit;
        room.maxAdults = roomDto.maxAdults;
        room.maxChildren = roomDto.maxChildren;
        room.maxOccupancy = roomDto.maxOccupancy;
        room.bedRelation = roomDto.bedRelation;

        // Process bed types
        if (roomDto.bedTypes != null) {
            for (var bedTypeDto : roomDto.bedTypes) {
                HotelRoomBedType bedType = new HotelRoomBedType();
                bedType.room = room;
                bedType.quantity = bedTypeDto.quantity;
                bedType.bedType = bedTypeDto.bedType;
                bedType.bedSize = bedTypeDto.bedSize;
                room.bedTypes.add(bedType); // Add to collection
            }
        }

        // Process room amenities
        if (roomDto.roomAmenities != null) {
            for (var amenityDto : roomDto.roomAmenities) {
                HotelRoomAmenity amenity = new HotelRoomAmenity();
                amenity.room = room;
                amenity.amenityId = amenityDto.amenitiesId;
                amenity.name = amenityDto.name;
                amenity.sortOrder = amenityDto.sort;
                room.amenities.add(amenity); // Add to collection
            }
        }

        // Process room photos
        if (roomDto.photos != null) {
            for (var photoDto : roomDto.photos) {
                HotelRoomPhoto photo = new HotelRoomPhoto();
                photo.room = room;
                photo.url = photoDto.url;
                photo.hdUrl = photoDto.hdUrl;
                photo.imageDescription = photoDto.imageDescription;
                photo.imageClass1 = photoDto.imageClass1;
                photo.imageClass2 = photoDto.imageClass2;
                photo.mainPhoto = photoDto.mainPhoto;
                photo.score = photoDto.score != null ? BigDecimal.valueOf(photoDto.score) : null;
                photo.classId = photoDto.classId;
                photo.classOrder = photoDto.classOrder;
                room.photos.add(photo); // Add to collection
            }
        }

        // Process room views
        if (roomDto.views != null) {
            for (var viewDto : roomDto.views) {
                HotelRoomView view = new HotelRoomView();
                view.room = room;
                view.viewId = viewDto.id;
                view.view = viewDto.view;
                room.views.add(view);
            }
        }

        return room;
    }
}
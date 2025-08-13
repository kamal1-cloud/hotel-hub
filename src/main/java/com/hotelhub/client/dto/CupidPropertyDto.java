package com.hotelhub.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class CupidPropertyDto {

    @JsonProperty("hotel_id")
    public Long hotelId;

    @JsonProperty("cupid_id")
    public Long cupidId;

    @JsonProperty("hotel_name")
    public String hotelName;

    @JsonProperty("main_image_th")
    public String mainImageThumbnail;

    @JsonProperty("hotel_type")
    public String hotelType;

    @JsonProperty("hotel_type_id")
    public Integer hotelTypeId;

    @JsonProperty("chain")
    public String chain;

    @JsonProperty("chain_id")
    public Integer chainId;

    @JsonProperty("latitude")
    public Double latitude;

    @JsonProperty("longitude")
    public Double longitude;

    @JsonProperty("phone")
    public String phone;

    @JsonProperty("fax")
    public String fax;

    @JsonProperty("email")
    public String email;

    @JsonProperty("address")
    public Address address;

    @JsonProperty("stars")
    public Integer stars;

    @JsonProperty("airport_code")
    public String airportCode;

    @JsonProperty("rating")
    public BigDecimal rating;

    @JsonProperty("review_count")
    public Integer reviewCount;

    @JsonProperty("checkin")
    public CheckinInfo checkin;

    @JsonProperty("parking")
    public String parking;

    @JsonProperty("group_room_min")
    public Integer groupRoomMin;

    @JsonProperty("child_allowed")
    public Boolean childAllowed;

    @JsonProperty("pets_allowed")
    public Boolean petsAllowed;

    @JsonProperty("photos")
    public List<Photo> photos;

    @JsonProperty("description")
    public String description;

    @JsonProperty("markdown_description")
    public String markdownDescription;

    @JsonProperty("important_info")
    public String importantInfo;

    @JsonProperty("facilities")
    public List<Facility> facilities;

    @JsonProperty("policies")
    public List<Policy> policies;

    @JsonProperty("rooms")
    public List<Room> rooms;

    public static class Address {
        @JsonProperty("address")
        public String address;

        @JsonProperty("city")
        public String city;

        @JsonProperty("state")
        public String state;

        @JsonProperty("country")
        public String country;

        @JsonProperty("postal_code")
        public String postalCode;
    }

    public static class CheckinInfo {
        @JsonProperty("checkin_start")
        public String checkinStart;

        @JsonProperty("checkin_end")
        public String checkinEnd;

        @JsonProperty("checkout")
        public String checkout;

        @JsonProperty("special_instructions")
        public String specialInstructions;

        @JsonProperty("instructions")
        public List<Instruction> instructions;
    }

    public static class Instruction {
        @JsonProperty("id")
        public Long id;

        @JsonProperty("instruction")
        public String instruction;
    }

    public static class Photo {
        @JsonProperty("url")
        public String url;

        @JsonProperty("hd_url")
        public String hdUrl;

        @JsonProperty("image_description")
        public String imageDescription;

        @JsonProperty("image_class1")
        public String imageClass1;

        @JsonProperty("image_class2")
        public String imageClass2;

        @JsonProperty("main_photo")
        public Boolean mainPhoto;

        @JsonProperty("score")
        public Double score;

        @JsonProperty("class_id")
        public Integer classId;

        @JsonProperty("class_order")
        public Integer classOrder;
    }

    public static class Facility {
        @JsonProperty("facility_id")
        public Integer facilityId;

        @JsonProperty("name")
        public String name;
    }

    public static class Policy {
        @JsonProperty("policy_type")
        public String policyType;

        @JsonProperty("name")
        public String name;

        @JsonProperty("description")
        public String description;

        @JsonProperty("child_allowed")
        public String childAllowed;

        @JsonProperty("pets_allowed")
        public String petsAllowed;

        @JsonProperty("parking")
        public String parking;
    }

    public static class Room {
        @JsonProperty("id")
        public Long id;

        @JsonProperty("room_name")
        public String roomName;

        @JsonProperty("description")
        public String description;

        @JsonProperty("room_size_square")
        public Double roomSizeSquare;

        @JsonProperty("room_size_unit")
        public String roomSizeUnit;

        @JsonProperty("hotel_id")
        public String hotelId;

        @JsonProperty("max_adults")
        public Integer maxAdults;

        @JsonProperty("max_children")
        public Integer maxChildren;

        @JsonProperty("max_occupancy")
        public Integer maxOccupancy;

        @JsonProperty("bed_relation")
        public String bedRelation;

        @JsonProperty("bed_types")
        public List<BedType> bedTypes;

        @JsonProperty("room_amenities")
        public List<RoomAmenity> roomAmenities;

        @JsonProperty("photos")
        public List<Photo> photos;

        @JsonProperty("views")
        public List<RoomView> views;
    }

    public static class BedType {
        @JsonProperty("quantity")
        public Integer quantity;

        @JsonProperty("bed_type")
        public String bedType;

        @JsonProperty("bed_size")
        public String bedSize;
    }

    public static class RoomAmenity {
        @JsonProperty("amenities_id")
        public Integer amenitiesId;

        @JsonProperty("name")
        public String name;

        @JsonProperty("sort")
        public Integer sort;
    }

    public static class RoomView {
        @JsonProperty("id")
        public Integer id;

        @JsonProperty("view")
        public String view;
    }
}
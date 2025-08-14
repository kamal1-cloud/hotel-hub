
## Entity Descriptions

### Core Entities

**HOTEL**: The main entity representing a hotel property with comprehensive information including contact details, location, ratings, and policies.

**HOTEL_ROOM**: Represents individual room types within a hotel with occupancy details and room characteristics.

### Hotel-Related Entities

**HOTEL_PHOTO**: Stores hotel images with metadata for display ordering and classification.

**HOTEL_FACILITY**: Lists available facilities and amenities at the hotel level.

**HOTEL_POLICY**: Contains hotel policies regarding children, pets, parking, and other rules.

**HOTEL_REVIEW**: Customer reviews and ratings with support for multiple languages and sources.

**HOTEL_TRANSLATION**: Multilingual support for hotel information in different languages.

### Room-Related Entities

**HOTEL_ROOM_BED_TYPE**: Defines the types and quantities of beds available in each room.

**HOTEL_ROOM_AMENITY**: Room-specific amenities and features.

**HOTEL_ROOM_PHOTO**: Images specific to each room type.

**HOTEL_ROOM_VIEW**: Describes the view available from each room (e.g., ocean view, city view).

## Key Design Features

- **Hierarchical Structure**: Two-level hierarchy with Hotel at the top and HotelRoom as the secondary level
- **Comprehensive Media Support**: Separate photo entities for both hotel and room levels
- **Internationalization**: Translation support for multiple languages
- **External System Integration**: Cupid ID fields for integration with external booking systems
- **Audit Trail**: Created/Updated timestamps on all entities
- **Flexible Policies**: Separate policy entity allows for extensible rule management
- **Review System**: Comprehensive review system with scoring and multi-source support
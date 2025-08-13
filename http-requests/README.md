# Hotel Hub API Testing

This directory contains organized HTTP request files for testing the Hotel Hub API endpoints.

## Files Structure

- **`hotel-ids.json`** - Contains all hotel IDs organized in batches for easy reference
- **`hotel-endpoints.http`** - Basic hotel listing, searching, and filtering endpoints
- **`hotel-details.http`** - Specific hotel detail endpoints (photos, facilities, reviews, translations)
- **`data-ingestion.http`** - Data ingestion endpoints with different batch sizes
- **`system-endpoints.http`** - Health checks, metrics, and system information endpoints

## How to Use

### In IntelliJ IDEA / VS Code
1. Install the **HTTP Client** plugin (built-in for IntelliJ IDEA Ultimate)
2. Open any `.http` file
3. Click the **▶️ Run** button next to each request
4. View responses in the tool window

### Command Line (curl examples)
```bash
# Get all hotels
curl http://localhost:8080/api/v1/hotels

# Get hotel statistics  
curl http://localhost:8080/api/v1/hotels/stats

# Ingest sample data
curl -X POST http://localhost:8080/api/v1/ingest \
  -H "Content-Type: application/json" \
  -d '[1641879, 317597, 1202743]'
```

## Testing Workflow

1. **Start with system checks:**
   - Run requests from `system-endpoints.http` to verify the API is running

2. **Ingest sample data:**
   - Use `data-ingestion.http` to start with the sample batch (3 hotels)
   - Monitor application logs for success/errors

3. **Test hotel endpoints:**
   - Use `hotel-endpoints.http` for basic operations
   - Use `hotel-details.http` for specific hotel data

4. **Scale up ingestion:**
   - Gradually increase batch sizes as needed
   - Monitor API rate limits and performance

## Important Notes

- **API Key Required:** Ensure `CUPID_API_KEY` environment variable is set
- **Hotel IDs:** Replace example IDs in hotel-details.http with actual IDs from your database
- **Rate Limits:** Be mindful of Cupid API rate limits when ingesting large batches
- **Performance:** Large ingestion requests may take several minutes to complete

## Environment Setup

```bash
export CUPID_API_KEY="your-actual-api-key"
docker compose up -d --build
```
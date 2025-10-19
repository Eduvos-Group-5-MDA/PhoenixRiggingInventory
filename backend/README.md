# Phoenix Rigging Inventory Backend

Node.js/Express REST API with PostgreSQL database for the Phoenix Rigging Inventory Android application.

## Prerequisites

- Node.js (v16 or higher)
- PostgreSQL (v12 or higher)
- npm or yarn

## Setup

### 1. Install Dependencies

```bash
cd backend
npm install
```

### 2. Configure PostgreSQL Database

Create a PostgreSQL database:

```bash
psql -U postgres
CREATE DATABASE phoenix_inventory;
\q
```

### 3. Configure Environment

Copy `.env.example` to `.env` and update with your database credentials:

```bash
cp .env.example .env
```

Edit `.env`:
```
DB_HOST=localhost
DB_PORT=5433
DB_NAME=phoenix_inventory
DB_USER=postgres
DB_PASSWORD=your_password
PORT=3000
NODE_ENV=development
```

### 4. Initialize Database Schema

Run the schema SQL file to create tables and insert sample data:

```bash
psql -U postgres -d phoenix_inventory -f database/schema.sql
```

Or use npm script:
```bash
npm run init-db
```

### 5. Start the Server

Development mode (with auto-reload):
```bash
npm run dev
```

Production mode:
```bash
npm start
```

The server will start on `http://localhost:3000`

## API Endpoints

### Items

- `GET /api/items` - Get all inventory items
- `GET /api/items/:id` - Get item by ID
- `POST /api/items` - Create new item
- `PUT /api/items/:id` - Update item
- `DELETE /api/items/:id` - Delete item
- `GET /api/items/stats/summary` - Get inventory statistics

### Users

- `GET /api/users` - Get all users
- `GET /api/users/:id` - Get user by ID
- `GET /api/users/current/me` - Get current user (demo)
- `POST /api/users` - Create new user
- `PUT /api/users/:id` - Update user
- `DELETE /api/users/:id` - Delete user

### Checkouts

- `GET /api/checkouts` - Get all checkout records
- `GET /api/checkouts/:id` - Get checkout record by ID
- `GET /api/checkouts/active` - Get active checkouts
- `GET /api/checkouts/checked-out-items` - Get checked out items with full details
- `GET /api/checkouts/overdue/:days` - Get items out longer than specified days
- `POST /api/checkouts/checkout` - Check out an item
- `POST /api/checkouts/checkin/:itemId` - Check in an item

### Health

- `GET /health` - Health check endpoint

## Example API Calls

### Check Out Item
```bash
curl -X POST http://localhost:3000/api/checkouts/checkout \
  -H "Content-Type: application/json" \
  -d '{
    "itemId": "item1",
    "userId": "user1",
    "notes": "Needed for project X"
  }'
```

### Check In Item
```bash
curl -X POST http://localhost:3000/api/checkouts/checkin/item1
```

### Get Statistics
```bash
curl http://localhost:3000/api/items/stats/summary
```

## Database Schema

### Tables

- **users** - User accounts with roles (Admin, Manager, Employee)
- **inventory_items** - Equipment and supplies inventory
- **checkout_records** - History of item checkouts/checkins

See `database/schema.sql` for complete schema definition.

## Android Integration

Update your Android app's network configuration to point to this backend:

```kotlin
// In your Retrofit configuration
const val BASE_URL = "http://10.0.2.2:3000/api/" // For Android emulator
// or
const val BASE_URL = "http://YOUR_LOCAL_IP:3000/api/" // For physical device
```

## Troubleshooting

### Connection Issues

- Verify PostgreSQL is running: `pg_isready`
- Check database credentials in `.env`
- Ensure database exists: `psql -U postgres -l`

### Port Already in Use

Change the PORT in `.env` file or kill the process using port 3000:

```bash
# Windows
netstat -ano | findstr :3000
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:3000 | xargs kill
```

## Development

Project structure:
```
backend/
├── config/          # Database configuration
├── models/          # Data models (InventoryItem, User, CheckoutRecord)
├── routes/          # API route handlers
├── database/        # SQL schema files
├── server.js        # Main application entry point
├── package.json     # Dependencies
└── .env            # Environment variables (not in git)
```

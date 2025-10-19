-- Phoenix Rigging Inventory Database Schema
-- PostgreSQL Database

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS checkout_records CASCADE;
DROP TABLE IF EXISTS inventory_items CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Users table
CREATE TABLE users (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL CHECK (role IN ('Admin', 'Manager', 'Employee')),
    password_hash TEXT NOT NULL,
    phone VARCHAR(50),
    company VARCHAR(255),
    driver_license BOOLEAN NOT NULL DEFAULT FALSE,
    employee_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Inventory items table
CREATE TABLE inventory_items (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    serial_id VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    condition VARCHAR(50) NOT NULL CHECK (condition IN ('Excellent', 'Good', 'Fair', 'Poor')),
    status VARCHAR(50) NOT NULL CHECK (status IN ('Available', 'Checked Out', 'Under Maintenance', 'Retired', 'Stolen', 'Lost', 'Damaged')),
    value DECIMAL(10, 2) NOT NULL DEFAULT 0.0,
    permanent_checkout BOOLEAN NOT NULL DEFAULT FALSE,
    permission_needed BOOLEAN NOT NULL DEFAULT FALSE,
    drivers_license_needed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Checkout records table
CREATE TABLE checkout_records (
    id VARCHAR(255) PRIMARY KEY,
    item_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    checked_out_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    checked_in_at TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (item_id) REFERENCES inventory_items(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_checkout_records_item_id ON checkout_records(item_id);
CREATE INDEX idx_checkout_records_user_id ON checkout_records(user_id);
CREATE INDEX idx_checkout_records_checked_in_at ON checkout_records(checked_in_at);
CREATE INDEX idx_inventory_items_status ON inventory_items(status);
CREATE INDEX idx_inventory_items_serial_id ON inventory_items(serial_id);

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger to automatically update updated_at
CREATE TRIGGER update_inventory_items_updated_at BEFORE UPDATE
    ON inventory_items FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Insert sample data
INSERT INTO users (id, name, email, role, password_hash, phone, company, driver_license, employee_id) VALUES
    ('user1', 'John Doe', 'stadlerkieran@gmail.com', 'Employee', '$2a$10$7EqJtq98hPqEX7fNLEnixe/LO8G3cV8Wp8p3D3S5aaJ6dHe4blY.G', '555-0101', 'Phoenix Rigging', TRUE, 'EMP-001'),
    ('user2', 'Jane Smith', 'jane.smith@example.com', 'Manager', '$2a$10$7EqJtq98hPqEX7fNLEnixe/LO8G3cV8Wp8p3D3S5aaJ6dHe4blY.G', '555-0102', 'Phoenix Rigging', FALSE, 'EMP-002'),
    ('user3', 'Mike Johnson', 'mike.j@example.com', 'Employee', '$2a$10$7EqJtq98hPqEX7fNLEnixe/LO8G3cV8Wp8p3D3S5aaJ6dHe4blY.G', '555-0103', 'Phoenix Rigging', FALSE, 'EMP-003');

INSERT INTO inventory_items (id, name, serial_id, description, condition, status, value, permanent_checkout, permission_needed, drivers_license_needed) VALUES
    ('item1', 'Safety Harness Type A', 'SH-001', 'Professional safety harness for rigging operations', 'Excellent', 'Available', 450.0, FALSE, FALSE, FALSE),
    ('item2', 'Carabiner Set (10pc)', 'CAR-102', 'Heavy-duty aluminum carabiners', 'Good', 'Checked Out', 350.0, FALSE, FALSE, FALSE),
    ('item3', 'Rigging Rope 50m', 'RR-203', 'Industrial grade rigging rope', 'Fair', 'Under Maintenance', 280.0, FALSE, FALSE, FALSE),
    ('item4', 'Pulley System', 'PS-304', 'Multi-point pulley rigging system', 'Excellent', 'Checked Out', 890.0, FALSE, TRUE, FALSE),
    ('item5', 'Damaged Cable', 'DC-405', 'Steel cable - damaged during operation', 'Poor', 'Damaged', 520.0, FALSE, FALSE, FALSE);

-- Insert sample checkout records
INSERT INTO checkout_records (id, item_id, user_id, checked_out_at, checked_in_at, notes) VALUES
    ('checkout1', 'item2', 'user1', CURRENT_TIMESTAMP - INTERVAL '5 days', NULL, ''),
    ('checkout2', 'item4', 'user2', CURRENT_TIMESTAMP - INTERVAL '35 days', NULL, '');

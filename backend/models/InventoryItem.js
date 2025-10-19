const pool = require('../config/database');
const { v4: uuidv4 } = require('uuid');

class InventoryItem {
    static async getAll() {
        const result = await pool.query(
            'SELECT * FROM inventory_items ORDER BY created_at DESC'
        );
        return result.rows.map(this.mapFromDb);
    }

    static async getById(id) {
        const result = await pool.query(
            'SELECT * FROM inventory_items WHERE id = $1',
            [id]
        );
        return result.rows.length > 0 ? this.mapFromDb(result.rows[0]) : null;
    }

    static async create(itemData) {
        const id = uuidv4();
        const {
            name,
            serialId,
            description,
            condition,
            status,
            value = 0.0,
            permanentCheckout = false,
            permissionNeeded = false,
            driversLicenseNeeded = false
        } = itemData;

        const result = await pool.query(
            `INSERT INTO inventory_items
            (id, name, serial_id, description, condition, status, value,
             permanent_checkout, permission_needed, drivers_license_needed)
            VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)
            RETURNING *`,
            [id, name, serialId, description, condition, status, value,
             permanentCheckout, permissionNeeded, driversLicenseNeeded]
        );
        return this.mapFromDb(result.rows[0]);
    }

    static async update(id, itemData) {
        const {
            name,
            serialId,
            description,
            condition,
            status,
            value,
            permanentCheckout,
            permissionNeeded,
            driversLicenseNeeded
        } = itemData;

        const result = await pool.query(
            `UPDATE inventory_items
            SET name = $2, serial_id = $3, description = $4, condition = $5,
                status = $6, value = $7, permanent_checkout = $8,
                permission_needed = $9, drivers_license_needed = $10
            WHERE id = $1
            RETURNING *`,
            [id, name, serialId, description, condition, status, value,
             permanentCheckout, permissionNeeded, driversLicenseNeeded]
        );
        return result.rows.length > 0 ? this.mapFromDb(result.rows[0]) : null;
    }

    static async delete(id) {
        const result = await pool.query(
            'DELETE FROM inventory_items WHERE id = $1 RETURNING *',
            [id]
        );
        return result.rows.length > 0;
    }

    static async getTotalValue() {
        const result = await pool.query(
            'SELECT COALESCE(SUM(value), 0) as total FROM inventory_items'
        );
        return parseFloat(result.rows[0].total);
    }

    static async getStolenLostDamagedValue() {
        const result = await pool.query(
            `SELECT COALESCE(SUM(value), 0) as total
             FROM inventory_items
             WHERE status IN ('Stolen', 'Lost', 'Damaged')`
        );
        return parseFloat(result.rows[0].total);
    }

    static async getStolenLostDamagedCount() {
        const result = await pool.query(
            `SELECT COUNT(*) as count
             FROM inventory_items
             WHERE status IN ('Stolen', 'Lost', 'Damaged')`
        );
        return parseInt(result.rows[0].count);
    }

    static async getCheckedOutCount() {
        const result = await pool.query(
            `SELECT COUNT(*) as count
             FROM inventory_items
             WHERE status = 'Checked Out'`
        );
        return parseInt(result.rows[0].count);
    }

    // Helper method to map database columns to camelCase
    static mapFromDb(row) {
        return {
            id: row.id,
            name: row.name,
            serialId: row.serial_id,
            description: row.description,
            condition: row.condition,
            status: row.status,
            value: parseFloat(row.value),
            permanentCheckout: row.permanent_checkout,
            permissionNeeded: row.permission_needed,
            driversLicenseNeeded: row.drivers_license_needed,
            createdAt: row.created_at,
            updatedAt: row.updated_at
        };
    }
}

module.exports = InventoryItem;

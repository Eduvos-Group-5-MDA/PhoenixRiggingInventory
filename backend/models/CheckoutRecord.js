const pool = require('../config/database');
const { v4: uuidv4 } = require('uuid');

class CheckoutRecord {
    static async getAll() {
        const result = await pool.query(
            'SELECT * FROM checkout_records ORDER BY checked_out_at DESC'
        );
        return result.rows.map(this.mapFromDb);
    }

    static async getById(id) {
        const result = await pool.query(
            'SELECT * FROM checkout_records WHERE id = $1',
            [id]
        );
        return result.rows.length > 0 ? this.mapFromDb(result.rows[0]) : null;
    }

    static async getActiveCheckouts() {
        const result = await pool.query(
            'SELECT * FROM checkout_records WHERE checked_in_at IS NULL ORDER BY checked_out_at DESC'
        );
        return result.rows.map(this.mapFromDb);
    }

    static async getCheckedOutItems() {
        const result = await pool.query(
            `SELECT
                cr.id as checkout_id,
                cr.checked_out_at,
                cr.checked_in_at,
                cr.notes,
                i.id as item_id,
                i.name as item_name,
                i.serial_id,
                i.description,
                i.condition,
                i.status,
                i.value,
                i.permanent_checkout,
                i.permission_needed,
                i.drivers_license_needed,
                i.created_at as item_created_at,
                i.updated_at as item_updated_at,
                u.id as user_id,
                u.name as user_name,
                u.email as user_email,
                u.role as user_role,
                u.created_at as user_created_at,
                EXTRACT(DAY FROM (CURRENT_TIMESTAMP - cr.checked_out_at)) as days_out
            FROM checkout_records cr
            JOIN inventory_items i ON cr.item_id = i.id
            JOIN users u ON cr.user_id = u.id
            WHERE cr.checked_in_at IS NULL
            ORDER BY cr.checked_out_at ASC`
        );

        return result.rows.map(row => ({
            item: {
                id: row.item_id,
                name: row.item_name,
                serialId: row.serial_id,
                description: row.description,
                condition: row.condition,
                status: row.status,
                value: parseFloat(row.value),
                permanentCheckout: row.permanent_checkout,
                permissionNeeded: row.permission_needed,
                driversLicenseNeeded: row.drivers_license_needed,
                createdAt: row.item_created_at,
                updatedAt: row.item_updated_at
            },
            user: {
                id: row.user_id,
                name: row.user_name,
                email: row.user_email,
                role: row.user_role,
                createdAt: row.user_created_at
            },
            checkoutRecord: {
                id: row.checkout_id,
                itemId: row.item_id,
                userId: row.user_id,
                checkedOutAt: row.checked_out_at,
                checkedInAt: row.checked_in_at,
                notes: row.notes
            },
            daysOut: parseInt(row.days_out)
        }));
    }

    static async getItemsOutLongerThan(days) {
        const result = await pool.query(
            `SELECT
                cr.id as checkout_id,
                cr.checked_out_at,
                cr.checked_in_at,
                cr.notes,
                i.id as item_id,
                i.name as item_name,
                i.serial_id,
                i.description,
                i.condition,
                i.status,
                i.value,
                i.permanent_checkout,
                i.permission_needed,
                i.drivers_license_needed,
                i.created_at as item_created_at,
                i.updated_at as item_updated_at,
                u.id as user_id,
                u.name as user_name,
                u.email as user_email,
                u.role as user_role,
                u.created_at as user_created_at,
                EXTRACT(DAY FROM (CURRENT_TIMESTAMP - cr.checked_out_at)) as days_out
            FROM checkout_records cr
            JOIN inventory_items i ON cr.item_id = i.id
            JOIN users u ON cr.user_id = u.id
            WHERE cr.checked_in_at IS NULL
                AND EXTRACT(DAY FROM (CURRENT_TIMESTAMP - cr.checked_out_at)) >= $1
            ORDER BY cr.checked_out_at ASC`,
            [days]
        );

        return result.rows.map(row => ({
            item: {
                id: row.item_id,
                name: row.item_name,
                serialId: row.serial_id,
                description: row.description,
                condition: row.condition,
                status: row.status,
                value: parseFloat(row.value),
                permanentCheckout: row.permanent_checkout,
                permissionNeeded: row.permission_needed,
                driversLicenseNeeded: row.drivers_license_needed,
                createdAt: row.item_created_at,
                updatedAt: row.item_updated_at
            },
            user: {
                id: row.user_id,
                name: row.user_name,
                email: row.user_email,
                role: row.user_role,
                createdAt: row.user_created_at
            },
            checkoutRecord: {
                id: row.checkout_id,
                itemId: row.item_id,
                userId: row.user_id,
                checkedOutAt: row.checked_out_at,
                checkedInAt: row.checked_in_at,
                notes: row.notes
            },
            daysOut: parseInt(row.days_out)
        }));
    }

    static async checkOutItem(itemId, userId, notes = '') {
        const client = await pool.connect();
        try {
            await client.query('BEGIN');

            // Check if item is available
            const itemResult = await client.query(
                'SELECT * FROM inventory_items WHERE id = $1',
                [itemId]
            );

            if (itemResult.rows.length === 0) {
                throw new Error('Item not found');
            }

            const item = itemResult.rows[0];
            if (item.status !== 'Available') {
                throw new Error('Item is not available for checkout');
            }

            // Update item status
            await client.query(
                'UPDATE inventory_items SET status = $1 WHERE id = $2',
                ['Checked Out', itemId]
            );

            // Create checkout record
            const id = uuidv4();
            const checkoutResult = await client.query(
                `INSERT INTO checkout_records (id, item_id, user_id, notes)
                 VALUES ($1, $2, $3, $4)
                 RETURNING *`,
                [id, itemId, userId, notes]
            );

            await client.query('COMMIT');
            return this.mapFromDb(checkoutResult.rows[0]);
        } catch (error) {
            await client.query('ROLLBACK');
            throw error;
        } finally {
            client.release();
        }
    }

    static async checkInItem(itemId) {
        const client = await pool.connect();
        try {
            await client.query('BEGIN');

            // Find active checkout record
            const checkoutResult = await client.query(
                'SELECT * FROM checkout_records WHERE item_id = $1 AND checked_in_at IS NULL',
                [itemId]
            );

            if (checkoutResult.rows.length === 0) {
                throw new Error('No active checkout found for this item');
            }

            // Update checkout record
            await client.query(
                'UPDATE checkout_records SET checked_in_at = CURRENT_TIMESTAMP WHERE id = $1',
                [checkoutResult.rows[0].id]
            );

            // Update item status
            await client.query(
                'UPDATE inventory_items SET status = $1 WHERE id = $2',
                ['Available', itemId]
            );

            await client.query('COMMIT');
            return true;
        } catch (error) {
            await client.query('ROLLBACK');
            throw error;
        } finally {
            client.release();
        }
    }

    static mapFromDb(row) {
        return {
            id: row.id,
            itemId: row.item_id,
            userId: row.user_id,
            checkedOutAt: row.checked_out_at,
            checkedInAt: row.checked_in_at,
            notes: row.notes
        };
    }
}

module.exports = CheckoutRecord;

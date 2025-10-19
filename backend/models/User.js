const pool = require('../config/database');
const { v4: uuidv4 } = require('uuid');

class User {
    static async getAll() {
        const result = await pool.query(
            'SELECT * FROM users ORDER BY created_at DESC'
        );
        return result.rows.map(this.mapFromDb);
    }

    static async getById(id) {
        const result = await pool.query(
            'SELECT * FROM users WHERE id = $1',
            [id]
        );
        return result.rows.length > 0 ? this.mapFromDb(result.rows[0]) : null;
    }

    static async getByEmail(email) {
        const result = await pool.query(
            'SELECT * FROM users WHERE email = $1',
            [email]
        );
        return result.rows.length > 0 ? this.mapFromDb(result.rows[0]) : null;
    }

    static async create(userData) {
        const id = uuidv4();
        const { name, email, role } = userData;

        const result = await pool.query(
            `INSERT INTO users (id, name, email, role)
             VALUES ($1, $2, $3, $4)
             RETURNING *`,
            [id, name, email, role]
        );
        return this.mapFromDb(result.rows[0]);
    }

    static async update(id, userData) {
        const { name, email, role } = userData;

        const result = await pool.query(
            `UPDATE users
             SET name = $2, email = $3, role = $4
             WHERE id = $1
             RETURNING *`,
            [id, name, email, role]
        );
        return result.rows.length > 0 ? this.mapFromDb(result.rows[0]) : null;
    }

    static async delete(id) {
        const result = await pool.query(
            'DELETE FROM users WHERE id = $1 RETURNING *',
            [id]
        );
        return result.rows.length > 0;
    }

    static async getCurrent() {
        // For demo purposes, returns the first user
        // In production, this would be based on authentication
        const result = await pool.query(
            'SELECT * FROM users ORDER BY created_at LIMIT 1'
        );
        return result.rows.length > 0 ? this.mapFromDb(result.rows[0]) : null;
    }

    static mapFromDb(row) {
        return {
            id: row.id,
            name: row.name,
            email: row.email,
            role: row.role,
            createdAt: row.created_at
        };
    }
}

module.exports = User;

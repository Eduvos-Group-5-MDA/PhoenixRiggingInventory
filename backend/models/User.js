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
        const id = userData.id || uuidv4();
        const {
            name,
            email,
            role = 'Employee',
            passwordHash,
            phone = null,
            company = null,
            driverLicense = false,
            employeeId = null
        } = userData;

        const result = await pool.query(
            `INSERT INTO users (id, name, email, role, password_hash, phone, company, driver_license, employee_id)
             VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
             RETURNING *`,
            [id, name, email, role, passwordHash, phone, company, driverLicense, employeeId]
        );
        return this.mapFromDb(result.rows[0]);
    }

    static async update(id, userData) {
        const {
            name,
            email,
            role,
            phone = null,
            company = null,
            driverLicense = false,
            employeeId = null,
            passwordHash
        } = userData;

        const fields = [
            name,
            email,
            role,
            phone,
            company,
            driverLicense,
            employeeId
        ];

        let query = `UPDATE users
             SET name = $2, email = $3, role = $4,
                 phone = $5, company = $6, driver_license = $7, employee_id = $8`;
        const values = [id, ...fields];

        if (passwordHash) {
            query += `, password_hash = $9`;
            values.push(passwordHash);
        }

        query += ` WHERE id = $1 RETURNING *`;

        const result = await pool.query(query, values);
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
            phone: row.phone,
            company: row.company,
            driverLicense: row.driver_license,
            employeeId: row.employee_id,
            createdAt: row.created_at
        };
    }

    static async verifyCredentials(email, password, bcrypt) {
        const user = await this.getByEmail(email);
        if (!user) {
            return null;
        }

        const result = await pool.query(
            'SELECT password_hash FROM users WHERE id = $1',
            [user.id]
        );

        if (result.rows.length === 0) {
            return null;
        }

        const passwordHash = result.rows[0].password_hash;
        const isValid = await bcrypt.compare(password, passwordHash);
        return isValid ? user : null;
    }
}

module.exports = User;

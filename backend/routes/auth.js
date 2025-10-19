const express = require('express');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const User = require('../models/User');

const router = express.Router();

function createToken(user) {
    const payload = { userId: user.id, role: user.role };
    const secret = process.env.JWT_SECRET || 'dev-secret';
    return jwt.sign(payload, secret, { expiresIn: '7d' });
}

// Register new user
router.post('/register', async (req, res) => {
    try {
        const {
            firstName,
            lastName,
            email,
            password,
            role = 'Employee',
            phone,
            company,
            hasDriverLicense = false,
            employeeId
        } = req.body;

        if (!firstName || !lastName || !email || !password) {
            return res.status(400).json({ error: 'Missing required fields' });
        }

        const existing = await User.getByEmail(email);
        if (existing) {
            return res.status(409).json({ error: 'Email already registered' });
        }

        const passwordHash = await bcrypt.hash(password, 10);
        const name = `${firstName} ${lastName}`.trim();

        const user = await User.create({
            name,
            email,
            role,
            passwordHash,
            phone,
            company,
            driverLicense: hasDriverLicense,
            employeeId
        });

        const token = createToken(user);
        res.status(201).json({ token, user });
    } catch (error) {
        console.error('Error registering user:', error);
        res.status(500).json({ error: 'Failed to register user' });
    }
});

// Login existing user
router.post('/login', async (req, res) => {
    try {
        const { email, password } = req.body;
        if (!email || !password) {
            return res.status(400).json({ error: 'Email and password are required' });
        }

        const user = await User.verifyCredentials(email, password, bcrypt);
        if (!user) {
            return res.status(401).json({ error: 'Invalid email or password' });
        }

        const token = createToken(user);
        res.json({ token, user });
    } catch (error) {
        console.error('Error logging in:', error);
        res.status(500).json({ error: 'Failed to login' });
    }
});

module.exports = router;

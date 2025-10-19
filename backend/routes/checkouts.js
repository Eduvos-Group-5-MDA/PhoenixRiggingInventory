const express = require('express');
const router = express.Router();
const CheckoutRecord = require('../models/CheckoutRecord');

// Get all checkout records
router.get('/', async (req, res) => {
    try {
        const records = await CheckoutRecord.getAll();
        res.json(records);
    } catch (error) {
        console.error('Error fetching checkout records:', error);
        res.status(500).json({ error: 'Failed to fetch checkout records' });
    }
});

// Get active checkouts
router.get('/active', async (req, res) => {
    try {
        const records = await CheckoutRecord.getActiveCheckouts();
        res.json(records);
    } catch (error) {
        console.error('Error fetching active checkouts:', error);
        res.status(500).json({ error: 'Failed to fetch active checkouts' });
    }
});

// Get checked out items with details
router.get('/checked-out-items', async (req, res) => {
    try {
        const items = await CheckoutRecord.getCheckedOutItems();
        res.json(items);
    } catch (error) {
        console.error('Error fetching checked out items:', error);
        res.status(500).json({ error: 'Failed to fetch checked out items' });
    }
});

// Get items out longer than X days
router.get('/overdue/:days', async (req, res) => {
    try {
        const days = parseInt(req.params.days);
        const items = await CheckoutRecord.getItemsOutLongerThan(days);
        res.json(items);
    } catch (error) {
        console.error('Error fetching overdue items:', error);
        res.status(500).json({ error: 'Failed to fetch overdue items' });
    }
});

// Check out item
router.post('/checkout', async (req, res) => {
    try {
        const { itemId, userId, notes } = req.body;
        const record = await CheckoutRecord.checkOutItem(itemId, userId, notes);
        res.status(201).json(record);
    } catch (error) {
        console.error('Error checking out item:', error);
        res.status(400).json({ error: error.message || 'Failed to check out item' });
    }
});

// Check in item
router.post('/checkin/:itemId', async (req, res) => {
    try {
        const result = await CheckoutRecord.checkInItem(req.params.itemId);
        if (result) {
            res.json({ message: 'Item checked in successfully' });
        } else {
            res.status(400).json({ error: 'Failed to check in item' });
        }
    } catch (error) {
        console.error('Error checking in item:', error);
        res.status(400).json({ error: error.message || 'Failed to check in item' });
    }
});

// Get checkout record by ID
router.get('/:id', async (req, res) => {
    try {
        const record = await CheckoutRecord.getById(req.params.id);
        if (record) {
            res.json(record);
        } else {
            res.status(404).json({ error: 'Checkout record not found' });
        }
    } catch (error) {
        console.error('Error fetching checkout record:', error);
        res.status(500).json({ error: 'Failed to fetch checkout record' });
    }
});

module.exports = router;

const express = require('express');
const router = express.Router();
const InventoryItem = require('../models/InventoryItem');

// Get all items
router.get('/', async (req, res) => {
    try {
        const items = await InventoryItem.getAll();
        res.json(items);
    } catch (error) {
        console.error('Error fetching items:', error);
        res.status(500).json({ error: 'Failed to fetch items' });
    }
});

// Get item by ID
router.get('/:id', async (req, res) => {
    try {
        const item = await InventoryItem.getById(req.params.id);
        if (item) {
            res.json(item);
        } else {
            res.status(404).json({ error: 'Item not found' });
        }
    } catch (error) {
        console.error('Error fetching item:', error);
        res.status(500).json({ error: 'Failed to fetch item' });
    }
});

// Create new item
router.post('/', async (req, res) => {
    try {
        const item = await InventoryItem.create(req.body);
        res.status(201).json(item);
    } catch (error) {
        console.error('Error creating item:', error);
        res.status(500).json({ error: 'Failed to create item' });
    }
});

// Update item
router.put('/:id', async (req, res) => {
    try {
        const item = await InventoryItem.update(req.params.id, req.body);
        if (item) {
            res.json(item);
        } else {
            res.status(404).json({ error: 'Item not found' });
        }
    } catch (error) {
        console.error('Error updating item:', error);
        res.status(500).json({ error: 'Failed to update item' });
    }
});

// Delete item
router.delete('/:id', async (req, res) => {
    try {
        const deleted = await InventoryItem.delete(req.params.id);
        if (deleted) {
            res.json({ message: 'Item deleted successfully' });
        } else {
            res.status(404).json({ error: 'Item not found' });
        }
    } catch (error) {
        console.error('Error deleting item:', error);
        res.status(500).json({ error: 'Failed to delete item' });
    }
});

// Get stats
router.get('/stats/summary', async (req, res) => {
    try {
        const [totalValue, stolenLostDamagedValue, stolenLostDamagedCount, checkedOutCount] =
            await Promise.all([
                InventoryItem.getTotalValue(),
                InventoryItem.getStolenLostDamagedValue(),
                InventoryItem.getStolenLostDamagedCount(),
                InventoryItem.getCheckedOutCount()
            ]);

        res.json({
            totalValue,
            stolenLostDamagedValue,
            stolenLostDamagedCount,
            checkedOutCount
        });
    } catch (error) {
        console.error('Error fetching stats:', error);
        res.status(500).json({ error: 'Failed to fetch stats' });
    }
});

module.exports = router;

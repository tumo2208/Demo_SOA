const express = require('express');
const mysql = require("mysql2/promise");

const cors = require('cors');
const app = express();

app.use(cors());
app.use(express.json());

const pool = mysql.createPool({
    host: "localhost",
    user: "root",
    password: "naksu2204",
    database: "ballon",
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0,
});

app.get("/offset", async (req, res) => {
    let offset = parseInt(req.query.offset) || 0;
    let limit = parseInt(req.query.limit) || 10;
    if (offset < 0) offset = 0;
    if (limit < 1) limit = 1;

    const [rows] = await pool.query(
        `SELECT * FROM players ORDER BY no_rank LIMIT ${limit} OFFSET ${offset}`
    );

    const [[{ total }]] = await pool.query(`SELECT COUNT(*) as total FROM players`);

    res.json({ total, offset, limit, data: rows });
});

app.get("/page", async (req, res) => {
    let page = parseInt(req.query.page) || 1;
    let size = parseInt(req.query.size) || 10;
    if (page < 1) page = 1;
    if (size < 1) size = 1;

    const offset = (page - 1) * size;

    const [rows] = await pool.query(
        `SELECT * FROM players ORDER BY no_rank LIMIT ${size} OFFSET ${offset}`
    );

    const [[{ total }]] = await pool.query(`SELECT COUNT(*) as total FROM players`);

    res.json({ total, page, size, data: rows });
});

app.get("/cursor", async (req, res) => {
    let cursor = parseInt(req.query.cursor) || 0;
    let limit = parseInt(req.query.limit) || 10;
    if (limit < 1) limit = 1;

    const [rows] = await pool.query(
        `SELECT * FROM players WHERE no_rank > ${cursor} ORDER BY no_rank ASC LIMIT ${limit}`
    );

    const [[{ total }]] = await pool.query(`SELECT COUNT(*) as total FROM players`);

    const nextCursor = rows.length > 0 ? rows[rows.length - 1].rank : null;

    res.json({ total, cursor, limit, nextCursor, data: rows });
});

app.listen(3000, () => {
    console.log(`Server running at http://localhost:3000`);
});
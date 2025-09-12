const express = require("express");
const cors = require("cors");
const app = express();

app.use(cors());
app.use(express.json());

const swaggerUi = require("swagger-ui-express");
const swaggerJsdoc = require("swagger-jsdoc");

const options = {
    definition: {
        openapi: "3.0.0",
        info: { title: "My API", version: "1.0.0" }
    },
    apis: ["./index.js"], // có thể viết docs dạng JSDoc trong code
};

const swaggerSpec = swaggerJsdoc(options);

app.use("/api-docs", swaggerUi.serve, swaggerUi.setup(swaggerSpec));

let books = [
    { id: 1, title: "Book A", author: "Alice" },
    { id: 2, title: "Book B", author: "Bob" }
];

/**
 * @swagger
 * /books:
 *   get:
 *     summary: Lấy danh sách tất cả sách
 *     responses:
 *       200:
 *         description: Trả về danh sách sách
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 $ref: '#/components/schemas/Book'
 */
// GET all books
app.get("/books", (req, res) => {
    res.status(200).json(books);
});

/**
 * @swagger
 * /books/{id}:
 *   get:
 *     summary: Lấy thông tin 1 sách theo ID
 *     parameters:
 *       - in: path
 *         name: id
 *         schema:
 *           type: integer
 *         required: true
 *     responses:
 *       200:
 *         description: Thành công
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Book'
 *       404:
 *         description: Không tìm thấy
 */
// GET one book
app.get("/books/:id", (req, res) => {
    const book = books.find(b => b.id === parseInt(req.params.id));
    if (!book) return res.status(404).json({ message: "Book not found" });
    res.status(200).json(book);
});

/**
 * @swagger
 * /books:
 *   post:
 *     summary: Tạo mới 1 sách
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/Book'
 *     responses:
 *       201:
 *         description: Sách được tạo thành công
 */
// POST create new book
app.post("/books", (req, res) => {
    const newBook = {
        id: books.length + 1,
        title: req.body.title,
        author: req.body.author
    };
    books.push(newBook);
    res.status(201).json(newBook);
});

/**
 * @swagger
 * /books/{id}:
 *   put:
 *     summary: Cập nhật toàn bộ thông tin 1 sách
 *     parameters:
 *       - in: path
 *         name: id
 *         schema:
 *           type: integer
 *         required: true
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/Book'
 *     responses:
 *       200:
 *         description: Cập nhật thành công
 *       404:
 *         description: Không tìm thấy
 */
// PUT update book
app.put("/books/:id", (req, res) => {
    const book = books.find(b => b.id === parseInt(req.params.id));
    if (!book) return res.status(404).json({ message: "Book not found" });

    book.title = req.body.title;
    book.author = req.body.author;
    res.status(200).json(book);
});

/**
 * @swagger
 * /books/{id}:
 *   patch:
 *     summary: Cập nhật 1 phần thông tin sách
 *     parameters:
 *       - in: path
 *         name: id
 *         schema:
 *           type: integer
 *         required: true
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               title:
 *                 type: string
 *                 example: New Title
 *               author:
 *                 type: string
 *                 example: New Author
 *     responses:
 *       200:
 *         description: Cập nhật thành công
 *       404:
 *         description: Không tìm thấy
 */
// PATCH update partial book
app.patch("/books/:id", (req, res) => {
    const book = books.find(b => b.id === parseInt(req.params.id));
    if (!book) return res.status(404).json({ message: "Book not found" });

    if (req.body.title !== undefined) book.title = req.body.title;
    if (req.body.author !== undefined) book.author = req.body.author;

    res.json(book);
});

/**
 * @swagger
 * /books/{id}:
 *   delete:
 *     summary: Xoá 1 sách theo ID
 *     parameters:
 *       - in: path
 *         name: id
 *         schema:
 *           type: integer
 *         required: true
 *     responses:
 *       200:
 *         description: Xoá thành công
 *       404:
 *         description: Không tìm thấy
 */
// DELETE book
app.delete("/books/:id", (req, res) => {
    books = books.filter(b => b.id !== parseInt(req.params.id));
    res.status(204).send();
});

app.listen(3000, () => console.log("Server running at http://localhost:3000"));

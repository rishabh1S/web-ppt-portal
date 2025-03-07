// index.js
import express from 'express';
import dotenv from 'dotenv';
import cors from 'cors'; // Import the cors package
import convertRouter from './routes/convert.js';

// Load environment variables
dotenv.config();

const app = express();
const port = process.env.PORT || 3000;


app.use(cors());

// Middleware to parse JSON
app.use(express.json());

// Routes
app.use('/convert', convertRouter);

// Start the server
app.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
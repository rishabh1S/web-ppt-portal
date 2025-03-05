
// db.js
import pg from 'pg';
import dotenv from 'dotenv';

// Load environment variables from .env file
dotenv.config();

// Create a connection pool
const pool = new pg.Pool({
  connectionString: process.env.NEONDB_CONNECTION_STRING, // Use the connection string from .env
  ssl: {
    rejectUnauthorized: false, // Required for NeonDB's SSL connection
  },
});

// Test the connection
pool.query('SELECT NOW()', (err, res) => {
  if (err) {
    console.error('Error connecting to NeonDB:', err);
  } else {
    console.log('Connected to NeonDB:', res.rows[0].now);
  }
});

export default pool;
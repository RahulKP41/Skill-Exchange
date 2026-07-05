const mysql = require("mysql2/promise");

const pool = mysql.createPool({
  host: process.env.DB_HOST || "localhost",
  port: Number(process.env.DB_PORT || 3306),
  database: process.env.DB_NAME || "skill_exchange",
  user: process.env.DB_USER || "skill_user",
  password: process.env.DB_PASSWORD || "skill_pass",
  waitForConnections: true,
  connectionLimit: 10
});

module.exports = pool;


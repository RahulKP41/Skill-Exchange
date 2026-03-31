require("dotenv").config();

const http = require("http");
const db = require("./config/db");
const { buildApp } = require("./app");
const { registerSocketServer } = require("./sockets/socketServer");

const config = {
  port: Number(process.env.PORT || 3001),
  jwtSecret: process.env.JWT_SECRET || "super-secret-access-key-change-me",
  corsOrigin: process.env.CORS_ORIGIN || "http://localhost:8080"
};

const app = buildApp(db, config);
const server = http.createServer(app);

registerSocketServer(server, db, config);

server.listen(config.port, () => {
  console.log(`Node gateway listening on port ${config.port}`);
});


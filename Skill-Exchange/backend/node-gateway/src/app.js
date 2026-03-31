const express = require("express");
const cors = require("cors");
const { authMiddleware } = require("./middleware");
const { buildMessageRouter } = require("./routes/messages");

function buildApp(db, config) {
  const app = express();
  app.use(cors({ origin: config.corsOrigin, credentials: true }));
  app.use(express.json());

  app.get("/health", (req, res) => {
    res.json({ status: "ok" });
  });

  app.use(authMiddleware(config.jwtSecret));
  app.use(buildMessageRouter(db));

  app.use((error, req, res, next) => {
    if (res.headersSent) {
      return next(error);
    }

    return res.status(500).json({
      message: error.message || "Unexpected gateway error."
    });
  });

  return app;
}

module.exports = { buildApp };


const express = require("express");
const { ensureRoom, findRequestForUser, listMessages } = require("../services/chatService");

function buildMessageRouter(db) {
  const router = express.Router();

  router.get("/requests/:requestId/messages", async (req, res, next) => {
    try {
      const requestId = Number(req.params.requestId);
      const request = await findRequestForUser(db, requestId, req.user.id);
      if (!request) {
        return res.status(403).json({ message: "You do not have access to this chat room." });
      }

      const room = await ensureRoom(db, requestId);
      const messages = await listMessages(db, room.id);
      return res.json({ room, messages });
    } catch (error) {
      return next(error);
    }
  });

  return router;
}

module.exports = { buildMessageRouter };


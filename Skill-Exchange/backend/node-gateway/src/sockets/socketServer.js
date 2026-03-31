const { Server } = require("socket.io");
const { verifyToken } = require("../services/authService");
const { createMessage, ensureRoom, findRequestForUser, listMessages, markRoomSeen } = require("../services/chatService");

function registerSocketServer(httpServer, db, options) {
  const onlineUsers = new Set();

  const io = new Server(httpServer, {
    cors: {
      origin: options.corsOrigin,
      credentials: true
    }
  });

  io.use((socket, next) => {
    const raw = socket.handshake.auth?.token || socket.handshake.headers.authorization?.replace("Bearer ", "");
    if (!raw) {
      return next(new Error("Authentication token is required."));
    }

    const payload = verifyToken(raw, options.jwtSecret);
    if (!payload) {
      return next(new Error("Invalid token."));
    }

    socket.user = {
      id: Number(payload.sub),
      email: payload.email,
      role: payload.role
    };
    return next();
  });

  io.on("connection", (socket) => {
    onlineUsers.add(socket.user.id);
    io.emit("presence:update", { userId: socket.user.id, status: "online" });

    socket.on("chat:join", async ({ requestId }) => {
      const request = await findRequestForUser(db, Number(requestId), socket.user.id);
      if (!request) {
        socket.emit("chat:error", { message: "Access denied for this room." });
        return;
      }

      const room = await ensureRoom(db, Number(requestId));
      socket.join(`request:${requestId}`);
      socket.emit("chat:joined", { requestId: Number(requestId), roomId: room.id });
    });

    socket.on("chat:history", async ({ requestId }) => {
      const request = await findRequestForUser(db, Number(requestId), socket.user.id);
      if (!request) {
        socket.emit("chat:error", { message: "Access denied for this room." });
        return;
      }

      const room = await ensureRoom(db, Number(requestId));
      const messages = await listMessages(db, room.id);
      socket.emit("chat:history", { requestId: Number(requestId), messages });
    });

    socket.on("chat:message", async ({ requestId, content }) => {
      if (!content || !String(content).trim()) {
        socket.emit("chat:error", { message: "Message content is required." });
        return;
      }

      const request = await findRequestForUser(db, Number(requestId), socket.user.id);
      if (!request) {
        socket.emit("chat:error", { message: "Access denied for this room." });
        return;
      }

      const room = await ensureRoom(db, Number(requestId));
      const message = await createMessage(db, room.id, socket.user.id, String(content).trim());
      io.to(`request:${requestId}`).emit("chat:message", {
        requestId: Number(requestId),
        message
      });
      io.to(`request:${requestId}`).emit("notification:new", {
        type: "chat",
        requestId: Number(requestId),
        senderId: socket.user.id
      });
    });

    socket.on("chat:typing", ({ requestId, isTyping }) => {
      socket.to(`request:${requestId}`).emit("chat:typing", {
        requestId: Number(requestId),
        userId: socket.user.id,
        isTyping: Boolean(isTyping)
      });
    });

    socket.on("chat:seen", async ({ requestId }) => {
      const request = await findRequestForUser(db, Number(requestId), socket.user.id);
      if (!request) {
        return;
      }

      const room = await ensureRoom(db, Number(requestId));
      await markRoomSeen(db, room.id, socket.user.id);
      io.to(`request:${requestId}`).emit("chat:seen", {
        requestId: Number(requestId),
        userId: socket.user.id
      });
    });

    socket.on("disconnect", () => {
      onlineUsers.delete(socket.user.id);
      io.emit("presence:update", { userId: socket.user.id, status: "offline" });
    });
  });

  return io;
}

module.exports = { registerSocketServer };


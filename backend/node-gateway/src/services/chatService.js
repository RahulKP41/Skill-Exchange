async function findRequestForUser(db, requestId, userId) {
  const [rows] = await db.execute(
    `
      SELECT er.id, er.sender_id, er.receiver_id, er.status
      FROM exchange_requests er
      WHERE er.id = ? AND (er.sender_id = ? OR er.receiver_id = ?)
    `,
    [requestId, userId, userId]
  );
  return rows[0] || null;
}

async function ensureRoom(db, requestId) {
  const [existing] = await db.execute("SELECT id, request_id, created_at FROM chat_rooms WHERE request_id = ?", [requestId]);
  if (existing.length > 0) {
    return existing[0];
  }

  const [insert] = await db.execute("INSERT INTO chat_rooms (request_id) VALUES (?)", [requestId]);
  const [created] = await db.execute("SELECT id, request_id, created_at FROM chat_rooms WHERE id = ?", [insert.insertId]);
  return created[0];
}

async function listMessages(db, roomId) {
  const [rows] = await db.execute(
    `
      SELECT id, room_id AS roomId, sender_id AS senderId, content, is_seen AS isSeen, created_at AS createdAt
      FROM chat_messages
      WHERE room_id = ?
      ORDER BY created_at ASC
    `,
    [roomId]
  );
  return rows;
}

async function createMessage(db, roomId, senderId, content) {
  const [insert] = await db.execute(
    "INSERT INTO chat_messages (room_id, sender_id, content, is_seen) VALUES (?, ?, ?, FALSE)",
    [roomId, senderId, content]
  );
  const [rows] = await db.execute(
    `
      SELECT id, room_id AS roomId, sender_id AS senderId, content, is_seen AS isSeen, created_at AS createdAt
      FROM chat_messages
      WHERE id = ?
    `,
    [insert.insertId]
  );
  return rows[0];
}

async function markRoomSeen(db, roomId, userId) {
  await db.execute(
    `
      UPDATE chat_messages
      SET is_seen = TRUE
      WHERE room_id = ? AND sender_id <> ?
    `,
    [roomId, userId]
  );
}

module.exports = {
  findRequestForUser,
  ensureRoom,
  listMessages,
  createMessage,
  markRoomSeen
};


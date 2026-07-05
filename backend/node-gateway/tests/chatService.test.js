const assert = require("assert");
const { findRequestForUser, ensureRoom } = require("../src/services/chatService");

async function testFindRequestForUser() {
  const db = {
    async execute(query, params) {
      if (query.includes("FROM exchange_requests")) {
        return [[{ id: params[0], sender_id: params[1], receiver_id: 9, status: "ACCEPTED" }]];
      }
      throw new Error("Unexpected query in testFindRequestForUser");
    }
  };

  const request = await findRequestForUser(db, 1, 5);

  assert(request, "Expected accessible request");
  assert.strictEqual(request.id, 1);
  console.log("- findRequestForUser returns an accessible request");
}

async function testEnsureRoomCreatesRoom() {
  let created = false;
  const db = {
    async execute(query, params) {
      if (query.startsWith("SELECT id, request_id, created_at FROM chat_rooms WHERE request_id = ?")) {
        return created
          ? [[{ id: 3, request_id: params[0], created_at: "2026-04-19T00:00:00Z" }]]
          : [[]];
      }
      if (query.startsWith("INSERT INTO chat_rooms")) {
        created = true;
        return [{ insertId: 3 }];
      }
      if (query.startsWith("SELECT id, request_id, created_at FROM chat_rooms WHERE id = ?")) {
        return [[{ id: params[0], request_id: 4, created_at: "2026-04-19T00:00:00Z" }]];
      }
      throw new Error("Unexpected query in testEnsureRoomCreatesRoom");
    }
  };

  const room = await ensureRoom(db, 4);

  assert(room, "Expected created room");
  assert.strictEqual(room.id, 3);
  assert.strictEqual(room.request_id, 4);
  console.log("- ensureRoom creates a room when none exists");
}

(async () => {
  await testFindRequestForUser();
  await testEnsureRoomCreatesRoom();
})().catch((error) => {
  console.error(error);
  process.exit(1);
});

const test = require("node:test");
const assert = require("node:assert/strict");
const { ensureRoom, findRequestForUser } = require("../src/services/chatService");

test("findRequestForUser finds a request when the user is a participant", async () => {
  let calls = 0;
  const db = {
    async execute() {
      calls += 1;
      return [[{ id: 4, sender_id: 1, receiver_id: 2, status: "ACCEPTED" }]];
    }
  };

  const request = await findRequestForUser(db, 4, 1);
  assert.equal(request.id, 4);
  assert.equal(calls, 1);
});

test("ensureRoom creates a room when none exists", async () => {
  const responses = [
    [[]],
    [{ insertId: 7 }],
    [[{ id: 7, request_id: 4, created_at: "2026-03-20T00:00:00Z" }]]
  ];
  let calls = 0;
  const db = {
    async execute() {
      const response = responses[calls];
      calls += 1;
      return response;
    }
  };

  const room = await ensureRoom(db, 4);
  assert.equal(room.id, 7);
  assert.equal(calls, 3);
});

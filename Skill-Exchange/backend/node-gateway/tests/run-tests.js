const assert = require("node:assert/strict");
const jwt = require("jsonwebtoken");
const { verifyToken } = require("../src/services/authService");
const { ensureRoom, findRequestForUser } = require("../src/services/chatService");

async function run() {
  const results = [];

  try {
    const token = jwt.sign({ email: "maya@test.dev", role: "USER" }, "test-secret-test-secret-test-secret", {
      subject: "42"
    });
    const payload = verifyToken(token, "test-secret-test-secret-test-secret");
    assert.equal(payload.sub, "42");
    assert.equal(payload.email, "maya@test.dev");
    results.push("verifyToken returns payload for a valid token");
  } catch (error) {
    fail("verifyToken returns payload for a valid token", error);
  }

  try {
    const payload = verifyToken("not-a-token", "test-secret-test-secret-test-secret");
    assert.equal(payload, null);
    results.push("verifyToken returns null for an invalid token");
  } catch (error) {
    fail("verifyToken returns null for an invalid token", error);
  }

  try {
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
    results.push("findRequestForUser returns an accessible request");
  } catch (error) {
    fail("findRequestForUser returns an accessible request", error);
  }

  try {
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
    results.push("ensureRoom creates a room when none exists");
  } catch (error) {
    fail("ensureRoom creates a room when none exists", error);
  }

  console.log("Node gateway tests passed:");
  results.forEach((result) => console.log(`- ${result}`));
}

function fail(name, error) {
  console.error(`Test failed: ${name}`);
  console.error(error);
  process.exit(1);
}

run().catch((error) => fail("run-tests bootstrap", error));

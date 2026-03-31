const test = require("node:test");
const assert = require("node:assert/strict");
const jwt = require("jsonwebtoken");
const { verifyToken } = require("../src/services/authService");

test("verifyToken returns payload for a valid token", () => {
  const token = jwt.sign({ email: "maya@test.dev", role: "USER" }, "test-secret-test-secret-test-secret", {
    subject: "42"
  });
  const payload = verifyToken(token, "test-secret-test-secret-test-secret");
  assert.equal(payload.sub, "42");
  assert.equal(payload.email, "maya@test.dev");
});

test("verifyToken returns null for an invalid token", () => {
  const payload = verifyToken("not-a-token", "test-secret-test-secret-test-secret");
  assert.equal(payload, null);
});

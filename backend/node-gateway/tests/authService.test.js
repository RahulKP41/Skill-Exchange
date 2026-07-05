const assert = require("assert");
const jwt = require("jsonwebtoken");
const { verifyToken } = require("../src/services/authService");

const secret = "test-secret";

function testValidToken() {
  const token = jwt.sign({ sub: "7", role: "USER" }, secret);
  const payload = verifyToken(token, secret);

  assert(payload, "Expected payload for a valid token");
  assert.strictEqual(payload.sub, "7");
  console.log("- verifyToken returns payload for a valid token");
}

function testInvalidToken() {
  const payload = verifyToken("not-a-real-token", secret);

  assert.strictEqual(payload, null);
  console.log("- verifyToken returns null for an invalid token");
}

testValidToken();
testInvalidToken();

const jwt = require("jsonwebtoken");

function verifyToken(token, secret) {
  try {
    return jwt.verify(token, secret);
  } catch {
    return null;
  }
}

module.exports = { verifyToken };


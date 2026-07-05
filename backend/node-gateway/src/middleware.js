const { verifyToken } = require("./services/authService");

function authMiddleware(secret) {
  return (req, res, next) => {
    const authorization = req.headers.authorization || "";
    const token = authorization.startsWith("Bearer ") ? authorization.slice(7) : null;
    if (!token) {
      return res.status(401).json({ message: "Missing bearer token." });
    }

    const payload = verifyToken(token, secret);
    if (!payload) {
      return res.status(401).json({ message: "Invalid token." });
    }

    req.user = {
      id: Number(payload.sub),
      email: payload.email,
      role: payload.role
    };
    return next();
  };
}

module.exports = { authMiddleware };


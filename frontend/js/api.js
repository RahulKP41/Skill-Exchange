(function () {
  const ACCESS_KEY = "skillx_access_token";
  const REFRESH_KEY = "skillx_refresh_token";
  const USER_KEY = "skillx_user";
  const protocol = window.location.protocol === "http:" || window.location.protocol === "https:"
    ? window.location.protocol
    : "http:";
  const host = window.location.hostname || "localhost";
  const API_BASE = `${protocol}//${host}:8081/api`;
  const GATEWAY_BASE = `${protocol}//${host}:3001`;
  const SOCKET_BASE = `${protocol}//${host}:3001`;

  function getStoredUser() {
    const value = localStorage.getItem(USER_KEY);
    return value ? JSON.parse(value) : null;
  }

  function setSession(authResponse) {
    localStorage.setItem(ACCESS_KEY, authResponse.accessToken);
    localStorage.setItem(REFRESH_KEY, authResponse.refreshToken);
    localStorage.setItem(USER_KEY, JSON.stringify(authResponse.user));
  }

  function clearSession() {
    localStorage.removeItem(ACCESS_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(USER_KEY);
  }

  function getAccessToken() {
    return localStorage.getItem(ACCESS_KEY);
  }

  function getRefreshToken() {
    return localStorage.getItem(REFRESH_KEY);
  }

  async function refreshSession() {
    const refreshToken = getRefreshToken();
    if (!refreshToken) {
      throw new Error("Your session has expired. Please log in again.");
    }

    const response = await fetch(`${API_BASE}/auth/refresh`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ refreshToken })
    });

    if (!response.ok) {
      clearSession();
      throw new Error("Your session has expired. Please log in again.");
    }

    const data = await response.json();
    setSession(data);
    return data;
  }

  async function safeJson(response) {
    const text = await response.text();
    return text ? JSON.parse(text) : {};
  }

  async function request(path, options = {}, base = API_BASE, retry = true) {
    const headers = new Headers(options.headers || {});
    const token = getAccessToken();
    const hasBody = options.body && !(options.body instanceof FormData);

    if (hasBody && !headers.has("Content-Type")) {
      headers.set("Content-Type", "application/json");
    }
    if (token && !headers.has("Authorization")) {
      headers.set("Authorization", `Bearer ${token}`);
    }

    const response = await fetch(`${base}${path}`, { ...options, headers });

    if (response.status === 401 && retry && getRefreshToken() && !path.startsWith("/auth/refresh")) {
      await refreshSession();
      return request(path, options, base, false);
    }

    if (!response.ok) {
      const payload = await safeJson(response);
      throw new Error(payload.message || "Something went wrong.");
    }

    if (response.status === 204) {
      return null;
    }

    return safeJson(response);
  }

  async function ensureCurrentUser() {
    if (getStoredUser()) {
      return getStoredUser();
    }

    const user = await request("/auth/me");
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    return user;
  }

  async function requireAuth(roles) {
    if (!getAccessToken()) {
      window.location.href = "/pages/login.html";
      throw new Error("Authentication required.");
    }

    const user = await ensureCurrentUser();
    if (roles && roles.length && !roles.includes(user.role)) {
      window.location.href = "/pages/dashboard.html";
      throw new Error("You do not have access to this page.");
    }
    return user;
  }

  function redirectIfAuthenticated(target) {
    if (getAccessToken()) {
      window.location.href = target || "/pages/dashboard.html";
    }
  }

  function logout() {
    clearSession();
    window.location.href = "/pages/login.html";
  }

  function setUser(user) {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  function formatDate(value) {
    if (!value) return "Not scheduled";
    return new Intl.DateTimeFormat("en-IN", {
      dateStyle: "medium",
      timeStyle: "short"
    }).format(new Date(value));
  }

  window.SkillExchange = {
    request,
    gatewayRequest: (path, options) => request(path, options, GATEWAY_BASE),
    setSession,
    clearSession,
    getStoredUser,
    ensureCurrentUser,
    requireAuth,
    redirectIfAuthenticated,
    logout,
    setUser,
    formatDate,
    socketBase: SOCKET_BASE
  };
})();

document.addEventListener("DOMContentLoaded", async () => {
  const body = document.body;
  const authMode = body.dataset.auth;

  try {
    if (authMode === "required") {
      await window.SkillExchange.requireAuth();
    }
    if (authMode === "admin") {
      await window.SkillExchange.requireAuth(["ADMIN"]);
    }
    if (authMode === "guest") {
      window.SkillExchange.redirectIfAuthenticated("/pages/dashboard.html");
    }
  } catch (error) {
    return;
  }

  const navUser = document.querySelector("[data-nav-user]");
  const logoutButton = document.querySelector("[data-logout]");
  const currentUser = window.SkillExchange.getStoredUser();

  if (navUser) {
    if (currentUser) {
      navUser.innerHTML = `
        <div class="d-flex align-items-center gap-2">
          <span class="avatar-circle d-inline-flex align-items-center justify-content-center bg-white text-primary fw-bold">
            ${currentUser.fullName ? currentUser.fullName.charAt(0) : "S"}
          </span>
          <div>
            <div class="fw-semibold">${currentUser.fullName || "Member"}</div>
            <div class="small text-secondary">${currentUser.role}</div>
          </div>
        </div>
      `;
    } else {
      navUser.innerHTML = `
        <div class="d-flex gap-2">
          <a class="btn btn-soft" href="/pages/login.html">Log In</a>
          <a class="btn btn-brand" href="/pages/register.html">Join Now</a>
        </div>
      `;
    }
  }

  if (logoutButton) {
    logoutButton.addEventListener("click", () => window.SkillExchange.logout());
  }
});


document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.querySelector("#loginForm");
  const registerForm = document.querySelector("#registerForm");
  const feedback = document.querySelector("[data-auth-feedback]");

  const showMessage = (message, isError = false) => {
    if (!feedback) return;
    feedback.className = `alert ${isError ? "alert-danger" : "alert-success"} d-block`;
    feedback.textContent = message;
  };

  if (loginForm) {
    loginForm.addEventListener("submit", async (event) => {
      event.preventDefault();
      try {
        const formData = new FormData(loginForm);
        const payload = await window.SkillExchange.request("/auth/login", {
          method: "POST",
          body: JSON.stringify({
            email: formData.get("email"),
            password: formData.get("password")
          })
        });
        window.SkillExchange.setSession(payload);
        showMessage("Welcome back. Redirecting...");
        window.location.href = "/pages/dashboard.html";
      } catch (error) {
        showMessage(error.message, true);
      }
    });
  }

  if (registerForm) {
    registerForm.addEventListener("submit", async (event) => {
      event.preventDefault();
      try {
        const formData = new FormData(registerForm);
        const payload = await window.SkillExchange.request("/auth/register", {
          method: "POST",
          body: JSON.stringify({
            fullName: formData.get("fullName"),
            email: formData.get("email"),
            password: formData.get("password"),
            headline: formData.get("headline")
          })
        });
        window.SkillExchange.setSession(payload);
        showMessage("Your account is ready. Redirecting...");
        window.location.href = "/pages/dashboard.html";
      } catch (error) {
        showMessage(error.message, true);
      }
    });
  }
});

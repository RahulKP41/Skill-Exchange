document.addEventListener("DOMContentLoaded", async () => {
  const summary = document.querySelector("#dashboardSummary");
  if (!summary) return;

  try {
    const [profile, matches, requests, sessions, wallet] = await Promise.all([
      window.SkillExchange.request("/profile"),
      window.SkillExchange.request("/matches"),
      window.SkillExchange.request("/requests"),
      window.SkillExchange.request("/sessions"),
      window.SkillExchange.request("/wallet")
    ]);

    summary.innerHTML = `
      <div class="col-md-6 col-xl-3">
        <div class="metric-card">
          <p class="text-secondary mb-2">Points balance</p>
          <h3 class="mb-1">${wallet.currentBalance}</h3>
          <span class="small text-secondary">Use points to book new skill sessions.</span>
        </div>
      </div>
      <div class="col-md-6 col-xl-3">
        <div class="metric-card">
          <p class="text-secondary mb-2">Profile rating</p>
          <h3 class="mb-1">${profile.averageRating}</h3>
          <span class="small text-secondary">${profile.totalReviews} public reviews</span>
        </div>
      </div>
      <div class="col-md-6 col-xl-3">
        <div class="metric-card">
          <p class="text-secondary mb-2">Smart matches</p>
          <h3 class="mb-1">${matches.length}</h3>
          <span class="small text-secondary">Compatible members in your network.</span>
        </div>
      </div>
      <div class="col-md-6 col-xl-3">
        <div class="metric-card">
          <p class="text-secondary mb-2">Unread alerts</p>
          <h3 class="mb-1">${profile.unreadNotifications}</h3>
          <span class="small text-secondary">Requests, session updates, and feedback.</span>
        </div>
      </div>
    `;

    renderList("#dashboardMatches", matches.slice(0, 3), (match) => `
      <li class="panel-card">
        <div class="d-flex justify-content-between align-items-start gap-3">
          <div>
            <h5 class="mb-1">${match.fullName}</h5>
            <p class="mb-1 text-secondary">${match.offeredSkillName} for ${match.requestedSkillName}</p>
            <small class="text-secondary">Match score ${match.totalScore}</small>
          </div>
          <span class="status-pill accepted">${match.pointsCost} pts</span>
        </div>
      </li>
    `, "No matches yet. Add more teach and learn skills to improve recommendations.");

    renderList("#dashboardRequests", requests.slice(0, 3), (request) => `
      <li class="panel-card">
        <div class="d-flex justify-content-between align-items-start gap-3">
          <div>
            <h5 class="mb-1">${request.senderName} → ${request.receiverName}</h5>
            <p class="mb-1 text-secondary">${request.offeredSkillName} for ${request.requestedSkillName}</p>
          </div>
          <span class="status-pill ${request.status.toLowerCase()}">${request.status}</span>
        </div>
      </li>
    `, "Your request board is empty right now.");

    renderList("#dashboardSessions", sessions.slice(0, 3), (session) => `
      <li class="panel-card">
        <div class="d-flex justify-content-between align-items-start gap-3">
          <div>
            <h5 class="mb-1">${session.skillFocus}</h5>
            <p class="mb-1 text-secondary">With ${session.partnerName}</p>
            <small class="text-secondary">${window.SkillExchange.formatDate(session.scheduledAt)}</small>
          </div>
          <span class="status-pill ${session.status.toLowerCase()}">${session.status}</span>
        </div>
      </li>
    `, "Schedule your first session from the requests page.");
  } catch (error) {
    summary.innerHTML = `<div class="col-12"><div class="alert alert-danger">${error.message}</div></div>`;
  }
});

function renderList(selector, items, renderer, emptyMessage) {
  const target = document.querySelector(selector);
  if (!target) return;
  if (!items.length) {
    target.innerHTML = `<div class="empty-state">${emptyMessage}</div>`;
    return;
  }
  target.innerHTML = items.map(renderer).join("");
}


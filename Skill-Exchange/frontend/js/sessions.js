document.addEventListener("DOMContentLoaded", async () => {
  const sessionList = document.querySelector("#sessionList");
  const scheduleForm = document.querySelector("#scheduleForm");
  if (!sessionList || !scheduleForm) return;

  async function loadAcceptedRequests() {
    const requests = await window.SkillExchange.request("/requests");
    const select = scheduleForm.requestId;
    const accepted = requests.filter((request) => request.status === "ACCEPTED");
    select.innerHTML = accepted.map((request) => `
      <option value="${request.id}">
        ${request.offeredSkillName} → ${request.requestedSkillName} with ${request.receiverName}
      </option>
    `).join("");
  }

  async function loadSessions() {
    try {
      const sessions = await window.SkillExchange.request("/sessions");
      if (!sessions.length) {
        sessionList.innerHTML = `<div class="empty-state">No sessions booked yet. Accept a request and schedule your first exchange.</div>`;
        return;
      }

      sessionList.innerHTML = sessions.map((session) => `
        <div class="panel-card mb-3">
          <div class="d-flex justify-content-between align-items-start gap-3">
            <div>
              <h4 class="mb-1">${session.skillFocus}</h4>
              <p class="text-secondary mb-2">With ${session.partnerName}</p>
              <p class="mb-2">${session.agenda || "No agenda set yet."}</p>
              <small class="text-secondary">${window.SkillExchange.formatDate(session.scheduledAt)} · ${session.durationMinutes} mins</small>
            </div>
            <span class="status-pill ${session.status.toLowerCase()}">${session.status}</span>
          </div>
          <div class="d-flex gap-2 flex-wrap mt-3">
            <a class="btn btn-brand btn-sm" href="${session.meetingLink}" target="_blank" rel="noreferrer">Open Jitsi room</a>
            ${session.status === "SCHEDULED" ? `<button class="btn btn-soft btn-sm" data-complete-session="${session.id}">Mark complete</button>` : ""}
          </div>
        </div>
      `).join("");
    } catch (error) {
      sessionList.innerHTML = `<div class="alert alert-danger">${error.message}</div>`;
    }
  }

  await Promise.all([loadAcceptedRequests(), loadSessions()]);

  scheduleForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
      await window.SkillExchange.request("/sessions", {
        method: "POST",
        body: JSON.stringify({
          requestId: Number(scheduleForm.requestId.value),
          scheduledAt: new Date(scheduleForm.scheduledAt.value).toISOString(),
          durationMinutes: Number(scheduleForm.durationMinutes.value),
          agenda: scheduleForm.agenda.value
        })
      });
      scheduleForm.reset();
      await Promise.all([loadAcceptedRequests(), loadSessions()]);
    } catch (error) {
      alert(error.message);
    }
  });

  sessionList.addEventListener("click", async (event) => {
    const button = event.target.closest("[data-complete-session]");
    if (!button) return;
    try {
      await window.SkillExchange.request(`/sessions/${button.dataset.completeSession}/complete`, {
        method: "PUT",
        body: JSON.stringify({ completionNotes: "Completed via the Skill Exchange dashboard." })
      });
      await loadSessions();
    } catch (error) {
      alert(error.message);
    }
  });
});

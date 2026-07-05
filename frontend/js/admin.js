document.addEventListener("DOMContentLoaded", async () => {
  const overview = document.querySelector("#adminOverview");
  const analyticsTarget = document.querySelector("#adminAnalytics");
  const reportsTarget = document.querySelector("#moderationReports");
  if (!overview) return;

  async function loadAdmin() {
    try {
      const [summary, analytics, reports] = await Promise.all([
        window.SkillExchange.request("/admin/overview"),
        window.SkillExchange.request("/analytics/summary"),
        window.SkillExchange.request("/admin/reports")
      ]);

      overview.innerHTML = `
        <div class="col-md-6 col-xl-4"><div class="metric-card"><p class="text-secondary mb-2">Members</p><h3>${summary.totalUsers}</h3></div></div>
        <div class="col-md-6 col-xl-4"><div class="metric-card"><p class="text-secondary mb-2">Exchange requests</p><h3>${summary.totalRequests}</h3></div></div>
        <div class="col-md-6 col-xl-4"><div class="metric-card"><p class="text-secondary mb-2">Open reports</p><h3>${summary.openReports}</h3></div></div>
      `;

      analyticsTarget.innerHTML = `
        <div class="panel-card h-100">
          <h4 class="mb-3">Platform analytics</h4>
          <ul class="list-clean">
            <li>Active users: ${analytics.activeUsers}</li>
            <li>Completed sessions: ${analytics.completedSessions}</li>
            <li>Average feedback rating: ${analytics.averageRating.toFixed(2)}</li>
            <li>Point transactions: ${analytics.totalPointTransactions}</li>
          </ul>
        </div>
      `;

      if (!reports.length) {
        reportsTarget.innerHTML = `<div class="empty-state">No moderation reports right now.</div>`;
        return;
      }

      reportsTarget.innerHTML = reports.map((report) => `
        <div class="panel-card mb-3">
          <div class="d-flex justify-content-between gap-3">
            <div>
              <h5 class="mb-1">${report.reason}</h5>
              <p class="mb-1 text-secondary">${report.reporterName} reported ${report.reportedUserName}</p>
              <p class="mb-2">${report.details || "No extra details provided."}</p>
              <small class="text-secondary">${window.SkillExchange.formatDate(report.createdAt)}</small>
            </div>
            <div class="text-end">
              <span class="status-pill ${report.status.toLowerCase()}">${report.status}</span>
              <div class="mt-3 d-flex gap-2 justify-content-end">
                <button class="btn btn-soft btn-sm" data-report-id="${report.id}" data-status="REVIEWING">Reviewing</button>
                <button class="btn btn-brand btn-sm" data-report-id="${report.id}" data-status="RESOLVED">Resolve</button>
              </div>
            </div>
          </div>
        </div>
      `).join("");
    } catch (error) {
      reportsTarget.innerHTML = `<div class="alert alert-danger">${error.message}</div>`;
    }
  }

  reportsTarget.addEventListener("click", async (event) => {
    const button = event.target.closest("[data-report-id]");
    if (!button) return;
    try {
      await window.SkillExchange.request(`/admin/reports/${button.dataset.reportId}`, {
        method: "PUT",
        body: JSON.stringify({ status: button.dataset.status })
      });
      await loadAdmin();
    } catch (error) {
      alert(error.message);
    }
  });

  await loadAdmin();
});

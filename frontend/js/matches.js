document.addEventListener("DOMContentLoaded", async () => {
  const matchList = document.querySelector("#matchList");
  if (!matchList) return;

  async function loadMatches() {
    try {
      const matches = await window.SkillExchange.request("/matches");
      if (!matches.length) {
        matchList.innerHTML = `<div class="empty-state">No smart matches yet. Add more learning goals or availability to widen your network.</div>`;
        return;
      }

      matchList.innerHTML = matches.map((match) => `
        <div class="col-lg-6 fade-up">
          <div class="panel-card h-100">
            <div class="d-flex align-items-start gap-3 mb-3">
              <img class="avatar-circle" src="${match.profilePhotoUrl || "https://placehold.co/72x72"}" alt="${match.fullName}">
              <div class="flex-grow-1">
                <div class="d-flex justify-content-between align-items-start gap-3">
                  <div>
                    <h4 class="mb-1">${match.fullName}</h4>
                    <p class="text-secondary mb-1">${match.headline || "Skill exchange member"}</p>
                    <small class="text-secondary">${match.location || "Remote"} · Rating ${match.averageRating}</small>
                  </div>
                  <span class="status-pill accepted">${match.totalScore}</span>
                </div>
              </div>
            </div>
            <div class="d-flex flex-wrap gap-2 mb-3">
              <span class="skill-chip">You teach: ${match.offeredSkillName}</span>
              <span class="skill-chip">You learn: ${match.requestedSkillName}</span>
              <span class="skill-chip">${match.pointsCost} points</span>
            </div>
            <div class="small text-secondary mb-3">
              Reciprocity ${match.reciprocityScore} · Rating ${match.ratingScore} · Availability ${match.availabilityScore}
            </div>
            <button
              class="btn btn-brand"
              data-send-request="true"
              data-user-id="${match.userId}"
              data-offered-id="${match.offeredUserSkillId}"
              data-requested-id="${match.requestedUserSkillId}"
              data-offered-skill="${match.offeredSkillName}"
              data-requested-skill="${match.requestedSkillName}"
            >
              Send exchange request
            </button>
          </div>
        </div>
      `).join("");
    } catch (error) {
      matchList.innerHTML = `<div class="alert alert-danger">${error.message}</div>`;
    }
  }

  await loadMatches();

  matchList.addEventListener("click", async (event) => {
    const button = event.target.closest("[data-send-request]");
    if (!button) return;

    const offeredSkillName = button.dataset.offeredSkill;
    const requestedSkillName = button.dataset.requestedSkill;
    const message = window.prompt("Add a short intro message for this request:", `I would love to exchange ${offeredSkillName} for ${requestedSkillName}.`);
    if (!message) return;

    try {
      await window.SkillExchange.request("/requests", {
        method: "POST",
        body: JSON.stringify({
          receiverId: Number(button.dataset.userId),
          offeredUserSkillId: Number(button.dataset.offeredId),
          requestedUserSkillId: Number(button.dataset.requestedId),
          message,
          preferredDateTime: new Date(Date.now() + 86400000).toISOString()
        })
      });
      alert("Exchange request sent.");
    } catch (error) {
      alert(error.message);
    }
  });
});

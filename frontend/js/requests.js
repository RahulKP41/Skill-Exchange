document.addEventListener("DOMContentLoaded", async () => {
  const requestBoard = document.querySelector("#requestBoard");
  if (!requestBoard) return;
  const currentUser = await window.SkillExchange.requireAuth();

  async function loadRequests() {
    try {
      const requests = await window.SkillExchange.request("/requests");
      if (!requests.length) {
        requestBoard.innerHTML = `<div class="empty-state">You have not created or received any exchange requests yet.</div>`;
        return;
      }

      requestBoard.innerHTML = requests.map((request) => {
        const isReceiver = request.receiverId === currentUser.id;
        const isSender = request.senderId === currentUser.id;
        return `
          <div class="panel-card mb-3">
            <div class="d-flex justify-content-between align-items-start gap-3">
              <div>
                <h4 class="mb-1">${request.offeredSkillName} → ${request.requestedSkillName}</h4>
                <p class="text-secondary mb-2">${request.senderName} to ${request.receiverName}</p>
                <p class="mb-2">${request.message || "No message included."}</p>
                <small class="text-secondary">Preferred time: ${window.SkillExchange.formatDate(request.preferredDateTime)}</small>
              </div>
              <span class="status-pill ${request.status.toLowerCase()}">${request.status}</span>
            </div>
            <div class="d-flex gap-2 flex-wrap mt-3">
              ${isReceiver && request.status === "PENDING" ? `<button class="btn btn-brand btn-sm" data-request-action="ACCEPTED" data-request-id="${request.id}">Accept</button><button class="btn btn-soft btn-sm" data-request-action="REJECTED" data-request-id="${request.id}">Reject</button>` : ""}
              ${isSender && request.status === "PENDING" ? `<button class="btn btn-soft btn-sm" data-request-action="CANCELLED" data-request-id="${request.id}">Cancel</button>` : ""}
            </div>
          </div>
        `;
      }).join("");
    } catch (error) {
      requestBoard.innerHTML = `<div class="alert alert-danger">${error.message}</div>`;
    }
  }

  await loadRequests();

  requestBoard.addEventListener("click", async (event) => {
    const button = event.target.closest("[data-request-action]");
    if (!button) return;
    try {
      await window.SkillExchange.request(`/requests/${button.dataset.requestId}/status`, {
        method: "PUT",
        body: JSON.stringify({ status: button.dataset.requestAction })
      });
      await loadRequests();
    } catch (error) {
      alert(error.message);
    }
  });
});


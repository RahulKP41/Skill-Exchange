document.addEventListener("DOMContentLoaded", async () => {
  const walletBalance = document.querySelector("#walletBalance");
  const transactionList = document.querySelector("#transactionList");
  const feedbackList = document.querySelector("#feedbackList");
  if (!walletBalance) return;

  try {
    const [wallet, feedback] = await Promise.all([
      window.SkillExchange.request("/wallet"),
      window.SkillExchange.request("/feedback")
    ]);

    walletBalance.textContent = `${wallet.currentBalance} points available`;

    if (!wallet.transactions.length) {
      transactionList.innerHTML = `<div class="empty-state">No point transactions yet.</div>`;
    } else {
      transactionList.innerHTML = wallet.transactions.map((transaction) => `
        <div class="panel-card mb-3">
          <div class="d-flex justify-content-between gap-3">
            <div>
              <h5 class="mb-1">${transaction.description}</h5>
              <p class="mb-1 text-secondary">${transaction.transactionType}</p>
              <small class="text-secondary">${window.SkillExchange.formatDate(transaction.createdAt)}</small>
            </div>
            <div class="text-end">
              <h5 class="mb-1 ${transaction.pointsDelta > 0 ? "text-success" : "text-danger"}">${transaction.pointsDelta}</h5>
              <small class="text-secondary">Balance ${transaction.balanceAfter}</small>
            </div>
          </div>
        </div>
      `).join("");
    }

    if (!feedback.length) {
      feedbackList.innerHTML = `<div class="empty-state">No feedback yet. Complete a session to start building your trust score.</div>`;
    } else {
      feedbackList.innerHTML = feedback.map((entry) => `
        <div class="panel-card mb-3">
          <div class="d-flex justify-content-between align-items-start gap-3">
            <div>
              <h5 class="mb-1">${entry.reviewerName}</h5>
              <p class="mb-2">${entry.comment}</p>
              <small class="text-secondary">${window.SkillExchange.formatDate(entry.createdAt)}</small>
            </div>
            <span class="status-pill accepted">${entry.rating}/5</span>
          </div>
        </div>
      `).join("");
    }
  } catch (error) {
    transactionList.innerHTML = `<div class="alert alert-danger">${error.message}</div>`;
  }
});


document.addEventListener("DOMContentLoaded", async () => {
  const requestPicker = document.querySelector("#chatRequestPicker");
  const chatMessages = document.querySelector("#chatMessages");
  const chatForm = document.querySelector("#chatForm");
  const typingState = document.querySelector("#typingState");
  if (!requestPicker || !chatMessages || !chatForm || typeof io === "undefined") return;

  let activeRequestId = null;
  const user = await window.SkillExchange.requireAuth();
  const token = localStorage.getItem("skillx_access_token");
  const socket = io(window.SkillExchange.socketBase, {
    auth: { token }
  });

  async function loadRequests() {
    const requests = await window.SkillExchange.request("/requests");
    const chatRequests = requests.filter((request) => ["ACCEPTED", "COMPLETED", "PENDING"].includes(request.status));
    requestPicker.innerHTML = chatRequests.map((request) => `
      <option value="${request.id}">
        ${request.offeredSkillName} ↔ ${request.requestedSkillName}
      </option>
    `).join("");

    if (chatRequests[0]) {
      activeRequestId = Number(chatRequests[0].id);
      await openConversation(activeRequestId);
    }
  }

  async function openConversation(requestId) {
    activeRequestId = Number(requestId);
    socket.emit("chat:join", { requestId: activeRequestId });
    socket.emit("chat:history", { requestId: activeRequestId });
    const room = await window.SkillExchange.gatewayRequest(`/requests/${activeRequestId}/messages`);
    renderMessages(room.messages);
  }

  function renderMessages(messages) {
    if (!messages.length) {
      chatMessages.innerHTML = `<div class="empty-state">No messages yet. Start the conversation with a warm introduction.</div>`;
      return;
    }

    chatMessages.innerHTML = messages.map((message) => `
      <div class="message-bubble ${message.senderId === user.id ? "self" : ""}">
        <div>${message.content}</div>
        <small class="${message.senderId === user.id ? "text-white-50" : "text-secondary"}">${window.SkillExchange.formatDate(message.createdAt)}</small>
      </div>
    `).join("");
    chatMessages.scrollTop = chatMessages.scrollHeight;
  }

  requestPicker.addEventListener("change", async () => {
    if (!requestPicker.value) return;
    await openConversation(Number(requestPicker.value));
  });

  chatForm.addEventListener("submit", (event) => {
    event.preventDefault();
    if (!activeRequestId) return;
    const content = chatForm.content.value.trim();
    if (!content) return;
    socket.emit("chat:message", { requestId: activeRequestId, content });
    chatForm.reset();
  });

  chatForm.content.addEventListener("input", () => {
    if (activeRequestId) {
      socket.emit("chat:typing", { requestId: activeRequestId, isTyping: true });
    }
  });

  socket.on("chat:history", ({ requestId, messages }) => {
    if (Number(requestId) === activeRequestId) {
      renderMessages(messages);
      socket.emit("chat:seen", { requestId: activeRequestId });
    }
  });

  socket.on("chat:message", ({ requestId }) => {
    if (Number(requestId) === activeRequestId) {
      openConversation(activeRequestId);
    }
  });

  socket.on("chat:typing", ({ requestId, userId, isTyping }) => {
    if (Number(requestId) !== activeRequestId || userId === user.id) return;
    typingState.textContent = isTyping ? "Your exchange partner is typing..." : "";
  });

  socket.on("chat:seen", ({ requestId, userId }) => {
    if (Number(requestId) === activeRequestId && userId !== user.id) {
      typingState.textContent = "Messages seen";
      setTimeout(() => {
        typingState.textContent = "";
      }, 2000);
    }
  });

  await loadRequests();
});

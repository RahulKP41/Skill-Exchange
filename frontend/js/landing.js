document.addEventListener("DOMContentLoaded", async () => {
  const featured = document.querySelector("#featuredMembers");
  if (!featured) return;

  try {
    const users = await window.SkillExchange.request("/users/featured");
    featured.innerHTML = users.slice(0, 3).map((user) => `
      <div class="col-lg-4">
        <div class="panel-card h-100">
          <div class="d-flex align-items-center gap-3 mb-3">
            <img class="avatar-circle" src="${user.profilePhotoUrl || "https://placehold.co/72x72"}" alt="${user.fullName}">
            <div>
              <h5 class="mb-1">${user.fullName}</h5>
              <p class="text-secondary mb-1">${user.headline || "Skill exchange member"}</p>
              <small class="text-secondary">${user.location || "Remote"}</small>
            </div>
          </div>
          <p class="mb-3">${user.bio || "Community member building their next learning sprint."}</p>
          <div class="d-flex flex-wrap gap-2">
            <span class="skill-chip">${user.pointsBalance} pts</span>
            <span class="skill-chip">${user.averageRating} rating</span>
          </div>
        </div>
      </div>
    `).join("");
  } catch (error) {
    featured.innerHTML = `<div class="col-12"><div class="alert alert-warning">Featured profiles will appear once the API is running.</div></div>`;
  }
});

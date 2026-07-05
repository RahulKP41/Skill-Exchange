document.addEventListener("DOMContentLoaded", async () => {
  const profileForm = document.querySelector("#profileForm");
  const availabilityList = document.querySelector("#availabilityList");
  const profileHero = document.querySelector("#profileHero");
  const profileFeedback = document.querySelector("#profileFeedback");
  if (!profileForm) return;

  try {
    const profile = await window.SkillExchange.request("/profile");
    profileHero.innerHTML = `
      <div class="d-flex align-items-center gap-3">
        <img class="avatar-circle" src="${profile.profilePhotoUrl || "https://placehold.co/96x96"}" alt="${profile.fullName}">
        <div>
          <h2 class="mb-1">${profile.fullName}</h2>
          <p class="mb-1 text-secondary">${profile.headline || "Add a strong headline to attract the right exchange partners."}</p>
          <div class="d-flex gap-2 flex-wrap">
            <span class="skill-chip">${profile.pointsBalance} pts</span>
            <span class="skill-chip">${profile.averageRating} rating</span>
            <span class="skill-chip">${profile.location || "Remote-friendly"}</span>
          </div>
        </div>
      </div>
    `;

    profileForm.fullName.value = profile.fullName || "";
    profileForm.headline.value = profile.headline || "";
    profileForm.bio.value = profile.bio || "";
    profileForm.phone.value = profile.phone || "";
    profileForm.location.value = profile.location || "";
    profileForm.profilePhotoUrl.value = profile.profilePhotoUrl || "";

    availabilityList.innerHTML = (profile.availability || []).map((slot) => `
      <div class="row g-2 align-items-center mb-2 availability-row">
        <div class="col-md-3"><input class="form-control" name="weekday" value="${slot.weekday}"></div>
        <div class="col-md-3"><input class="form-control" name="startTime" value="${slot.startTime}"></div>
        <div class="col-md-3"><input class="form-control" name="endTime" value="${slot.endTime}"></div>
        <div class="col-md-3"><input class="form-control" name="timezone" value="${slot.timezone}"></div>
      </div>
    `).join("") || defaultAvailabilityRow();

    profileFeedback.textContent = `${profile.skills.length} skills across teaching and learning tracks.`;
  } catch (error) {
    profileFeedback.textContent = error.message;
  }

  document.querySelector("#addAvailabilityRow").addEventListener("click", () => {
    availabilityList.insertAdjacentHTML("beforeend", defaultAvailabilityRow());
  });

  profileForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
      const availability = Array.from(availabilityList.querySelectorAll(".availability-row")).map((row) => ({
        weekday: row.querySelector("[name=weekday]").value,
        startTime: row.querySelector("[name=startTime]").value,
        endTime: row.querySelector("[name=endTime]").value,
        timezone: row.querySelector("[name=timezone]").value
      })).filter((slot) => slot.weekday && slot.startTime && slot.endTime && slot.timezone);

      await window.SkillExchange.request("/profile", {
        method: "PUT",
        body: JSON.stringify({
          fullName: profileForm.fullName.value,
          headline: profileForm.headline.value,
          bio: profileForm.bio.value,
          phone: profileForm.phone.value,
          location: profileForm.location.value,
          profilePhotoUrl: profileForm.profilePhotoUrl.value,
          availability
        })
      });

      profileFeedback.textContent = "Profile saved successfully.";
    } catch (error) {
      profileFeedback.textContent = error.message;
    }
  });
});

function defaultAvailabilityRow() {
  return `
    <div class="row g-2 align-items-center mb-2 availability-row">
      <div class="col-md-3"><input class="form-control" name="weekday" placeholder="MONDAY"></div>
      <div class="col-md-3"><input class="form-control" name="startTime" placeholder="18:00"></div>
      <div class="col-md-3"><input class="form-control" name="endTime" placeholder="20:00"></div>
      <div class="col-md-3"><input class="form-control" name="timezone" placeholder="Asia/Kolkata"></div>
    </div>
  `;
}


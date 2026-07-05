document.addEventListener("DOMContentLoaded", async () => {
  const addSkillForm = document.querySelector("#addSkillForm");
  const skillCatalog = document.querySelector("#skillCatalog");
  const mySkills = document.querySelector("#mySkills");
  if (!addSkillForm) return;

  try {
    const [skills, userSkills] = await Promise.all([
      window.SkillExchange.request("/skills"),
      window.SkillExchange.request("/user-skills")
    ]);

    const select = addSkillForm.skillId;
    select.innerHTML = skills.map((skill) => `<option value="${skill.id}">${skill.name} · ${skill.category}</option>`).join("");
    skillCatalog.innerHTML = skills.map((skill) => `
      <div class="col-md-6 col-xl-4">
        <div class="panel-card h-100">
          <span class="section-kicker">${skill.category}</span>
          <h5 class="mt-2">${skill.name}</h5>
          <p class="text-secondary mb-0">${skill.description}</p>
        </div>
      </div>
    `).join("");
    renderUserSkills(userSkills);
  } catch (error) {
    mySkills.innerHTML = `<div class="alert alert-danger">${error.message}</div>`;
  }

  addSkillForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    try {
      await window.SkillExchange.request("/user-skills", {
        method: "POST",
        body: JSON.stringify({
          skillId: Number(addSkillForm.skillId.value),
          skillType: addSkillForm.skillType.value,
          proficiencyLevel: addSkillForm.proficiencyLevel.value,
          yearsOfExperience: Number(addSkillForm.yearsOfExperience.value || 0),
          highlights: addSkillForm.highlights.value,
          featured: addSkillForm.featured.checked
        })
      });

      addSkillForm.reset();
      const userSkills = await window.SkillExchange.request("/user-skills");
      renderUserSkills(userSkills);
    } catch (error) {
      alert(error.message);
    }
  });

  mySkills.addEventListener("click", async (event) => {
    const button = event.target.closest("[data-delete-skill]");
    if (!button) return;
    try {
      await window.SkillExchange.request(`/user-skills/${button.dataset.deleteSkill}`, {
        method: "DELETE"
      });
      const userSkills = await window.SkillExchange.request("/user-skills");
      renderUserSkills(userSkills);
    } catch (error) {
      alert(error.message);
    }
  });

  function renderUserSkills(userSkills) {
    if (!userSkills.length) {
      mySkills.innerHTML = `<div class="empty-state">No skills added yet. Start with one skill you can teach and one you want to learn.</div>`;
      return;
    }
    mySkills.innerHTML = userSkills.map((skill) => `
      <div class="col-md-6">
        <div class="panel-card h-100">
          <div class="d-flex justify-content-between gap-2">
            <div>
              <span class="section-kicker">${skill.skillType}</span>
              <h5 class="mt-2 mb-1">${skill.skillName}</h5>
              <p class="text-secondary mb-2">${skill.category} · ${skill.proficiencyLevel}</p>
              <p class="mb-3">${skill.highlights || "Add highlights to show what you bring to a session."}</p>
            </div>
            <button class="btn btn-soft btn-sm" data-delete-skill="${skill.id}">Remove</button>
          </div>
        </div>
      </div>
    `).join("");
  }
});

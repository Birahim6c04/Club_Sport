/* ============================================================
   club-app.js — Espace club (tâche 3)
   ============================================================ */

// ID du club récupéré depuis l'URL : club.html?id=35238
const CLUB_ID = new URLSearchParams(window.location.search).get('id') || 'demo';
const API_GET  = `/clubs/api/club/${CLUB_ID}`;
const API_SAVE = `/clubs/api/club/${CLUB_ID}/update`;

const JOURS = ['lundi','mardi','mercredi','jeudi','vendredi','samedi','dimanche'];

// État local du club
let clubData = {
  actualites: [],
  horaires: {
    lundi:    { ouvert:true,  matin:'09:00', soir:'12:00' },
    mardi:    { ouvert:true,  matin:'14:00', soir:'20:00' },
    mercredi: { ouvert:true,  matin:'09:00', soir:'19:00' },
    jeudi:    { ouvert:true,  matin:'14:00', soir:'20:00' },
    vendredi: { ouvert:true,  matin:'14:00', soir:'19:00' },
    samedi:   { ouvert:true,  matin:'09:00', soir:'17:00' },
    dimanche: { ouvert:false, matin:'',      soir:''      }
  },
  cotisations: { adulte:180, jeune:90, famille:300, note:'' },
  contact:     { adresse:'', tel:'', email:'', web:'' }
};

// ============================================================
// CHARGEMENT INITIAL
// ============================================================
document.addEventListener('DOMContentLoaded', async () => {
  try {
    const resp = await fetch(API_GET);
    const data = await resp.json();

    // En-tête
    if (data.nom)        document.getElementById('hero-nom').textContent     = data.nom;
    if (data.federation) document.getElementById('hero-fed').textContent     = data.federation;
    if (data.region)     document.getElementById('hero-region').textContent  = data.region;
    if (data.total)      document.getElementById('stat-total').textContent   = data.total;
    if (data.femmes)     document.getElementById('stat-femmes').textContent  = data.femmes;
    if (data.hommes)     document.getElementById('stat-hommes').textContent  = data.hommes;

    // Contenu modifiable
    if (data.contenu) Object.assign(clubData, data.contenu);

  } catch (e) {
    console.warn('API non disponible — mode démo');
    document.getElementById('hero-nom').textContent    = 'Club Démo Sportif';
    document.getElementById('hero-fed').textContent    = 'Fédération française de tennis';
    document.getElementById('hero-region').textContent = 'Bretagne · Rennes';
    document.getElementById('stat-total').textContent  = '145';
    document.getElementById('stat-femmes').textContent = '72';
    document.getElementById('stat-hommes').textContent = '73';
    clubData.actualites = [
      { date:'1 mai 2025', titre:'Tournoi de printemps — inscriptions ouvertes !',
        corps:'Le tournoi se tiendra les 10 et 11 mai. Inscriptions avant le 5 mai.', tag:'Tournoi' },
      { date:'15 mars 2025', titre:'Nouveaux créneaux enfants le mercredi',
        corps:'Deux nouveaux créneaux pour les 6-10 ans, le mercredi de 10h à 11h30.', tag:'Entraînement' }
    ];
  }

  renderAll();
});

// ============================================================
// RENDU
// ============================================================
function renderAll() {
  renderActualites();
  renderHoraires();
  renderCotisations();
  renderContact();
  renderFormulaireHoraires();
}

function renderActualites() {
  const el = document.getElementById('news-list');
  if (!clubData.actualites.length) {
    el.innerHTML = '<p style="color:var(--muted);font-size:14px">Aucune actualité publiée.</p>';
    return;
  }
  el.innerHTML = clubData.actualites.map(a => `
    <div class="news-card">
      <div class="news-date">${esc(a.date)}</div>
      <div class="news-title">${esc(a.titre)}</div>
      <div class="news-body">${esc(a.corps)}</div>
      <span class="news-tag">${esc(a.tag)}</span>
    </div>`).join('');
}

function renderHoraires() {
  document.getElementById('horaires-display').innerHTML = JOURS.map(j => {
    const h = clubData.horaires[j] || { ouvert:false };
    const cls = h.ouvert ? 'schedule-row' : 'schedule-row ferme';
    const txt = h.ouvert ? `${h.matin} – ${h.soir}` : 'Fermé';
    return `<div class="${cls}"><span class="day">${cap(j)}</span><span>${txt}</span></div>`;
  }).join('');
}

function renderCotisations() {
  const c = clubData.cotisations;
  document.getElementById('cotisations-display').innerHTML = `
    <div class="info-row"><span class="key">Adulte</span><span class="val">${c.adulte} €/an</span></div>
    <div class="info-row"><span class="key">Jeune (–18 ans)</span><span class="val">${c.jeune} €/an</span></div>
    <div class="info-row"><span class="key">Famille</span><span class="val">${c.famille} €/an</span></div>
    ${c.note ? `<p style="font-size:12px;color:var(--muted);margin-top:8px">${esc(c.note)}</p>` : ''}`;
}

function renderContact() {
  const c = clubData.contact;
  document.getElementById('contact-display').innerHTML = `
    ${c.adresse ? `<div class="info-row"><span class="key">Adresse</span><span class="val">${esc(c.adresse)}</span></div>` : ''}
    ${c.tel     ? `<div class="info-row"><span class="key">Tél</span><span class="val">${esc(c.tel)}</span></div>` : ''}
    ${c.email   ? `<div class="info-row"><span class="key">Email</span><span class="val">${esc(c.email)}</span></div>` : ''}
    ${c.web     ? `<div class="info-row"><span class="key">Web</span><a href="${esc(c.web)}" target="_blank" style="color:var(--accent);font-size:12px">${esc(c.web)}</a></div>` : ''}
    ${!c.adresse && !c.tel && !c.email ? '<p style="font-size:13px;color:var(--muted)">Aucun contact renseigné.</p>' : ''}`;
}

function renderFormulaireHoraires() {
  document.getElementById('horaires-form').innerHTML = JOURS.map(j => {
    const h = clubData.horaires[j] || { ouvert:false, matin:'', soir:'' };
    return `
      <div style="display:flex;align-items:center;gap:10px;margin-bottom:10px;flex-wrap:wrap">
        <label style="min-width:78px;font-size:13px;font-weight:600">${cap(j)}</label>
        <input type="checkbox" id="hor-${j}-ouvert" ${h.ouvert ? 'checked' : ''}
               onchange="toggleJour('${j}')">
        <input type="time" class="form-input" id="hor-${j}-debut" value="${h.matin}"
               style="width:100px;padding:6px 10px" ${h.ouvert ? '' : 'disabled'}>
        <span style="color:var(--muted)">–</span>
        <input type="time" class="form-input" id="hor-${j}-fin" value="${h.soir}"
               style="width:100px;padding:6px 10px" ${h.ouvert ? '' : 'disabled'}>
      </div>`;
  }).join('');
}

function toggleJour(j) {
  const ouvert = document.getElementById(`hor-${j}-ouvert`).checked;
  document.getElementById(`hor-${j}-debut`).disabled = !ouvert;
  document.getElementById(`hor-${j}-fin`).disabled   = !ouvert;
}

// ============================================================
// ACTIONS
// ============================================================
function ajouterActualite() {
  const titre = document.getElementById('news-titre').value.trim();
  const corps = document.getElementById('news-corps').value.trim();
  const tag   = document.getElementById('news-tag').value;
  if (!titre || !corps) { alert('Titre et contenu sont obligatoires.'); return; }

  clubData.actualites.unshift({
    date: new Date().toLocaleDateString('fr-FR', { day:'numeric', month:'long', year:'numeric' }),
    titre, corps, tag
  });
  document.getElementById('news-titre').value = '';
  document.getElementById('news-corps').value = '';
  renderActualites();
  showToast('Actualité publiée !');
}

async function sauvegarder() {
  // Lire les horaires depuis le formulaire
  JOURS.forEach(j => {
    clubData.horaires[j] = {
      ouvert: document.getElementById(`hor-${j}-ouvert`)?.checked || false,
      matin:  document.getElementById(`hor-${j}-debut`)?.value   || '',
      soir:   document.getElementById(`hor-${j}-fin`)?.value     || ''
    };
  });
  // Cotisations
  clubData.cotisations = {
    adulte:  +document.getElementById('cotis-adulte').value  || 0,
    jeune:   +document.getElementById('cotis-jeune').value   || 0,
    famille: +document.getElementById('cotis-famille').value || 0,
    note:     document.getElementById('cotis-note').value
  };
  // Contact
  clubData.contact = {
    adresse: document.getElementById('contact-adresse').value,
    tel:     document.getElementById('contact-tel').value,
    email:   document.getElementById('contact-email').value,
    web:     document.getElementById('contact-web').value
  };

  // Envoi au backend
  try {
    await fetch(API_SAVE, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(clubData)
    });
  } catch (e) {
    console.warn('Backend non dispo — sauvegarde locale uniquement');
  }

  renderAll();
  fermerModal();
  showToast('Informations mises à jour !');
}

// ============================================================
// MODAL / TABS
// ============================================================
function ouvrirModal() {
  // Pré-remplir cotisations et contact
  const c = clubData.cotisations;
  document.getElementById('cotis-adulte').value  = c.adulte  || '';
  document.getElementById('cotis-jeune').value   = c.jeune   || '';
  document.getElementById('cotis-famille').value = c.famille || '';
  document.getElementById('cotis-note').value    = c.note    || '';
  const ct = clubData.contact;
  document.getElementById('contact-adresse').value = ct.adresse || '';
  document.getElementById('contact-tel').value     = ct.tel     || '';
  document.getElementById('contact-email').value   = ct.email   || '';
  document.getElementById('contact-web').value     = ct.web     || '';
  document.getElementById('modal-overlay').classList.add('open');
}

function fermerModal() {
  document.getElementById('modal-overlay').classList.remove('open');
}

function switchTab(name) {
  const tabs = ['actualites','horaires','cotisations','contact'];
  document.querySelectorAll('.tab-btn').forEach((b, i) => b.classList.toggle('active', tabs[i] === name));
  document.querySelectorAll('.tab-panel').forEach(p => p.classList.toggle('active', p.id === 'tab-' + name));
}

document.getElementById('modal-overlay').addEventListener('click', e => {
  if (e.target === e.currentTarget) fermerModal();
});

// ============================================================
// UTILITAIRES
// ============================================================
function showToast(msg) {
  const t = document.getElementById('toast');
  t.textContent = '✓ ' + msg;
  t.classList.add('show');
  setTimeout(() => t.classList.remove('show'), 3000);
}
function esc(s) {
  if (!s) return '';
  return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
}
function cap(s) { return s.charAt(0).toUpperCase() + s.slice(1); }
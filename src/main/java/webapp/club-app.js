/* ============================================================
   club-app.js — Espace club (tâche 3)
   Connecté exclusivement à la vraie base de données MySQL.
   Chemin : src/main/webapp/club-app.js
   ============================================================ */

const CLUB_ID = new URLSearchParams(window.location.search).get('id') || '';
const API_GET  = `/clubs/api/club/${CLUB_ID}`;
const API_SAVE = `/clubs/api/club/${CLUB_ID}/update`;

const JOURS = ['lundi','mardi','mercredi','jeudi','vendredi','samedi','dimanche'];

// État local — vide par défaut, rempli par la BDD
let clubData = {
  actualites:  [],
  horaires:    {},
  cotisations: { adulte: 0, jeune: 0, famille: 0, note: '' },
  contact:     { adresse: '', tel: '', email: '', web: '' }
};

// ── CHARGEMENT INITIAL ────────────────────────────────────────
document.addEventListener('DOMContentLoaded', async () => {

  // Si pas de code commune dans l'URL → erreur immédiate
  if (!CLUB_ID) {
    afficherErreur('Aucun club sélectionné. Revenez à la carte et cliquez sur un club.');
    return;
  }

  try {
    const resp = await fetch(API_GET);

    if (!resp.ok) throw new Error('Erreur serveur ' + resp.status);

    const data = await resp.json();

    // Remplir l'en-tête avec les vraies données de la base
    document.getElementById('hero-nom').textContent    = data.nom        || '—';
    document.getElementById('hero-fed').textContent    = data.federation || '—';
    document.getElementById('hero-region').textContent = data.region     || '—';
    document.getElementById('stat-total').textContent  = data.total      ?? '—';
    document.getElementById('stat-femmes').textContent = data.femmes     ?? '—';
    document.getElementById('stat-hommes').textContent = data.hommes     ?? '—';

    // Contenu modifiable depuis espace_club
    if (data.contenu) Object.assign(clubData, data.contenu);

  } catch (e) {
    afficherErreur(
      'Impossible de charger les données du club. ' +
      'Vérifiez que Tomcat est lancé et que la base de données est accessible.'
    );
    return;
  }

  renderAll();
});

// ── RENDU ─────────────────────────────────────────────────────
function renderAll() {
  renderActualites();
  renderHoraires();
  renderCotisations();
  renderContact();
  renderFormulaireHoraires();
}

function renderActualites() {
  const el = document.getElementById('news-list');
  if (!clubData.actualites || !clubData.actualites.length) {
    el.innerHTML = '<p style="color:var(--muted);font-size:14px">Aucune actualité publiée pour ce club.</p>';
    return;
  }
  el.innerHTML = clubData.actualites.map(a => `
    <div class="news-card">
      <div class="news-date">${esc(a.date || a.datePublication || '')}</div>
      <div class="news-title">${esc(a.titre)}</div>
      <div class="news-body">${esc(a.corps || a.contenu)}</div>
      <span class="news-tag">${esc(a.tag || a.categorie)}</span>
    </div>`).join('');
}

function renderHoraires() {
  const el = document.getElementById('horaires-display');
  const h = clubData.horaires;

  if (!h || !Object.keys(h).length) {
    el.innerHTML = '<p style="font-size:13px;color:var(--muted)">Aucun horaire renseigné.</p>';
    return;
  }

  el.innerHTML = JOURS.map(j => {
    const jour = h[j];
    if (!jour) return '';
    const cls = jour.ouvert ? 'schedule-row' : 'schedule-row ferme';
    const txt = jour.ouvert ? `${jour.matin} – ${jour.soir}` : 'Fermé';
    return `<div class="${cls}"><span class="day">${cap(j)}</span><span>${txt}</span></div>`;
  }).join('');
}

function renderCotisations() {
  const c = clubData.cotisations;
  if (!c || (!c.adulte && !c.jeune && !c.famille)) {
    document.getElementById('cotisations-display').innerHTML =
      '<p style="font-size:13px;color:var(--muted)">Aucun tarif renseigné.</p>';
    return;
  }
  document.getElementById('cotisations-display').innerHTML = `
    ${c.adulte  ? `<div class="info-row"><span class="key">Adulte</span><span class="val">${c.adulte} €/an</span></div>` : ''}
    ${c.jeune   ? `<div class="info-row"><span class="key">Jeune (–18 ans)</span><span class="val">${c.jeune} €/an</span></div>` : ''}
    ${c.famille ? `<div class="info-row"><span class="key">Famille</span><span class="val">${c.famille} €/an</span></div>` : ''}
    ${c.note    ? `<p style="font-size:12px;color:var(--muted);margin-top:8px">${esc(c.note)}</p>` : ''}`;
}

function renderContact() {
  const c = clubData.contact;
  if (!c || (!c.adresse && !c.tel && !c.email)) {
    document.getElementById('contact-display').innerHTML =
      '<p style="font-size:13px;color:var(--muted)">Aucun contact renseigné.</p>';
    return;
  }
  document.getElementById('contact-display').innerHTML = `
    ${c.adresse ? `<div class="info-row"><span class="key">Adresse</span><span class="val">${esc(c.adresse)}</span></div>` : ''}
    ${c.tel     ? `<div class="info-row"><span class="key">Tél</span><span class="val">${esc(c.tel)}</span></div>` : ''}
    ${c.email   ? `<div class="info-row"><span class="key">Email</span><span class="val">${esc(c.email)}</span></div>` : ''}
    ${c.web     ? `<div class="info-row"><span class="key">Web</span><a href="${esc(c.web)}" target="_blank" style="color:var(--accent);font-size:12px">${esc(c.web)}</a></div>` : ''}`;
}

function renderFormulaireHoraires() {
  document.getElementById('horaires-form').innerHTML = JOURS.map(j => {
    const h = (clubData.horaires && clubData.horaires[j]) || { ouvert: false, matin: '', soir: '' };
    return `
      <div style="display:flex;align-items:center;gap:10px;margin-bottom:10px;flex-wrap:wrap">
        <label style="min-width:78px;font-size:13px;font-weight:600">${cap(j)}</label>
        <input type="checkbox" id="hor-${j}-ouvert" ${h.ouvert ? 'checked' : ''}
               onchange="toggleJour('${j}')">
        <input type="time" class="form-input" id="hor-${j}-debut" value="${h.matin || ''}"
               style="width:100px;padding:6px 10px" ${h.ouvert ? '' : 'disabled'}>
        <span style="color:var(--muted)">–</span>
        <input type="time" class="form-input" id="hor-${j}-fin" value="${h.soir || ''}"
               style="width:100px;padding:6px 10px" ${h.ouvert ? '' : 'disabled'}>
      </div>`;
  }).join('');
}

function toggleJour(j) {
  const ouvert = document.getElementById(`hor-${j}-ouvert`).checked;
  document.getElementById(`hor-${j}-debut`).disabled = !ouvert;
  document.getElementById(`hor-${j}-fin`).disabled   = !ouvert;
}

// ── ACTIONS ──────────────────────────────────────────────────
function ajouterActualite() {
  const titre = document.getElementById('news-titre').value.trim();
  const corps = document.getElementById('news-corps').value.trim();
  const tag   = document.getElementById('news-tag').value;
  if (!titre || !corps) { alert('Titre et contenu sont obligatoires.'); return; }

  clubData.actualites.unshift({
    date: new Date().toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' }),
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

  clubData.cotisations = {
    adulte:  +document.getElementById('cotis-adulte').value  || 0,
    jeune:   +document.getElementById('cotis-jeune').value   || 0,
    famille: +document.getElementById('cotis-famille').value || 0,
    note:     document.getElementById('cotis-note').value
  };

  clubData.contact = {
    adresse: document.getElementById('contact-adresse').value,
    tel:     document.getElementById('contact-tel').value,
    email:   document.getElementById('contact-email').value,
    web:     document.getElementById('contact-web').value
  };

  try {
    const resp = await fetch(API_SAVE, {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify(clubData)
    });
    if (!resp.ok) throw new Error('Erreur serveur ' + resp.status);
  } catch (e) {
    alert('Erreur lors de la sauvegarde : ' + e.message +
          '\nVérifiez que Tomcat est lancé et que la base de données est accessible.');
    return;
  }

  renderAll();
  fermerModal();
  showToast('Informations mises à jour !');
}

// ── MODAL / TABS ──────────────────────────────────────────────
function ouvrirModal() {
  const c  = clubData.cotisations;
  const ct = clubData.contact;
  document.getElementById('cotis-adulte').value    = c.adulte   || '';
  document.getElementById('cotis-jeune').value     = c.jeune    || '';
  document.getElementById('cotis-famille').value   = c.famille  || '';
  document.getElementById('cotis-note').value      = c.note     || '';
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
  const tabs = ['actualites', 'horaires', 'cotisations', 'contact'];
  document.querySelectorAll('.tab-btn').forEach((b, i) =>
    b.classList.toggle('active', tabs[i] === name));
  document.querySelectorAll('.tab-panel').forEach(p =>
    p.classList.toggle('active', p.id === 'tab-' + name));
}

document.addEventListener('DOMContentLoaded', () => {
  const overlay = document.getElementById('modal-overlay');
  if (overlay) overlay.addEventListener('click', e => {
    if (e.target === e.currentTarget) fermerModal();
  });
});

// ── ERREUR GLOBALE ───────────────────────────────────────────
function afficherErreur(message) {
  document.getElementById('hero-nom').textContent = 'Erreur de chargement';
  document.getElementById('hero-fed').textContent = '';
  document.getElementById('news-list').innerHTML  = `
    <div style="background:#fff0f0;border:1px solid #e85d75;border-radius:12px;
                padding:20px;text-align:center;color:#e85d75;font-size:14px">
      ⚠️ ${message}
    </div>`;
}

// ── UTILITAIRES ──────────────────────────────────────────────
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
/**
 * app.js — SportIF
 * Gère : chargement CSV, recherche clubs, stats licenciés, espace communication clubs
 */

// ==============================
// STATE GLOBAL
// ==============================
const STATE = {
    licData: [],       // données lic-data-2019.csv parsées
    clubData: [],      // données clubs-data-2019.csv parsées
    federations: [],   // liste unique des fédérations
    regions: [],       // liste unique des régions
    publications: [],  // publications clubs (stockées localement)
    currentFilter: 'all'
};

// ==============================
// UTILITAIRES CSV
// ==============================

/**
 * Parse un fichier CSV (séparateur virgule ou point-virgule)
 * Retourne un tableau d'objets avec les headers comme clés
 */
function parseCSV(text) {
    const lines = text.split('\n').filter(l => l.trim());
    if (lines.length === 0) return [];

    // Détecter le séparateur
    const sep = lines[0].includes(';') ? ';' : ',';
    const headers = lines[0].split(sep).map(h => h.trim().replace(/^"|"$/g, '').toLowerCase());

    return lines.slice(1).map(line => {
        const vals = line.split(sep).map(v => v.trim().replace(/^"|"$/g, ''));
        const obj = {};
        headers.forEach((h, i) => { obj[h] = vals[i] ?? ''; });
        return obj;
    });
}

/**
 * Normalise un string pour comparaison insensible à la casse et aux accents
 */
function normalize(str) {
    return (str || '').toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
}

// ==============================
// CHARGEMENT DES DONNÉES
// ==============================
async function loadData() {
    try {
        // On essaie de charger les CSV depuis le même dossier
        const [licResp, clubResp] = await Promise.all([
            fetch('lic-data-2019.csv').catch(() => null),
            fetch('clubs-data-2019.csv').catch(() => null)
        ]);

        if (licResp && licResp.ok) {
            const text = await licResp.text();
            STATE.licData = parseCSV(text);
            console.log(`✅ lic-data-2019.csv chargé : ${STATE.licData.length} lignes`);
        } else {
            console.warn('⚠️ lic-data-2019.csv non trouvé — utilisation de données de démonstration');
            STATE.licData = generateMockLicData();
        }

        if (clubResp && clubResp.ok) {
            const text = await clubResp.text();
            STATE.clubData = parseCSV(text);
            console.log(`✅ clubs-data-2019.csv chargé : ${STATE.clubData.length} lignes`);
        } else {
            console.warn('⚠️ clubs-data-2019.csv non trouvé — utilisation de données de démonstration');
            STATE.clubData = generateMockClubData();
        }

        // Extraire les listes uniques
        buildFederationList();
        buildRegionList();
        populateSelects();

    } catch (err) {
        console.error('Erreur chargement CSV :', err);
        STATE.licData = generateMockLicData();
        STATE.clubData = generateMockClubData();
        buildFederationList();
        buildRegionList();
        populateSelects();
    }
}

// ==============================
// DONNÉES MOCK (si CSV absents)
// ==============================
function generateMockLicData() {
    const communes = [
        { code: '75056', libelle: 'Paris', region: 'Île-de-France', dep: '75' },
        { code: '13055', libelle: 'Marseille', region: "Provence-Alpes-Côte d'Azur", dep: '13' },
        { code: '69123', libelle: 'Lyon', region: 'Auvergne-Rhône-Alpes', dep: '69' },
        { code: '31555', libelle: 'Toulouse', region: 'Occitanie', dep: '31' },
        { code: '06088', libelle: 'Nice', region: "Provence-Alpes-Côte d'Azur", dep: '06' },
        { code: '44109', libelle: 'Nantes', region: 'Pays de la Loire', dep: '44' },
        { code: '33063', libelle: 'Bordeaux', region: 'Nouvelle-Aquitaine', dep: '33' },
        { code: '67482', libelle: 'Strasbourg', region: 'Grand Est', dep: '67' },
        { code: '34172', libelle: 'Montpellier', region: 'Occitanie', dep: '34' },
        { code: '59350', libelle: 'Lille', region: 'Hauts-de-France', dep: '59' },
    ];
    const feds = [
        { code: '111', nom: 'FF de Football' },
        { code: '113', nom: 'FF de Gymnastique' },
        { code: '117', nom: 'FF de Judo' },
        { code: '115', nom: 'FF de Handball' },
        { code: '101', nom: "FF d'Athlétisme" },
        { code: '103', nom: 'FF de Badminton' },
        { code: '133', nom: 'FF de Rugby' },
        { code: '122', nom: 'FF de Taekwondo' },
        { code: '123', nom: 'FF de Tennis' },
        { code: '127', nom: 'FF de Triathlon' },
    ];
    const rows = [];
    communes.forEach(c => {
        feds.forEach(f => {
            const total = Math.floor(Math.random() * 1200) + 50;
            const femmes = Math.floor(total * (0.2 + Math.random() * 0.6));
            const hommes = total - femmes;
            rows.push({
                code_commune: c.code,
                libelle: c.libelle,
                region: c.region,
                fed_2019: f.code,
                nom_fed: f.nom,
                l_2019: String(total),
                l_f_2019: String(femmes),
                l_h_2019: String(hommes),
            });
        });
    });
    return rows;
}

function generateMockClubData() {
    return STATE.licData.map(row => ({
        code_commune: row.code_commune,
        commune: row.libelle,
        region: row.region,
        code_federation: row.fed_2019,
        nom_federation: row.nom_fed,
        total_clubs_2019: String(Math.floor(Math.random() * 10) + 1),
        clubs_sportifs_2019: String(Math.floor(Math.random() * 8) + 1),
    }));
}

// ==============================
// CONSTRUCTION DES LISTES
// ==============================
function buildFederationList() {
    const seen = new Set();
    const source = STATE.licData.length ? STATE.licData : STATE.clubData;
    source.forEach(row => {
        const code = row.fed_2019 || row.code_federation || '';
        const nom = row.nom_fed || row.nom_federation || '';
        if (code && !seen.has(code)) {
            seen.add(code);
            STATE.federations.push({ code, nom });
        }
    });
    STATE.federations.sort((a, b) => a.nom.localeCompare(b.nom));
}

function buildRegionList() {
    const seen = new Set();
    const source = STATE.licData.length ? STATE.licData : STATE.clubData;
    source.forEach(row => {
        const r = row.region || '';
        if (r && !seen.has(r)) {
            seen.add(r);
            STATE.regions.push(r);
        }
    });
    STATE.regions.sort();
}

function populateSelects() {
    // Fédérations
    const fedSelects = ['fed-select-geo', 'fed-select-radius', 'fed-select-all', 'stats-fed-select', 'pub-fed-select'];
    fedSelects.forEach(id => {
        const el = document.getElementById(id);
        if (!el) return;
        STATE.federations.forEach(f => {
            const opt = document.createElement('option');
            opt.value = f.code;
            opt.textContent = f.nom;
            el.appendChild(opt);
        });
    });

    // Régions
    const regSelect = document.getElementById('region-select');
    if (regSelect) {
        STATE.regions.forEach(r => {
            const opt = document.createElement('option');
            opt.value = r;
            opt.textContent = r;
            regSelect.appendChild(opt);
        });
    }
}

// ==============================
// RECHERCHE CLUBS
// ==============================
function searchByGeo() {
    const commune = normalize(document.getElementById('commune-input').value);
    const region = document.getElementById('region-select').value;
    const fedCode = document.getElementById('fed-select-geo').value;

    if (!commune && !region && !fedCode) {
        showFeedback('Renseignez au moins un critère de recherche.', 'error-inline');
        return;
    }

    let results = STATE.licData.filter(row => {
        const matchCommune = commune ? normalize(row.libelle).includes(commune) || normalize(row.code_commune).includes(commune) : true;
        const matchRegion  = region  ? row.region === region : true;
        const matchFed     = fedCode ? (row.fed_2019 === fedCode || row.code_federation === fedCode) : true;
        return matchCommune && matchRegion && matchFed;
    });

    displayResults(results);
    if (typeof window.showMarkersOnMap === 'function') window.showMarkersOnMap(results);
}

function searchAllByFed() {
    const fedCode = document.getElementById('fed-select-all').value;
    let results = STATE.licData;
    if (fedCode) results = results.filter(r => (r.fed_2019 || r.code_federation) === fedCode);
    displayResults(results.slice(0, 200)); // limiter à 200 pour perf
    if (typeof window.showMarkersOnMap === 'function') window.showMarkersOnMap(results.slice(0, 200));
}

function displayResults(data) {
    const countEl = document.getElementById('results-count');
    const listEl  = document.getElementById('results-list');

    if (!data.length) {
        countEl.textContent = 'Aucun résultat trouvé.';
        listEl.innerHTML = `<div class="empty-state"><span>🔍</span>Aucun club ne correspond à votre recherche.</div>`;
        return;
    }

    countEl.textContent = `${data.length} résultat${data.length > 1 ? 's' : ''} trouvé${data.length > 1 ? 's' : ''}`;

    // Dédupliquer par commune + fédération pour l'affichage carte
    const grouped = {};
    data.forEach(row => {
        const key = `${row.code_commune || row.libelle}_${row.fed_2019 || row.code_federation}`;
        if (!grouped[key]) grouped[key] = row;
    });

    listEl.innerHTML = Object.values(grouped).slice(0, 60).map(row => {
        const total = parseInt(row.l_2019 || 0);
        const femmes = parseInt(row.l_f_2019 || 0);
        const hommes = parseInt(row.l_h_2019 || 0);
        return `
        <div class="result-card">
            <div class="result-card-fed">${row.nom_fed || row.nom_federation || 'Fédération inconnue'}</div>
            <div class="result-card-commune">${row.libelle || row.commune || '—'}</div>
            <div class="result-card-region">${row.region || '—'}</div>
            <div class="result-card-count">
                <span>👥 <strong>${total.toLocaleString('fr-FR')}</strong> licenciés</span>
                <span>♂ <strong>${hommes.toLocaleString('fr-FR')}</strong></span>
                <span>♀ <strong>${femmes.toLocaleString('fr-FR')}</strong></span>
            </div>
        </div>`;
    }).join('');
}

// ==============================
// STATS LICENCIÉS
// ==============================
function searchStats() {
    const commune = normalize(document.getElementById('stats-commune-input').value);
    const fedCode = document.getElementById('stats-fed-select').value;

    if (!commune) {
        alert('Veuillez saisir une commune.');
        return;
    }

    let results = STATE.licData.filter(row => {
        const matchCommune = normalize(row.libelle).includes(commune) || normalize(row.code_commune).includes(commune);
        const matchFed = fedCode ? (row.fed_2019 === fedCode || row.code_federation === fedCode) : true;
        return matchCommune && matchFed;
    });

    const el = document.getElementById('stats-results');

    if (!results.length) {
        el.innerHTML = `<div class="empty-state" style="grid-column:1/-1"><span>📊</span>Aucune donnée pour cette commune.</div>`;
        return;
    }

    el.innerHTML = results.map(row => {
        const total  = parseInt(row.l_2019 || 0);
        const femmes = parseInt(row.l_f_2019 || 0);
        const hommes = parseInt(row.l_h_2019 || 0);
        const pctH   = total > 0 ? Math.round((hommes / total) * 100) : 0;
        const pctF   = 100 - pctH;

        return `
        <div class="stat-card">
            <div class="stat-card-fed">${row.nom_fed || row.nom_federation || '—'}</div>
            <div class="stat-card-commune">${row.libelle || row.commune || '—'} — ${row.region || ''}</div>
            <div class="stat-total">${total.toLocaleString('fr-FR')}</div>
            <div class="stat-total-lbl">licenciés au total</div>
            <div class="gender-bar-wrap">
                <div class="gender-bar">
                    <div class="gender-bar-h" style="width:${pctH}%"></div>
                    <div class="gender-bar-f" style="width:${pctF}%"></div>
                </div>
                <div class="gender-legend">
                    <span><span class="dot-h"></span> Hommes ${hommes.toLocaleString('fr-FR')} (${pctH}%)</span>
                    <span><span class="dot-f"></span> Femmes ${femmes.toLocaleString('fr-FR')} (${pctF}%)</span>
                </div>
            </div>
        </div>`;
    }).join('');
}

// ==============================
// ESPACE COMMUNICATION CLUBS
// ==============================
const TYPE_LABELS = {
    actu: 'Actualité',
    horaire: 'Horaires',
    cotisation: 'Cotisations',
    autre: 'Autre'
};

function getSelectedType() {
    const checked = document.querySelector('input[name="pub-type"]:checked');
    return checked ? checked.value : 'actu';
}

function publishPost() {
    const club    = document.getElementById('pub-club-name').value.trim();
    const fedCode = document.getElementById('pub-fed-select').value;
    const title   = document.getElementById('pub-title').value.trim();
    const content = document.getElementById('pub-content').value.trim();
    const contact = document.getElementById('pub-contact').value.trim();
    const type    = getSelectedType();

    const feedback = document.getElementById('publish-feedback');

    if (!club || !title || !content) {
        feedback.textContent = 'Merci de remplir les champs obligatoires (*).';
        feedback.className = 'error';
        return;
    }

    const fedNom = fedCode
        ? (STATE.federations.find(f => f.code === fedCode)?.nom || '')
        : '';

    const pub = {
        id: Date.now(),
        club,
        fedCode,
        fedNom,
        type,
        title,
        content,
        contact,
        date: new Date().toLocaleDateString('fr-FR', { day: '2-digit', month: 'long', year: 'numeric' })
    };

    STATE.publications.unshift(pub);

    feedback.textContent = '✅ Publication ajoutée avec succès !';
    feedback.className = 'success';
    setTimeout(() => { feedback.style.display = 'none'; feedback.className = ''; }, 3000);

    // Reset form
    document.getElementById('pub-club-name').value = '';
    document.getElementById('pub-fed-select').value = '';
    document.getElementById('pub-title').value = '';
    document.getElementById('pub-content').value = '';
    document.getElementById('pub-contact').value = '';
    document.querySelector('input[name="pub-type"][value="actu"]').checked = true;

    renderFeed();
}

function renderFeed() {
    const el = document.getElementById('publications-feed');
    const filter = STATE.currentFilter;

    const toShow = filter === 'all'
        ? STATE.publications
        : STATE.publications.filter(p => p.type === filter);

    if (!toShow.length) {
        el.innerHTML = `<div class="empty-state"><span>📢</span>Aucune publication pour l'instant.<br>Soyez le premier à informer le grand public !</div>`;
        return;
    }

    el.innerHTML = toShow.map(p => `
        <div class="pub-card" data-id="${p.id}">
            <button class="pub-delete-btn" onclick="deletePost(${p.id})" title="Supprimer">✕</button>
            <span class="pub-card-type pub-type-${p.type}">${TYPE_LABELS[p.type]}</span>
            <div class="pub-card-title">${escapeHtml(p.title)}</div>
            <div class="pub-card-club">${escapeHtml(p.club)}${p.fedNom ? ' — ' + escapeHtml(p.fedNom) : ''}</div>
            <div class="pub-card-body">${escapeHtml(p.content)}</div>
            <div class="pub-card-meta">
                <span>${p.date}</span>
                ${p.contact ? `<span>📞 ${escapeHtml(p.contact)}</span>` : ''}
            </div>
        </div>
    `).join('');
}

function deletePost(id) {
    STATE.publications = STATE.publications.filter(p => p.id !== id);
    renderFeed();
}

function escapeHtml(str) {
    return (str || '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}

// ==============================
// TABS
// ==============================
function initTabs() {
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const tab = btn.dataset.tab;
            document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
            btn.classList.add('active');
            document.getElementById('tab-' + tab)?.classList.add('active');
        });
    });
}

function initFeedFilters() {
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            STATE.currentFilter = btn.dataset.filter;
            document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            renderFeed();
        });
    });
}

// ==============================
// EVENTS
// ==============================
function bindEvents() {
    document.getElementById('search-geo-btn')?.addEventListener('click', searchByGeo);
    document.getElementById('search-all-btn')?.addEventListener('click', searchAllByFed);
    document.getElementById('stats-search-btn')?.addEventListener('click', searchStats);
    document.getElementById('publish-btn')?.addEventListener('click', publishPost);
    document.getElementById('clear-btn')?.addEventListener('click', () => {
        ['pub-club-name','pub-title','pub-content','pub-contact'].forEach(id => {
            const el = document.getElementById(id);
            if (el) el.value = '';
        });
        document.getElementById('pub-fed-select').value = '';
        document.querySelector('input[name="pub-type"][value="actu"]').checked = true;
        const fb = document.getElementById('publish-feedback');
        fb.style.display = 'none'; fb.className = '';
    });

    // Enter sur les inputs de recherche
    ['commune-input', 'region-select', 'fed-select-geo'].forEach(id => {
        document.getElementById(id)?.addEventListener('keydown', e => {
            if (e.key === 'Enter') searchByGeo();
        });
    });
    ['stats-commune-input'].forEach(id => {
        document.getElementById(id)?.addEventListener('keydown', e => {
            if (e.key === 'Enter') searchStats();
        });
    });
}

// ==============================
// INIT
// ==============================
async function init() {
    initTabs();
    initFeedFilters();
    bindEvents();
    renderFeed(); // feed vide initial
    await loadData();

    // Indiquer au map.js que les données sont prêtes
    if (typeof window.onDataReady === 'function') window.onDataReady(STATE.licData);
}

document.addEventListener('DOMContentLoaded', init);

// Exposer l'état pour map.js
window.APP_STATE = STATE;
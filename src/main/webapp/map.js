

const franceBounds = [
    [41.0, -5.5], // Coin Sud-Ouest (vers l'Espagne/Océan)
    [51.5, 10.0]  // Coin Nord-Est (vers l'Allemagne/Belgique)
];


const map = L.map('map', {
    center: [46.603354, 1.888334], // Centre de la France
    zoom: 6,                       // Zoom initial
    minZoom: 6,                    // Empêche de dézoomer plus loin que le niveau 6
    maxBounds: franceBounds,       // Applique la boîte de restriction
    maxBoundsViscosity: 1.0        // Effet "mur" : empêche totalement de glisser hors des limites
});


L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '© <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
}).addTo(map);

// Variable pour stocker le cercle visuel sur la carte afin de pouvoir le supprimer/modifier
let searchCircle;


document.getElementById('search-btn').addEventListener('click', () => {
    // Récupération des valeurs tapées par l'utilisateur
    const location = document.getElementById('location-input').value;
    const radius = document.getElementById('radius-input').value;

    
    if (!location || !radius) {
        alert("Veuillez renseigner une ville (ou code postal) et un rayon en kilomètres.");
        return;
    }


    const simulatedLat = 46.603354;
    const simulatedLng = 1.888334;

    
    const radiusInMeters = radius * 1000;

    // Si un cercle existait déjà d'une recherche précédente, on l'efface
    if (searchCircle) {
        map.removeLayer(searchCircle);
    }


    searchCircle = L.circle([simulatedLat, simulatedLng], {
        color: '#2563eb',       // Bordure bleue
        fillColor: '#3b82f6',   // Remplissage bleu clair
        fillOpacity: 0.2,       // Transparence
        radius: radiusInMeters
    }).addTo(map);

    
    map.fitBounds(searchCircle.getBounds());

    console.log(`Logique prête : Prêt à envoyer la ville "${location}" et le rayon "${radius}km" au backend Java.`);
});
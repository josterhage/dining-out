

function initMap() {
    const tmac = { lat: 31.5535, lng: -110.3285 };
    const map = new google.maps.Map(document.getElementById("map"), {
        zoom: 15,
        center: tmac,
    });
    const marker = new google.maps.Marker({
        position: tmac,
        map: map,
    });
}

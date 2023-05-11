// This example requires the Places library. Include the libraries=places
// parameter when you first load the API. For example:
// <script src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&libraries=places">

let map;
let service;
let infowindow;
let markerIdCounter = 0;
let markers = [];

function initMap() {
    const sydney = new google.maps.LatLng(37.555, 126.9725);

    infowindow = new google.maps.InfoWindow();
    map = new google.maps.Map(document.getElementById("map"), {
        center: sydney,
        zoom: 15,
    });

    map.addListener("click", (event) => {
        addMarker(event.latLng);
    });


    const request = {
        location: {
            "lat": 37.558657268544984,
            "lng": 126.98636165618896
        },
        radius: 1, // 1km 반경 내의 장소 검색
    };
    //    for (let i = 0; i < test.length; i++) {
    //        addMarker(test[i]);
    //    }


    service = new google.maps.places.PlacesService(map);
    service.nearbySearch(request, (results, status) => {
        if (status === google.maps.places.PlacesServiceStatus.OK) {

        }
    });
}



function addMarker(position) {
    // 마커 생성
    const marker = new google.maps.Marker({
        position: position,
        map: map,
        id: markerIdCounter++,
    });

    // Reverse Geocoding API 호출
    const geocoder = new google.maps.Geocoder();
    geocoder.geocode({ location: position }, (results, status) => {
        if (status === "OK") {
            // placeId 추출
            const placeId = results[0].place_id;
            marker.set("placeId", placeId);
            // 장소 정보 가져오기
            const request = {
                placeId: placeId,
                fields: ['name', 'rating', 'formatted_phone_number', 'geometry'],
            };
            const placesService = new google.maps.places.PlacesService(map);
            placesService.getDetails(request, (place, status) => {
                place.rating
                const lat = place.geometry.location.lat()
                const lng = place.geometry.location.lng()
                console.log("lat : " + lat)
                console.log("lng : " + lng)
                console.log("name : " + place.name)
                console.log("rating : " + place.rating)
                console.log("place : " + place)

                const placeRating = place.rating === undefined ? 0 : place.rating
                const placeName = place.name === undefined ? "알수없음" : place.name
                console.log("placeRating : " + placeRating)
                console.log("placeName : " + placeName)

                const content = "<form action='http://localhost:8080/places/save' method='post'>" +
                    "<label for='placeId'>아이디</label><input name='placeId' id='placeId' value='" + placeId + "' /></br>" +
                    "<label for='phone'>전화번호</label><input name='phoneNumber' id='phone' value='" + place.formatted_phone_number + "'/></br>" +
                    "<label for='lat'>위도</label><input name='latitude' id='lat' value='" + lat + "'/></br>" +
                    "<label for='longi'>경도</label><input name='longitude' id='longi' value='" + lng + "'/></br>" +
                    "<label for='rating'>별점</label><input name='rating' id='rating' value='" + placeRating + "'/></br>" +
                    "<label for='name'>이름</label><input name='name' id='name' value='" + placeName + "' /></br>" +
                    "<input type='submit' value='저장'/>" +
                    "<input type='button' value='삭제' onClick='deleteMarkerById()' />" +
                    "</form>"


                if (status === google.maps.places.PlacesServiceStatus.OK) {
                    // 마커 클릭 시 장소 정보 보여주기

                    marker.addListener("click", () => {
                        const infowindow = new google.maps.InfoWindow({
                            //인포윈도우 내용
                            content: content,
                        });

                        infowindow.open({
                            anchor: marker,
                            map,
                        });
                    });
                }
            });
            //  console.log(marker);
        }
    });

    function deleteMarkerById(id) {
        for (let i = 0; i < markers.length; i++) {
            if (markers[i].id === id) {
                markers[i].setMap(null); // 해당 ID를 가진 마커 삭제
                markers.splice(i, 1);
                break;
            }
        }
    }


    markers.push(marker);
}


window.initMap = initMap;
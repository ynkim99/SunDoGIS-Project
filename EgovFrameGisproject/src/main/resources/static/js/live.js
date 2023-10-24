let liveLine, livePoint, liveVectorSource;
let fmtToday;
let intervalID;

window.addEventListener('load', function() {

    let today = new Date();
    let year = today.getFullYear();
    let month = today.getMonth() + 1;
    let date = today.getDate();
    fmtToday = year + "-" + month + "-" + date;

    let tabBtn = document.querySelector('.tab-btn');
    let liveBtn = document.querySelector('[data-toggle="toggle"]');
    let offCanvas = document.getElementById("offcanvasScrolling");
    let mapSize = document.querySelector('.map');
    let timer = document.querySelector('.timer');
    let offCanvasObj = new bootstrap.Offcanvas(offCanvas);
    let csvDownload = document.querySelector('.download-btn');


    liveBtn.addEventListener('click', function () {
        if (liveBtn.classList.contains('off')) {
            // location.reload();
            console.log("live stop")
            offCanvasObj.show();
            // csvDownload.style.display = 'block';
            timer.classList.remove('timer-show');
            map.removeLayer(livePoint);
            map.removeLayer(liveLine);
            map.removeLayer(liveVectorSource);

            mapSize.classList.add('map-active')
            mapSize.classList.remove('map-non-active');
            tabBtn.setAttribute('data-bs-target', '#offcanvasScrolling');
            tabBtn.setAttribute('data-bs-toggle', 'offcanvas');

            map.getView().animate({
                center: ol.proj.transform([127.1213408459, 37.2674315832], 'EPSG:4326', 'EPSG:3857'),
                zoom: 12,
                duration: 1500
            });

            clearInterval(intervalID);
            console.log("interval 중지");

        } else {

            console.log("live start")
            // csvDownload.style.display = 'none';
            timer.classList.add('timer-show');
            livePointLayer(fmtToday);
            liveLineLayer(fmtToday);
            liveGpsPoint(fmtToday);

            map.removeLayer(cleanPoint);
            map.removeLayer(cleanLine);

            map.addLayer(liveLine);
            map.addLayer(livePoint);

            intervalID = setInterval(function (){
                map.removeLayer(livePoint);
                map.removeLayer(liveLine);

                liveUpdateLayer(fmtToday)

                map.addLayer(liveLine);
                map.addLayer(livePoint);


            }, 2000);

            // intervalID = setInterval(liveUpdateLayer(fmtToday), 2000);

            tabBtn.removeAttribute('data-bs-target');
            tabBtn.removeAttribute('data-bs-toggle');
            offCanvasObj.hide();
            mapSize.classList.remove('map-active')
            mapSize.classList.add('map-non-active')
        }
        console.log("today = " + fmtToday);
    });

    let setMinute = document.querySelectorAll('.set-minute');
    let minute;

    setMinute.forEach(function (btn) {
        btn.addEventListener('click', function (e) {
            minute = e.target.id;
            console.log("minute = " + minute);

            $.ajax({
                type: 'post',
                url: '/api/sensor/cycle',
                // dataType: 'json',
                // contentType: 'application/json; charset=utf-8',
                data: minute,
                success: function (result) {
                    console.log("success");
                },
                error: function (error) {
                    console.log(error);
                },
            });

        })
    })

});



function liveUpdateLayer(today) {
    map.removeLayer(liveVectorSource);

    const url = 'http://localhost:8099/geoserver/wms';

    const liveLineParams = {
        'TIME': Date.now(),
    };

    const livePointParams = {
        'TIME': Date.now(),
    };


    livePoint.getSource().setUrl(url);
    livePoint.getSource().updateParams(liveLineParams);
    // livePoint.getSource().setParams(liveLineParams);
    livePoint.changed();


    liveLine.getSource().setUrl(url);
    liveLine.getSource().updateParams(livePointParams);
    // liveLine.getSource().setParams(livePointParams);
    liveLine.changed();

    liveGpsPoint(today);
    map.render();
    console.log("update Layer");
}


function liveGpsPoint(today) {

    let carNum;

    $.ajax({
        type: 'post',
        url: '/api/live/gps',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify(today),
        success: function (result) {
            console.log("success");
            console.log(result.carNum + ", " + result.lon +  ", "+ result.lat)
            carNum = result.carNum;
            let point = [result.lon, result.lat];
            getPoint(point);
        },
        error: function (error) {
            console.log("live Gps Error");
            console.log(error);
        },
    });
}

function getPoint(point) {
    let tempLivePoint = new ol.Feature({
        geometry: new ol.geom.Point(point).transform('EPSG:4326', 'EPSG:3857'),
        name: 'LiveGpsPoint',
        population: 4000,
        rainfall: 500
    });

    let style = new ol.style.Style({
        image: new ol.style.Icon({
            anchor: [0.5, 46],
            anchorXUnits: 'fraction',
            anchorYUnits: 'pixels',
            src: 'img/clean_car.png',
            scale: 0.07,
        }),
        zindex: 10
    });

    tempLivePoint.setStyle(style);
    let vectorSource = new ol.source.Vector({
        features: [tempLivePoint]
    });

    liveVectorSource = new ol.layer.Vector({
        source: vectorSource
    });

    map.addLayer(liveVectorSource);
    map.getView().animate({
        center: ol.proj.transform(point, 'EPSG:4326', 'EPSG:3857'),
        zoom: 18,
        duration: 1000
    });
}

function liveLineLayer(today) {
    liveLine = new ol.layer.Tile({
        source: new ol.source.TileWMS({
            url: 'http://localhost:8099/geoserver/wms',
            params: {
                'LAYERS': 'liveLine',
                'TILED': true,
                'VIEWPARAMS': 'date:' + today + ";"
            },
            serverType: 'geoserver',
        })
    });
}

function livePointLayer(today) {
    livePoint = new ol.layer.Tile({
        source: new ol.source.TileWMS({
            url: 'http://localhost:8099/geoserver/wms',
            params: {
                'LAYERS': 'livePoint',
                'TILED': true,
                'VIEWPARAMS': 'date:' + today + ";"
            },
            serverType: 'geoserver',
        })
    });
}
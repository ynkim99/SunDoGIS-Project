let dateList;
let map;
let carNum;
let cleanLine;
let cleanPoint;
let cleanRoute;
let yonginGu;
let startPoint, endPoint;

window.addEventListener("load", function(){

    buildCalendar();
    let tabBtn = document.querySelector('.tab-btn');
    let offCanvas = document.getElementById("offcanvasScrolling");
    let mapSize = document.querySelector('.map');
    let liveBtn = document.querySelector('[data-toggle="toggle"]');

    tabBtn.addEventListener('click', function () {


        if (!liveBtn.classList.contains('off')) {
            alert('라이브를 비활성화 시켜주세요.');

            return;
        }

        if (offCanvas.classList.contains('show')) {
            mapSize.classList.remove('map-active');
            mapSize.classList.add('map-non-active');
        } else {
            mapSize.classList.add('map-active');
            mapSize.classList.remove('map-non-active');
        }
    });

    let baseUrl = 'https://api.vworld.kr/req/wmts/1.0.0/01FC9396-78C3-3A58-99A4-EF97461DFFEE/Base/{z}/{y}/{x}.png';
    let hybridUrl = 'https://api.vworld.kr/req/wmts/1.0.0/01FC9396-78C3-3A58-99A4-EF97461DFFEE/Hybrid/{z}/{y}/{x}.png';
    let satelliteUrl = 'https://api.vworld.kr/req/wmts/1.0.0/01FC9396-78C3-3A58-99A4-EF97461DFFEE/Satellite/{z}/{y}/{x}.jpeg'

    map = new ol.Map({
        target: 'map',
        layers: [
            new ol.layer.Tile({
                source: new ol.source.XYZ({
                    url: baseUrl
                })
            })
        ],
        view: new ol.View({
            center: ol.proj.transform([127.2218671287, 37.2214676543], 'EPSG:4326', 'EPSG:3857'),
            zoom: 12,
            minZoom: 0,
            maxZoom: 21,

        })
    });

    let mapTypeBtns = document.querySelectorAll(".map-type");
    let regionTypeBtns = document.querySelectorAll(".region-type");

    mapTypeBtns.forEach(function (btn) {
        btn.addEventListener('click', function (e) {
            console.log(e.target.id);
            if (e.target.id === 'base') {
                map.getLayers().getArray()[0].getSource().setUrl(baseUrl);
            } else if (e.target.id === 'hybrid') {
                map.getLayers().getArray()[0].getSource().setUrl(hybridUrl);
            } else if (e.target.id === 'satellite') {
                map.getLayers().getArray()[0].getSource().setUrl(satelliteUrl);
            }

            mapTypeBtns.forEach(function (button) {
                button.classList.remove('bg-primary', 'text-white');
                button.classList.add('bg-white');
            });

            e.target.classList.remove('bg-white');
            e.target.classList.add('bg-primary', 'text-white');
        });
    });

    let currentLayer;
    let currentLayerBtn;

    regionTypeBtns.forEach(function (btn) {
        btn.addEventListener('click', function (e) {

            if (currentLayer) {
                map.removeLayer(currentLayer);
            }

            if (e.target === currentLayerBtn) {
                currentLayer = null;
                currentLayerBtn = null;

                e.target.classList.remove('bg-primary', 'text-white');
                e.target.classList.add('bg-white');

            } else {

                let gu = e.target.id;
                console.log("gu = ", gu);

                switch (gu) {
                    case 'giheung':
                        currentLayer = guLayer('giheung');
                        map.getView().animate({
                            center: ol.proj.transform([127.1213408459, 37.2674315832], 'EPSG:4326', 'EPSG:3857'),
                            zoom: 12,
                            duration: 1000
                        });
                        currentLayerBtn = e.target;
                        break;
                    case 'cheoin':
                        currentLayer = guLayer('cheoin');
                        map.getView().animate({
                            center: ol.proj.transform([127.2529331499, 37.2033318957], 'EPSG:4326', 'EPSG:3857'),
                            zoom: 12,
                            duration: 1000
                        });
                        currentLayerBtn = e.target;
                        break;
                    case 'suji':
                        currentLayer = guLayer('suji');
                        map.getView().animate({
                            center: ol.proj.transform([127.0715510732, 37.3334474297], 'EPSG:4326', 'EPSG:3857'),
                            zoom: 12,
                            duration: 1000
                        });
                        currentLayerBtn = e.target;
                        break;
                }
                map.addLayer(currentLayer);

                regionTypeBtns.forEach(function (button) {
                    button.classList.remove('bg-primary', 'text-white');
                    button.classList.add('bg-white');
                });

                e.target.classList.remove('bg-white');
                e.target.classList.add('bg-primary', 'text-white');
            }
        });
    });

    //레이어 추가
    let yongin = new ol.layer.Tile({
        source: new ol.source.TileWMS({
            url: 'http://localhost:8099/geoserver/wms',
            params: {
                'LAYERS': 'yongin',
                'TILED': true,

            },
            serverType: 'geoserver',
        })
    });
    map.addLayer(yongin)
    yongin.setOpacity(0.3);

    let carNumGroup = document.querySelector('#carNumGroup');
    let dates = document.querySelectorAll('.date');

    carNumGroup.addEventListener('change', function(){

        let options = carNumGroup.options[carNumGroup.selectedIndex];

        if (!options.hasAttribute('selected')) {
            carNum = options.text;
            console.log("carNum = " + carNum);
        }

        $.ajax({
            type: 'post',
            url: '/api/dates',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: carNum,
            success: function (result) {
                let dates = document.querySelectorAll('.date');

                for (let date of dates) {
                    if (date.classList.contains('text-bg-primary')){
                        date.classList.remove('text-bg-primary', 'border', 'border-white', 'custom-green', 'select-date');
                        map.removeLayer(startPoint);
                        map.removeLayer(endPoint);
                        map.removeLayer(cleanRoute);
                        map.removeLayer(cleanLine);
                        map.removeLayer(cleanPoint);
                    }
                }

                dateList = result;
                console.log("dateList = " + dateList);
                goToCleanDate(dateList[0]);
                findDateList(dateList, carNum);

            },
            error: function (error) {
                console.log(error);
            },
        });
    });
});

function cleanLineLayer(cleanDate, carNum) {
    cleanLine = new ol.layer.Tile({
        source: new ol.source.TileWMS({
            url: 'http://localhost:8099/geoserver/wms',
            params: {
                'LAYERS': 'selectCleanLine',
                'TILED': true,
                'VIEWPARAMS': 'date:' + cleanDate + ';carNum:' + carNum
            },
            serverType: 'geoserver',
        })
    });
}

function cleanPointLayer(cleanDate, carNum) {
    cleanPoint = new ol.layer.Tile({
        source: new ol.source.TileWMS({
            url: 'http://localhost:8099/geoserver/wms',
            params: {
                'LAYERS': 'selectCleanPoint',
                'TILED': true,
                'VIEWPARAMS': 'date:' + cleanDate + ';carNum:' + carNum

            },
            serverType: 'geoserver',
        })
    });
}

let today = new Date();
let date = new Date();

function prevCalendar() {//이전 달

    today = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
    buildCalendar();
    if (dateList != null) {
        findDateList(dateList, carNum);
    }
}

function nextCalendar() {//다음 달
    today = new Date(today.getFullYear(), today.getMonth() + 1, today.getDate());
    buildCalendar();
    if (dateList != null) {
        findDateList(dateList, carNum);
    }
}

function buildCalendar(){//현재 달 달력 만들기
    let firstDate = new Date(today.getFullYear(),today.getMonth(),1);
    console.log("firstDate = " + firstDate);
    let lastDate = new Date(today.getFullYear(),today.getMonth()+1,0);
    console.log("lastDate = " + lastDate);
    let tbCalendar = document.getElementById("calendar");
    let tbCalendarYM = document.getElementById("tbCalendarYM");
    tbCalendarYM.innerHTML = today.getFullYear() + "년 " + (today.getMonth() + 1) + "월";

    /*while은 이번달이 끝나면 다음달로 넘겨주는 역할*/
    while (tbCalendar.rows.length > 2) {
        tbCalendar.deleteRow(tbCalendar.rows.length-1);
    }

    let row = null;
    let cell = null;

    row = tbCalendar.insertRow();
    let cnt = 0;
    for (let i=0; i<firstDate.getDay(); i++) {
        cell = row.insertCell();
        cnt = cnt + 1;
    }

    /*달력 출력*/
    for (let i=1; i<=lastDate.getDate(); i++) {
        cell = row.insertCell();
        cell.innerHTML = i;
        cnt = cnt + 1;

        if (cnt % 7 === 1) {
            cell.innerHTML = i
            cell.classList.add('text-danger');
        }

        if (cnt%7 === 0) {
            // cell.classList.add('text-primary');
            cell.innerHTML = i;
            row = tbCalendar.insertRow();
        }

        /*오늘 날짜에 노란색*/
        if (today.getFullYear() === date.getFullYear()
            && today.getMonth() === date.getMonth()
            && i === date.getDate()) {
            cell.classList.add('text-bg-warning', 'border', 'border-white');
        }

        cell.classList.add('date');
    }
}

function findDateList(dateList, carNum) {

    for (let i = 0; i < dateList.length; i++) {

        let clString = new Date(dateList[i]);
        let clObject = new Date(dateList[i]);

        if (today.getFullYear() === clObject.getFullYear() && today.getMonth() === clObject.getMonth()) {

            let viewDate = document.querySelectorAll('.date');
            let clickDate = document.getElementById('clickDate');
            for (let date of viewDate) {

                if (+date.innerHTML === clObject.getDate()) {
                    date.classList.add('text-bg-primary', 'important', 'border', 'border-white', 'select-date');
                    date.classList.remove('text-bg-warning');

                    // if (date.classList.contains('text-bg-warning')) {
                    //     date.classList.remove('text-bg-warning');
                    // }

                    date.addEventListener('click', function() {

                        viewDate.forEach(function (date) {
                            date.classList.remove('custom-green');
                        });


                        map.removeLayer(cleanLine);
                        map.removeLayer(cleanPoint);
                        map.removeLayer(cleanRoute);
                        map.removeLayer(startPoint);
                        map.removeLayer(endPoint);

                        date.classList.add('custom-green');
                        clickDate.innerHTML = dateList[i];

                        console.log("date = " + date.innerHTML);

                        console.log("dateList = " + dateList[i]);
                        console.log("carNum = " + carNum);

                        cleanLineLayer(dateList[i], carNum);
                        cleanPointLayer(dateList[i], carNum);
                        cleanRouteLayer(dateList[i], carNum);
                        startAndEnd(dateList[i], carNum);
                        move(carNum, dateList[i]);

                        map.addLayer(cleanLine);
                        map.addLayer(cleanRoute);
                        map.addLayer(cleanPoint);
                        map.addLayer(startPoint);
                        map.addLayer(endPoint);

                        findCleanInfo(carNum, dateList[i]);

                    });
                }
            }
        }
    }
}

function findCleanInfo(carNum, date) {

    let data = {carNum, date};

    $.ajax({
        type: 'post',
        url: '/api/clean-info',
        data: JSON.stringify(data),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function (result) {

            let driveTime = document.getElementById('driveTime');
            let cleanRatio = document.getElementById('cleanRatio');
            let driveDistance = document.getElementById('driveDistance');
            let cleanDistance = document.getElementById('cleanDistance');

            driveTime.innerHTML = result.driveTime;
            cleanRatio.innerHTML = result.cleanRatio + '%';
            driveDistance.innerHTML = result.driveDistance + ' km';
            cleanDistance.innerHTML = result.cleanDistance + ' km';

        },
        errors: function (error) {
            console.log(error);
            console.log("errooooooooooorrrrrr");
        }
    });
}

function move(carNum, date) {

    let data = {carNum, date}
    console.log(data);
    let point;

    $.ajax({
        type: 'post',
        url: '/api/center-point',
        data: JSON.stringify(data),
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        success: function (result) {
            console.log("x :" + result.centroidX + " y:" + result.centroidY);
            // point = result;
            point = [result.centroidX, result.centroidY];



            // map.getView().setCenter(ol.proj.transform(point, 'EPSG:4326', 'EPSG:3857'));
            // map.getView().setZoom(15);

            let center = ol.proj.transform(point, 'EPSG:4326', 'EPSG:3857');
            map.getView().animate({
                center: center,
                zoom: 15,
                duration: 1000
            });
        },
        errors: function (error) {
            console.log(error);
        }
    });
}

function guLayer(gu) {
    yonginGu = new ol.layer.Tile({
        source: new ol.source.TileWMS({
            url: 'http://localhost:8099/geoserver/wms',
            params: {
                'LAYERS': gu,
                'TILED': true,
            },
            serverType: 'geoserver',
        })
    });
    return yonginGu;
}

function goToCleanDate(date){
    today = new Date(date);
    buildCalendar();
}

function startAndEnd(cleanDate, carNum) {
    startPoint = new ol.layer.Tile({
        source: new ol.source.TileWMS({
            url: 'http://localhost:8099/geoserver/wms',
            params: {
                'LAYERS': 'startPoint',
                'TILED': true,
                'VIEWPARAMS': 'date:' + cleanDate + ';carNum:' + carNum,

            },
            serverType: 'geoserver',
        })
    });

    endPoint = new ol.layer.Tile({
        source: new ol.source.TileWMS({
            url: 'http://localhost:8099/geoserver/wms',
            params: {
                'LAYERS': 'endPoint',
                'TILED': true,
                'VIEWPARAMS': 'date:' + cleanDate + ';carNum:' + carNum,
            },
            serverType: 'geoserver',
        })
    });
}

function cleanRouteLayer(cleanDate, carNum) {

    cleanRoute = new ol.layer.Tile({
        source: new ol.source.TileWMS({
            url: 'http://localhost:8099/geoserver/wms',
            params: {
                'LAYERS': 'selectNoCleanLine',
                'TILED': true,
                'VIEWPARAMS': 'date:' + cleanDate + ';carNum:' + carNum,

            },
            serverType: 'geoserver',
        })
    });
}
// CSV파일 추가
function submitCsvBtn(){
    const formData = new FormData();
    const gpsCsvInput = document.getElementById('gpsCsvInput');
    const noiseCsvInput = document.getElementById('noiseCsvInput');
    const frequencyCsvInput = document.getElementById('frequencyCsvInput');

    // 파일을 모두 첨부하지 않으면 실행되지 않음
    if(!gpsCsvInput.files[0] || !noiseCsvInput.files[0] || !frequencyCsvInput.files[0] ){
        alert('파일을 모두 첨부해주세요');
        return;
    }

    formData.append('gpsFile', gpsCsvInput.files[0]);
    formData.append('noiseFile', noiseCsvInput.files[0]);
    formData.append('frequencyFile', frequencyCsvInput.files[0]);

    fetch('/file',{
        method: 'POST',
        body: formData,
    })
        .then((response)=>	{
            if(!response.ok) {
                // 오류 발생시 처리
                alert('오류가 발생했습니다\n파일을 다시 확인해주세요');
                throw new Error('오류 발생');
            }})
        .then((data) =>	{
            alert('등록이 완료되었습니다.');
        })
}





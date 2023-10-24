let suji, cheoin, giheung;

suji = new ol.layer.Tile({
    source: new ol.source.TileWMS({
        url: 'http://localhost:8099/geoserver/wms',
        params: {
            'LAYERS': 'suji',
            'TILED': true,

        },
        serverType: 'geoserver',
    })
});

cheoin = new ol.layer.Tile({
    source: new ol.source.TileWMS({
        url: 'http://localhost:8099/geoserver/wms',
        params: {
            'LAYERS': 'cheoin',
            'TILED': true,

        },
        serverType: 'geoserver',
    })
});

giheung = new ol.layer.Tile({
    source: new ol.source.TileWMS({
        url: 'http://localhost:8099/geoserver/wms',
        params: {
            'LAYERS': 'giheung',
            'TILED': true,

        },
        serverType: 'geoserver',
    })
});

// function guLayer(gu) {
//
//     let yonginGu;
//
//      yonginGu = new ol.layer.Tile({
//         source: new ol.source.TileWMS({
//             url: 'http://localhost:8099/geoserver/wms',
//             params: {
//                 'LAYERS': gu,
//                 'TILED': true,
//             },
//             serverType: 'geoserver',
//         })
//     });
//
//     return yonginGu;
// }


/**
 * Created by gmo on 04/02/2016.
 */
/**
 * Created with IntelliJ IDEA.
 * User: gmo
 * Date: 13/05/14
 * Time: 12:02
 * To change this template use File | Settings | File Templates.
 */

/**
 * global data array field
 * @type {Array}
 */
var run = [];

var perf = [];

/**
 * init stage kinetic canvas
 * init route for get parameter data
 * init websocket for new notification
 */
function load (data) {


    var stagePredictionD1 = new Kinetic.Stage({
        container: "kinetic1D",
        width: 1120,
        height: 200
    });

    var stagePredictionD5 = new Kinetic.Stage({
        container: "kinetic5D",
        width: 1120,
        height: 200
    });

    var stagePredictionD20 = new Kinetic.Stage({
        container: "kinetic20D",
        width: 1220,
        height: 200
    });


    var layerChart = new Kinetic.Layer();
    var layerChartD5 = new Kinetic.Layer();
    var layerChartD20 = new Kinetic.Layer();


    perf = data;

    chartRun({x:20,y:75}, layerChart, 'mlD1', 'yield', 'grey' , true);
    chartRun({x:390,y:75}, layerChart, 'mlD1', 'realyield', '#96B399', false);
    chartRun({x:760,y:75}, layerChart, 'mlD1', 'error', 'orange', false);

    chartRun({x:20,y:75}, layerChartD5, 'mlD5', 'yield', 'grey' , true);
    chartRun({x:390,y:75}, layerChartD5, 'mlD5', 'realyield', '#96B399', false);
    chartRun({x:760,y:75}, layerChartD5, 'mlD5', 'error', 'orange', false);

    chartRun({x:20,y:75}, layerChartD20, 'mlD20', 'yield', 'grey' , true);
    chartRun({x:390,y:75}, layerChartD20, 'mlD20', 'realyield', '#96B399', false);
    chartRun({x:760,y:75}, layerChartD20, 'mlD20', 'error', 'orange', false);

    stagePredictionD1.add(layerChart);
    stagePredictionD5.add(layerChartD5);
    stagePredictionD20.add(layerChartD20);

};



var SIZEX=380;
var SIZEY=180;
var marginX= 340;


var marginInverseFrameY = 80;
var fontSize = 18;



/**
 *
 * @param list
 * @param f
 * @param h
 * @returns {number}
 */
function getMeanRun( list, f, c) {
    var mean = 0; var sizeNotNull = 0;
    for (var i=0;i<list.length;i++) {
        if (list[i][c]) {
            mean += list[i][c][f]
            sizeNotNull++;
        }
    }
    return mean / sizeNotNull;
}

/**
 *
 * @param list
 * @param f
 * @param h
 * @returns {number}
 */

function getmaxRun( list,f,c) {
    var max = 0;
    for (var i=0;i<list.length;i++) {
        if (list[i][c]) {
            var v = Math.abs(list[i][c][f]);
            if (v > max) max = v;
        }
    }
    return max;
}




/**
 * draw run graph
 * @param pos position in page
 * @param layer : add group to layer
 */
function chartRun(pos, layer, col, key, color, dyn) {

    var group = new Kinetic.Group();
    var eltLength = perf.perfList.length;
    var eltSize = 2;
    var max = getmaxRun(perf.perfList, key, col);
    var heightM = (SIZEY/2)/max;

    for (var i=0; i<perf.perfList.length;i++) {
        var dyncolor = color;

        if (perf.perfList[i][col]) {
            if (dyn === true && perf.perfList[i][col].sign === false)
                dyncolor = 'red'
            drawChart(group, pos, perf.perfList[i][col][key], heightM, eltSize, i, perf.perfList[i][col].date, layer, dyncolor );
        }

    }

    drawChartMax(group, pos, max, getMeanRun(perf.perfList, key , col), heightM, marginX);

    layer.add(group);
}




function drawChartMax(group, pos, max ,mean, heightM, width) {
    group.add(new Kinetic.Text({
        x:pos.x-40,
        y: 146 - heightM*max,
        text: max,
        fontFamily: 'Calibri',
        fontSize: 10,
        fill: '#AC7969'
    }));

    group.add(new Kinetic.Rect({
        x: pos.x-12,
        y: 150 - heightM*max,
        width: width,
        height:1,
        fill: '#AC7969',
        opacity: 0.2
    }));


    group.add(new Kinetic.Text({
        x:pos.x-40,
        y: 146 - heightM*mean,
        text: mean.toFixed(),
        fontFamily: 'Calibri',
        fontSize: 10,
        fill: '#AC7969'
    }));

    group.add(new Kinetic.Rect({
        x: pos.x-10,
        y: 150 - heightM*mean,
        width: width,
        height:1,
        fill: '#AC7969',
        opacity: 0.2
    }));

}

/**
 *
 * @param group
 * @param pos
 * @param elt
 * @param heightM
 * @param eltSize
 * @param i
 * @param text
 * @param layer
 * @param color
 */
function drawChart(group, pos, elt ,heightM, eltSize, i, text , layer, color) {
    var tooltip;

    var rectBack = new Kinetic.Rect({
        x: pos.x+i*(eltSize+2),
        y: pos.y*2+10,
        width: eltSize,
        height: -SIZEY,
        fill: '#EDEAE6'
    });

    group.add(rectBack);
    if  (i%30==0) {

        group.add(new Kinetic.Rect({
            x: pos.x+i*(eltSize+2)+4,
            y: pos.y,
            width: 1,
            height:-marginInverseFrameY+20,
            fill: '#AC7969'
        }));

        group.add(new Kinetic.Text({
            x: pos.x+i*(eltSize+2),
            y: pos.y,
            text: text,
            fontSize: 8,
            fontFamily: 'Calibri',
            fill: '#AC7969',
            width: 150,
            align: 'left'
        }));
    }


    var rect = new Kinetic.Rect({
        x: pos.x+i*(eltSize+2),
        y: pos.y,
        width: eltSize,
        height: -elt*heightM,
        fill: color
    });


    var rectLayer = new Kinetic.Rect({
        x: pos.x+i*(eltSize+2)-1,
        y: pos.y,
        width: eltSize+2,
        height: -marginInverseFrameY+20
    });

    group.add(rect);
    group.add(rectLayer);

    var chartGroup = new Kinetic.Group();
    rectLayer.on('mouseover', function() {
        dist = 0;
        anim.start();
        group.add(chartGroup);
        layer.draw();
    });

    rectLayer.on('mouseout', function() {
        anim.stop();
        chartGroup.remove();
        layer.draw();
    });



    var dist =  0;
    var anim = new Kinetic.Animation(function() {
        chartGroup.destroyChildren();

        var spos = {x:pos.x-18,y:pos.y};
        if (i>14) spos.x-=100;



        dist += 35;
        if (dist >= 360) {
            anim.stop();
            dist = 370;
        }


        tooltip = new Kinetic.Label({
            x: spos.x+i*(eltSize+2)+4,
            y: 50,
            height: 85
        });


        var rect = new Kinetic.Rect({
            x:spos.x,
            y: 80,
            width: 170,
            height: -110,
            fill: 'white',
            shadowOffset: {x:1,y:1}
        });

        tooltip.add(rect);

        //drawRunPie(tooltip, spos, elt,dist);

        chartGroup.add(tooltip);

        layer.draw();
    });

}

// 基于准备好的dom，初始化echarts实例
var myChart = echarts.init(document.getElementById('chart1'));
var myChart2 = echarts.init(document.getElementById('chart2'));

var lines = 8;
var cols = 13;

var Ylabels = [];
var Xlabels = [];

function create_labels() {

    for (var i = lines; i > 0; i--) {
        Ylabels.push(i + "");
    }
    for (var i = 1; i <= cols; i++) {
        Xlabels.push(i + "");
    }
}

function create_data() {
    var data = new Array();
    for (var i = 0; i < lines; i++) {
        for (var j = 0; j < cols; j++) {
            var indx = i * cols + j;
            var val = (lines - 1 - i) * cols + j + 1;
            data[indx] = new Array();
            data[indx][0] = i;
            data[indx][1] = j;
            data[indx][2] = 0;
        }
    }
    return data;
}

create_labels();
var data = create_data();
var data2 = create_data();

data = data.map(function (item) {
    return [item[1], item[0], item[2] || '-'];
});
data2 = data2.map(function (item) {
    return [item[1], item[0], item[2] || '-'];
});


var number1_new;
// comet4j的回调函数
var callback = function (str) {

    number1_new.innerHTML = str;
    var str_array = str.split(',');
    var len = str_array.length / 2;
    var line_number;
    var col_number;
    var newindex;
    var index;
    for (var i = 0; i < len; i++) {
        index = i;
        line_number = parseInt(index / cols);
        col_number = index % cols;
        newindex = (lines - 1 - line_number) * cols + col_number;
        data[newindex][2] = str_array[i];
        data2[newindex][2] = str_array[i + len];// rssi;
    }

}

// comet4j的函数，后台向前台推送
function init_new() {

    number1_new = document.getElementById('number1_new');
    // 建立连接，push 即web.xml中 CometServlet的<url-pattern>
    JS.Engine.start('conn');
    // 监听后台某个频道
    JS.Engine.on({
        // 对应服务端 “频道1” 的值 channel1
        channel1: callback,
        // 对应服务端 “频道2” 的值 channel2
    });
}


var option = {
    tooltip: {
        position: 'top'
    },
    animation: false,
    grid: {
        height: '50%',
        y: '10%'
    },
    xAxis: {
        type: 'category',
        data: Xlabels,
        splitArea: {
            show: true
        }
    },
    yAxis: {
        type: 'category',
        data: Ylabels,
        splitArea: {
            show: true
        }
    },
    visualMap: {
        min: 0,
        max: 7,
        calculable: true,
        orient: 'horizontal',
        left: 'center',
        bottom: '15%'
    },
    series: [{
        name: 'Punch Card',
        type: 'heatmap',
        data: data,
        label: {
            normal: {
                show: true
            }
        },
        itemStyle: {
            emphasis: {
                shadowBlur: 10,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
        }
    }]
};


var option2 = {
    tooltip: {
        position: 'top'
    },
    animation: false,
    grid: {
        height: '50%',
        y: '10%'
    },
    xAxis: {
        type: 'category',
        data: Xlabels,
        splitArea: {
            show: true
        }
    },
    yAxis: {
        type: 'category',
        data: Ylabels,
        splitArea: {
            show: true
        }
    },
    visualMap: {
        min: 0,
        max: 7,
        calculable: true,
        orient: 'horizontal',
        left: 'center',
        bottom: '15%'
    },
    series: [{
        name: 'Punch Card',
        type: 'heatmap',
        data: data,
        label: {
            normal: {
                show: true
            }
        },
        itemStyle: {
            emphasis: {
                shadowBlur: 10,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
        }
    }]
};


myChart.setOption(option);
myChart2.setOption(option2);
setInterval(function () {
    myChart.setOption({
        series: [{
            name: 'Punch Card',
            type: 'heatmap',
            data: data,
        }]
    });
    myChart2.setOption({
        series: [{
            name: 'Punch Card',
            type: 'heatmap',
            data: data2,
        }]
    });
}, 200);

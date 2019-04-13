var prefix = "/sys/nodemonitor"
var apiPrefix = "/api/sys/nodemonitor"
$(function () {
    load();
});

function load() {
    $('#nodeTable').bootstrapTable({
        method: 'GET',
        url: apiPrefix,
        iconSize: 'outline',
        striped: true,
        dataType: "json",
        pagination: true,
        singleSelect: false,
        pageSize: 10,
        pageList: [5, 10, 20],
        pageNumber: 1,
        showColumns: false,
        sidePagination: "server",
        queryParams: function (params) {
            return {
                limit: params.limit,
                offset: params.offset
            };
        },
        columns: [{
            field: 'instanceId',
            title: '实例ID'
        }, {
            field: 'app',
            title: '应用名称'
        }, {
            field: 'hostName',
            title: '实例Host'
        }, {
            field: 'status',
            title: '实例状态',
            align: 'center',
            formatter: function (value, row, index) {
                if (value == 'UP') {
                    return '<span class="label label-primary">正常</span>';
                } else {
                    return '<span class="label label-danger">禁用</span>';
                }
            }
        }, {
            field: 'ipAddr',
            title: '实例地址'
        }, {
            field: 'managementPort',
            title: '管理端口'
        }, {
            title: '操作',
            field: 'instanceId',
            align: 'center',
            formatter: function (value, row, index) {
                var e = '<a class=" ' + s_heap_h + '" href="javascript:void(0)" mce_href="#" title="heapdump导出" onclick="heapdump(\'' + row.ipAddr + '\',\'' + row.managementPort + '\')"><i class="glyphicon glyphicon-cloud-download"></i></a> ';
                var d = '<a class=" ' + s_thread_h + '" href="javascript:void(0)" mce_href="#" title="threaddump导出" onclick="threaddump(\'' + row.ipAddr + '\',\'' + row.managementPort + '\')"><i class="glyphicon glyphicon-save-file"></i></a> ';
                return e + d;
            }
        }]
    });
}

function heapdump(ip, port) {
    var url = "http://" + ip + ":" + port + "/actuator/heapdump";
    url = prefix + '/redirectgateway?url=' + encodeURI(url);
    document.location.href = url;
}

function threaddump(ip, port) {
    var url = "http://" + ip + ":" + port + "/actuator/threaddump";
    url = prefix + '/redirectgateway?url=' + encodeURI(url);
    document.location.href = url;
}


var apiPrefix = "/api/gateway/waf";
var pagePrefix = "/gateway/waf";
$(function () {
    load();
});

function load() {
    $('#wafListTable').bootstrapTable({
        method: 'get',
        url: apiPrefix,
        striped: true,
        dataType: "json",
        pagination: true,
        pageSize: 10,
        pageList: [5, 10, 20],
        pageNumber: 1,
        sidePagination: "server",
        showRefresh: true,
        iconSize: "outline",
        icons: {refresh: "glyphicon-repeat"},
        queryParams: function (params) {
            return {
                limit: params.limit,
                offset: params.offset
            };
        },
        columns: [{
            field: 'wafName',
            title: 'WAF名称'
        }, {
            field: 'wafDesc',
            title: 'WAF描述'
        }, {
            field: 'wafType',
            title: 'WAF类型'
        }, {
            field: 'wafEnabled',
            title: 'WAF启用状态',
            formatter: function (item, index) {
                if (item == 'N') {
                    return '<span class="label label-danger">禁用</span>';
                } else if (item == 'Y') {
                    return '<span class="label label-primary">启用</span>';
                }
            }
        }, {
            field: 'gmtCreate',
            title: '创建时间'
        }, {
            field: 'gmtModified',
            title: '更新时间'
        }, {
            title: '操作',
            field: 'id',
            width: '100px',
            align: 'center',
            formatter: function (value, row, index) {
                var e = '<a class="' + s_edit_h + '" href="javascript:void(0)" mce_href="#" title="编辑" onclick="edit(\'' + row.id + '\')">' +
                    '<i class="glyphicon glyphicon-edit"></i></a> ';
                return e ;
            }
        }]
    });

}

function reLoad() {
    $('#wafListTable').bootstrapTable('refresh');
}




function edit(id) {
    var url = pagePrefix + '/edit/' + id;
    var title = '修改WAF配置';
    page(url, title);
}

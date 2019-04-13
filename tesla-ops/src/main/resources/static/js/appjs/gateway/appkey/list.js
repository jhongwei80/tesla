var apiPrefix = "/api/gateway/appkey";
var pagePrefix = "/gateway/appkey";
$(function () {
    load();
});

function load() {
    $('#appkeyListTable').bootstrapTable({
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
        toolbar: "#appkeytoolbar",
        queryParams: function (params) {
            return {
                limit: params.limit,
                offset: params.offset
            };
        },
        columns: [{
            field: 'appName',
            title: '系统名称'
        }, {
            field: 'appKeyDesc',
            title: '描述'
        }, {
            field: 'appKey',
            title: '系统Key'
        }, {
            field: 'appKeyEnabled',
            title: '启用状态',
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
                var g = '<a class="' + s_edit_h + '" href="javascript:void(0)" mce_href="#" title="复制" onclick="copy(\'' + row.id + '\')">' +
                    '<i class="glyphicon glyphicon-copy"></i></a> ';
                var d = '<a class="' + s_remove_h + '" href="javascript:void(0)" title="删除"  mce_href="#" onclick="remove(\'' + row.id + '\')">' +
                    '<i class="glyphicon glyphicon-remove"></i></a> ';
                return e + g + d;
            }
        }]
    });

}

function reLoad() {
    $('#appkeyListTable').bootstrapTable('refresh');
}


function add() {
    var url = pagePrefix + '/add';
    var title = '新建接入系统配置';
    page(url, title);

}

function edit(id) {
    var url = pagePrefix + '/edit/' + id;
    var title = '修改接入系统配置';
    page(url, title);
}

function copy(id) {
    var url = pagePrefix + '/copy/' + id;
    $.ajax({
        type: "GET",
        url: url,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(data) {
            swal(
                {
                    title: "拷贝Appkey成功！",
                    text: "拷贝Appkey成功！",
                    type: "success"
                }, function (e) {
                    reLoad();
                });
        },
        error: function(err) {
            swal("拷贝Appkey失败！", data.msg, "error");
        }
    });
}

function remove(id) {
    swal({
        title: "确定要删除选中的记录？",
        text: "确定要删除选中的记录，删除后不可逆？",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "删除",
        closeOnConfirm: false,
        showLoaderOnConfirm: true
    }, function () {
        $.ajax({
            url: apiPrefix + "/" + id,
            type: "DELETE",
            success: function (data) {
                if (ajaxIsSuccess(data)) {
                    swal(
                        {
                            title: "删除Appkey成功！",
                            text: "删除Appkey成功！",
                            type: "success"
                        }, function (e) {
                            reLoad();
                        });
                } else {
                    swal("删除Appkey失败！", data.msg, "error");
                }
            }

        });
    });
}


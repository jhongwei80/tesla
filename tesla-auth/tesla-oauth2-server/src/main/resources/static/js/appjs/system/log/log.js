var apiPrefix = "/sys/logapi";
var pagePrefix = "/sys/log";
$(function () {
    load();
});

function reLoad() {
    $('#onlineLog').bootstrapTable('refresh');
}

function load() {
    $('#onlineLog').bootstrapTable({
        method: 'get',
        url: apiPrefix,
        striped: true,
        dataType: "json",
        pagination: true,
        singleSelect: false,
        pageSize: 5,
        pageList: [5, 10, 20],
        pageNumber: 1,
        showRefresh: true,
        iconSize: "outline",
        icons: {refresh: "glyphicon-repeat"},
        toolbar: "#logtoolbar",
        sidePagination: "server",
        queryParams: function (params) {
            return {
                limit: params.limit,
                offset: params.offset,
                username: $('#searchUsername').val().trim(),
                operation: $('#searchOperation').val().trim(),
                sort: 'gmt_create',
                order: 'desc'
            };
        },
        columns: [{
            checkbox: true
        }, {
            field: 'id',
            title: '序号'
        }, {
            field: 'userId',
            title: '用户Id'
        }, {
            field: 'username',
            title: '用户名'
        }, {
            field: 'operation',
            title: '操作'
        }, {
            field: 'time',
            title: '用时'
        }, {
            field: 'method',
            title: '方法'
        }, {
            field: 'ip',
            title: 'IP地址'
        }, {
            field: 'gmtCreate',
            title: '创建时间'
        }, {
            title: '操作',
            field: 'id',
            align: 'center',
            formatter: function (value, row, index) {
                var d = '<a  href="javascript:void(0)" title="删除"  mce_href="#" onclick="remove(\'' + row.id + '\')"><i class="glyphicon glyphicon-remove"></i></a> ';
                return d;
            }
        }]
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
                            title: "删除日志记录成功！",
                            text: "删除日志记录成功！",
                            type: "success"
                        }, function (e) {
                            reLoad();
                        });
                } else {
                    swal("删除日志记录失败！", "删除日志记录失败！", "error");
                }
            }

        });
    });
}

function batchRemove() {
    var rows = $('#onlineLog').bootstrapTable('getSelections');


    if (rows.length == 0) {
        swal("请选择要删除的记录？", "请选择要删除的记录？", "warning");
        return;
    }

    swal({
        title: "删除确认？",
        text: "确定要删除选中的记录？",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "删除",
        closeOnConfirm: false,
        showLoaderOnConfirm: true
    }, function () {
        sureremove();
    });

    function sureremove() {
        var ids = new Array();
        $.each(rows, function (i, row) {
            ids[i] = row['id'];
        });
        $.ajax({
            type: 'POST',
            data: {
                _method: "DELETE",
                "ids": ids
            },
            url: apiPrefix,
            success: function (data) {
                if (ajaxIsSuccess(data)) {
                    swal(
                        {
                            title: "删除日志记录成功！",
                            text: "删除日志记录成功！",
                            type: "success"
                        }, function (e) {
                            reLoad();
                        });
                } else {
                    swal("删除日志记录失败！", "删除日志记录失败！", "error");
                }
            }
        });
    }
}
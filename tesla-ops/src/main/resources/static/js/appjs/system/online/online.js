var prefix = "/sys/online"
$(function () {
    load();
});

function reLoad() {
    $('#onlineTable').bootstrapTable('refresh');
}

function load() {
    $('#onlineTable').bootstrapTable({
        method: 'get',
        url: prefix + "/list",
        striped: true,
        dataType: "json",
        pagination: true,
        singleSelect: false,
        pageSize: 5,
        pageList: [5, 10, 20],
        pageNumber: 1,
        showColumns: false,
        showRefresh: true,
        iconSize: "outline",
        icons: {refresh: "glyphicon-repeat"},
        sidePagination: "client",
        queryParams: function (params) {
            return {
                limit: params.limit,
                offset: params.offset,
                name: $('#searchName').val()
            };
        },
        columns: [{
            field: 'id',
            title: '序号'
        }, {
            field: 'username',
            title: '用户名'
        }, {
            field: 'host',
            title: '主机'
        }, {
            field: 'startTimestamp',
            title: '登录时间'
        }, {
            field: 'lastAccessTime',
            title: '最后访问时间'
        }, {
            field: 'timeout',
            title: '过期时间'
        }, {
            field: 'status',
            title: '状态',
            align: 'center',
            formatter: function (value, row, index) {
                if (value == 'on_line') {
                    return '<span class="label label-success">在线</span>';
                } else if (value == 'off_line') {
                    return '<span class="label label-primary">离线</span>';
                }
            }
        }, {
            title: '操作',
            field: 'id',
            align: 'center',
            formatter: function (value, row, index) {
                var d = '<a class="" href="javascript:void(0)" title="强制下线"  mce_href="#" onclick="forceLogout(\'' + row.id + '\')"><i class="glyphicon glyphicon-remove"></i></a> ';
                return d;
            }
        }]
    });
}

function reLoad() {
    $('#onlineTable').bootstrapTable('refresh');
}

function forceLogout(id) {
    swal({
        title: "确定要强制下线该用户？",
        text: "确定要强制下线该用户？",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "下线",
        closeOnConfirm: false,
        showLoaderOnConfirm: true
    }, function () {
        setTimeout(sureremove, 1000);
    });

    function sureremove() {
        $.ajax({
            url: prefix + "/forceLogout/" + id,
            type: "post",
            data: {
                'id': id
            },
            success: function (data) {

                if (ajaxIsSuccess(data)) {
                    swal(
                        {
                            title: "强制下线用户成功！",
                            text: "强制下线用户成功！",
                            type: "success"
                        }, function (e) {
                            reLoad();
                        });
                } else {
                    swal("强制下线用户失败！", "强制下线用户失败！", "error");
                }

            }
        });
    }
}
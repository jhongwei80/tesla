var prefix = "/sys/role";
var apiPrefix = "/api/sys/role";
$(function () {
    load();
});

function load() {
    $('#roleTable').bootstrapTable({
        method: 'get',
        url: apiPrefix,
        striped: true,
        pagination: true,
        singleSelect: false,
        pageSize: 10,
        pageList: [5, 10, 20],
        pageNumber: 1,
        showRefresh: true,
        iconSize: "outline",
        icons: {refresh: "glyphicon-repeat"},
        toolbar: "#roleToolbar",
        sidePagination: "client",
        columns: [{
            checkbox: true
        }, {
            field: 'roleId',
            title: '序号'
        }, {
            field: 'roleName',
            title: '角色名'
        }, {
            field: 'roleSign',
            title: '昵称'
        }, {
            field: 'remark',
            title: '备注'
        }, {
            field: '',
            title: '权限'
        }, {
            title: '操作',
            field: 'roleId',
            align: 'center',
            width: '70px',
            formatter: function (value, row, index) {
                var e = '<a class=" ' + s_edit_h + '" href="javascript:void(0)" mce_href="#" title="编辑" onclick="edit(\'' + row.roleId + '\')"><i class="glyphicon glyphicon-edit"></i></a> ';
                var d = '<a class="' + s_remove_h + '" href="javascript:void(0)" title="删除"  mce_href="#" onclick="remove(\'' + row.roleId + '\')"><i class="glyphicon glyphicon-remove"></i></a> ';
                return e + d;
            }
        }]
    });
}

function reLoad() {
    $('#roleTable').bootstrapTable('refresh');
}

function add() {
    var url = prefix + '/add';
    var title = '新增角色';
    page(url, title);
}

function edit(id) {
    var url = prefix + '/edit/' + id;
    var title = '编辑角色';
    page(url, title);

}

function remove(id) {


    swal({
        title: "确定要删除选中的角色？",
        text: "确定要删除选中的角色，删除后不可逆？",
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
                            title: "删除角色成功！",
                            text: "删除角色成功！",
                            type: "success"
                        }, function (e) {
                            reLoad();
                        });
                } else {
                    swal("删除角色失败！", "删除角色失败！", "error");
                }
            }

        });
    });
}

function batchRemove() {
    var rows = $('#roleTable').bootstrapTable('getSelections');

    if (rows.length == 0) {
        swal("请选择要删除的角色？", "请选择要删除的角色？", "warning");
        return;
    }

    swal({
        title: "删除确认？",
        text: "确定要删除选中的角色？",
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
            ids[i] = row['roleId'];
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
                            title: "删除角色成功！",
                            text: "删除角色成功！",
                            type: "success"
                        }, function (e) {
                            reLoad();
                        });
                } else {
                    swal("删除角色失败！", "删除角色失败！", "error");
                }
            }
        });
    }

}
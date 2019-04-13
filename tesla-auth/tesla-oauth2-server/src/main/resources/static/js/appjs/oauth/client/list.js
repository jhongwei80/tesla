var PREFIX = "/oauth2/client";
var RESTFUL_URL = "/api" + PREFIX;
$(function () {
    //initSelect();
    load();
});

var initSelect = function () {
    $("#deptId").selectpicker();
    $('#deptId').on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {
        var deptId = $(this).children('option:selected').attr('value');
        var opt = {
            query: {
                deptId: deptId == 0 ? '' : deptId,
                name: $('#searchName').val()
            }
        };
        $('#userTable').bootstrapTable('refresh', opt);
    });
};

function load() {
    $('#clientTable').bootstrapTable({
        method: 'GET',
        url: RESTFUL_URL,
        iconSize: 'outline',
        striped: true,
        dataType: "json",
        pagination: true,
        singleSelect: false,
        pageSize: 10,
        pageList: [5, 10, 20],
        pageNumber: 1,
        showColumns: false,
        toolbar: "userToolBar",
        sidePagination: "server",
        showRefresh: true,
        icons: {refresh: "glyphicon-repeat"},
        queryParams: function (params) {
            return {
                limit: params.limit,
                offset: params.offset
            };
        },
        columns: [{
            field: 'clientId',
            title: '客户端Id'
        }, {
            field: 'clientName',
            title: '客户端名称'
        }, {
            field: 'resourceIds',
            title: '资源Ids'
        }, {
            field: 'description',
            title: '说明简介'
        }, {
            field: 'createTime',
            title: '创建时间'
        }, {
            title: '操作',
            field: 'id',
            align: 'center',
            formatter: function (value, row, index) {
                var e = '<a  class="' + s_edit_h + '" href="javascript:void(0)" mce_href="#" title="编辑" onclick="edit(\'' + row.clientId + '\')"><i class="glyphicon glyphicon-edit "></i></a> ';
                var d = '<a class="' + s_remove_h + '" href="javascript:void(0)" title="删除"  mce_href="#" onclick="remove(\'' + row.clientId + '\')"><i class="glyphicon glyphicon-remove"></i></a> ';
                return e + d;
            }
        }]
    });
}

function refresh() {

    $('#clientTable').bootstrapTable('refresh');
}

function add() {
    var url = PREFIX + '/add';
    var title = '添加用户';
    page(url, title);
}

function edit(clientId) {
    var url = PREFIX + '/edit/' + clientId;
    var title = '编辑用户';
    page(url, title);
}

function remove(clientId) {
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
            url: RESTFUL_URL + '/' + clientId,
            type: "delete",
            success: function (data) {
                if (ajaxIsSuccess(data)) {
                    swal({
                        title: "删除用户成功！",
                        text: "删除用户成功！",
                        type: "success"
                    }, function (e) {
                        refresh();
                    });
                } else {
                    swal("删除用户失败！", "删除用户失败！", "error");
                }
            }
        });
    });
}

function resetPwd(id) {
    var url = PREFIX + '/resetPwd/' + id;
    var title = '修改密码';
    page(url, title);
}
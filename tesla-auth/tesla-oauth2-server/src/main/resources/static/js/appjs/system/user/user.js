var PREFIX = "/sys/user";
var RESTFUL_URL = "/api" + PREFIX;
$(function() {
    initSelect();
    var deptId = '';
    load(deptId);
});

var initSelect = function () {
    $("#deptId").selectpicker();
    $('#deptId').on('changed.bs.select', function (e, clickedIndex, isSelected, previousValue) {
        var deptId = $(this).children('option:selected').attr('value');
        var opt = {query: {
            deptId: deptId == 0 ? '' : deptId,
            name: $('#searchName').val()
        }};
        $('#userTable').bootstrapTable('refresh', opt);
    });
};

function load(deptId) {
    $('#userTable').bootstrapTable({
        method: 'GET',
        url: RESTFUL_URL,
        iconSize: 'outline',
        striped: true,
        dataType: "json",
        pagination: true,
        singleSelect: false,
        pageSize: 5,
        pageList: [5],
        pageNumber: 1,
        showColumns: false,
        toolbar: "userToolBar",
        sidePagination: "server",
        queryParams: function(params) {
            return {
                limit: params.limit,
                offset: params.offset,
                name: $('#searchName').val(),
                deptId: deptId
            };
        },
        columns: [{
            field: 'userId',
            title: '序号'
        }, {
            field: 'deptName',
            title: '部门'
        }, {
            field: 'name',
            title: '姓名'
        }, {
            field: 'username',
            title: '用户名'
        }, {
            field: 'email',
            title: '邮箱'
        }, {
            field: 'status',
            title: '状态',
            align: 'center',
            formatter: function(value, row, index) {
                if (value == '0') {
                    return '<span class="label label-danger">禁用</span>';
                } else if (value == '1') {
                    return '<span class="label label-primary">正常</span>';
                }
            }
        }, {
            title: '操作',
            field: 'id',
            align: 'center',
            formatter: function(value, row, index) {
                var e = '<a  class="' + s_edit_h + '" href="javascript:void(0)" mce_href="#" title="编辑" onclick="edit(\'' + row.userId + '\')"><i class="glyphicon glyphicon-edit "></i></a> ';
                var d = '<a class="' + s_remove_h + '" href="javascript:void(0)" title="删除"  mce_href="#" onclick="remove(\'' + row.userId + '\')"><i class="glyphicon glyphicon-remove"></i></a> ';
                var f = '<a class="' + s_resetPwd_h + '" href="javascript:void(0)" title="重置密码"  mce_href="#" onclick="resetPwd(\'' + row.userId + '\')"><i class="glyphicon glyphicon-lock"></i></a> ';
                return e + d + f;
            }
        }]
    });
}

function refresh() {
    var deptId = $('#deptId').children('option:selected').attr('value');
    var opt = {query: {
            deptId: deptId == 0 ? '' : deptId,
            name: $('#searchName').val()
        }};
  $('#userTable').bootstrapTable('refresh',opt);
}

function add() {
    var url = PREFIX + '/add';
    var title = '添加用户';
    page(url,title);
}

function edit(id) {
    var url = PREFIX + '/edit/' + id;
    var title = '编辑用户';
    page(url,title);
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
            url: RESTFUL_URL + '/' +id,
            type: "delete",
            success: function (data) {
                if(ajaxIsSuccess(data)){
                    swal({
                        title: "删除用户成功！",
                        text: "删除用户成功！",
                        type: "success"
                    }, function (e) {
                        refresh();
                    });
                }else{
                    swal("删除用户失败！", "删除用户失败！", "error");
                }
            }
        });
    });
}

function resetPwd(id) {
    var url = PREFIX + '/resetPwd/' + id;
    var title = '修改密码';
    page(url,title);
}
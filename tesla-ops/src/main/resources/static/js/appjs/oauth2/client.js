var prefix = "sys/oauth2";
$(document).ready(function() {
  $('#oauth2Table').bootstrapTable({
    method: 'GET',
    url: prefix + "/listClient",
    iconSize: 'outline',
    striped: true,
    dataType: "json",
    pagination: true,
    singleSelect: false,
    pageSize: 5,
    pageNumber: 1,
    showColumns: false,
    sidePagination: "server",
    queryParams: function(params) {
      return {
        limit: params.limit,
        offset: params.offset
      };
    },
    columns: [{
      checkbox: true
    }, {
      field: 'clientId',
      title: 'ClientId'
    }, {
      field: 'name',
      title: 'Name'
    }, {
      field: 'clientSecret',
      title: 'Secret'
    }, {
      field: 'redirectUri',
      title: 'Redirect URL',
      cellStyle: function(value, row, index) {
        return {
          css: {
            "overflow": "hidden",
            "white-space": "nowrap",
            "text-overflow": "ellipsis"
          }
        }
      },
      formatter: function(value, row, index) {
        return `<a href="javascript:void(0);" rel="popover" data-placement="top" data-original-title="RedirectUrL Detail" data-content="${value}"><strong>${value}</strong></a>`;
      }
    }, {
      field: 'scope',
      title: 'Scope'
    }, {
      field: 'grantTypes',
      title: 'GrantType',
      cellStyle: function(value, row, index) {
        return {
          css: {
            "overflow": "hidden",
            "white-space": "nowrap",
            "text-overflow": "ellipsis"
          }
        }
      },
      formatter: function(value, row, index) {
        return `<a href="javascript:void(0);" rel="popover" data-placement="top" data-original-title="GrantType Detail" data-content="${value}"><strong>${value}</strong></a>`;
      }
    }, {
      field: 'accessTokenValidity',
      title: 'AccessEffective'
    }, {
      field: 'refreshTokenValidity',
      title: 'RefreshEffective'
    }, {
      field: 'trusted',
      title: 'Trust',
      formatter: function(value, row, index) {
        if (value) {
          return "是";
        } else {
          return "否"
        }
      }
    }, {
      title: '操作',
      field: 'clientId',
      align: 'center',
      formatter: function(value, row, index) {
        var e = '<a class="btn btn-primary btn-sm ' + s_edit_h + '" href="javascript:void(0)" mce_href="#" title="编辑" onclick="edit(\'' + row.clientId + '\')"><i class="fa fa-edit"></i></a> ';
        var d = '<a class="btn btn-warning btn-sm" href="javascript:void(0)" title="删除"  mce_href="#" onclick="remove(\'' + row.clientId + '\')"><i class="fa fa-remove"></i></a> ';
        return e + d;
      }
    }]
  });
  $('#oauth2Table').on('post-body.bs.table', function() {
    pageSetUp();
  });
});

function add() {
  var url = prefix + '/add';
  loadURL(url, $('#content'));
}
function edit(id) {
  var url = prefix + '/edit/' + id;
  loadURL(url, $('#content'));
}

function remove(id) {
  $.SmartMessageBox({
    title: "<i class='fa fa-sign-out txt-color-orangeDark'></i> 确定要删除选中的记录？",
    buttons: '[No][Yes]'
  }, function(ButtonPressed) {
    if (ButtonPressed == "Yes") {
      setTimeout(sureremove, 1000);
    }
  });
  function sureremove() {
    $.ajax({
      url: prefix + "/remove",
      type: "post",
      data: {
        'id': id
      },
      success: function(data) {
        loadURL("sys/oauth2/client", $('#content'));
      }
    });
  }
}
function batchRemove() {
  var rows = $('#oauth2Table').bootstrapTable('getSelections');
  if (rows.length == 0) {
    $.SmartMessageBox({
      title: "<i class='fa fa-sign-out txt-color-orangeDark'></i> 请选择要删除的记录？",
      buttons: '[Yes]'
    });
    return;
  }
  $.SmartMessageBox({
    title: "<i class='fa fa-sign-out txt-color-orangeDark'></i> 确定要删除选中的记录？",
    buttons: '[No][Yes]'
  }, function(ButtonPressed) {
    if (ButtonPressed == "Yes") {
      setTimeout(sureremove, 1000);
    }
  });
  function sureremove() {
    var ids = new Array();
    $.each(rows, function(i, row) {
      ids[i] = row['clientId'];
    });
    $.ajax({
      type: 'POST',
      data: {
        "ids": ids
      },
      url: prefix + '/batchRemove',
      success: function(r) {
        loadURL("sys/oauth2/client", $('#content'));
      }
    });
  }
}
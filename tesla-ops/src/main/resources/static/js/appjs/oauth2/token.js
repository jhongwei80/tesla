var prefix = "/sys/oauth2";
$(document).ready(function() {
  $('#tokenTable').bootstrapTable({
    method: 'get',
    url: prefix + "/listToken",
    iconSize: 'outline',
    striped: true,
    dataType: "json",
    pagination: true,
    singleSelect: false,
    pageSize: 10,
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
      field: 'tokenId',
      title: '令牌Id'
    }, {
      field: 'username',
      title: '用户名'
    }, {
      field: 'authenticationId',
      title: '授权Id'
    }, {
      field: 'clientId',
      title: '客户端Id'
    }, {
      field: 'tokenType',
      title: '令牌类型'
    }, {
      field: 'tokenExpiredSeconds',
      title: '失效时间'
    }, {
      field: 'refreshTokenExpiredSeconds',
      title: '刷新失效'
    }]
  });
});
function batchRemove() {
  var rows = $('#tokenTable').bootstrapTable('getSelections');
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
      ids[i] = row['tokenId'];
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
$(document).ready(function() {
  var prefix = "sys/oauth2"
  pageSetUp();
  var pagefunction = function() {
    var $oauth2Form = $("#oauth2Form").validate({
      rules: {
        clientId: {
          required: true
        },
        name: {
          required: true
        },
        clientSecret: {
          required: true
        },
        scope: {
          required: true
        },
        grantTypes: {
          required: true
        },
        accessTokenValidity: {
          required: true
        },
        refreshTokenValidity: {
          required: true
        },
        trusted: {
          required: true
        },
        redirectUri: {
          required: true
        }
      },
      messages: {
        clientId: {
          required: "请输入客户端ID！"
        },
        name: {
          required: "请输入客户端命名！"
        },
        clientSecret: {
          required: "请输入客户端安全码！"
        },
        scope: {
          required: "请选择授权范围！"
        },
        grantTypes: {
          required: "请选择授权类型！"
        },
        accessTokenValidity: {
          required: "请输入AccessToken有效时长！"
        },
        refreshTokenValidity: {
          required: "请输入RefreshToken有效时长！"
        },
        trusted: {
          required: "请选择是否信任！"
        },
        redirectUri: {
          required: "请输入重定向URL！"
        }
      },
      submitHandler: function(form) {
        $(form).ajaxSubmit({
          cache: true,
          type: "post",
          url: prefix + "/update",
          data: $('#oauth2Form').serialize(),
          async: false,
          success: function() {
            $("#oauth2Form").addClass('submited');
            loadURL("sys/oauth2/client", $('#content'));
          }
        });
      },
      errorPlacement: function(error, element) {
        error.insertAfter(element.parent());
      }
    });
  };
  loadScript("js/plugin/jquery-form/jquery-form.min.js", pagefunction);
});
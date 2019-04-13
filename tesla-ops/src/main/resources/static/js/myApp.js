function page(url, title) {
    var parent = $(window.parent.document).find('.J_menuTabs .page-tabs-content ').find(".J_menuTab.active").attr('menu');
    $(window.parent.document).find('.J_menuTabs .page-tabs-content ').find(".J_menuTab.active").removeClass("active");
    $(window.parent.document).find('.J_mainContent').find("iframe").css("display", "none");
    var iframe = '<iframe class="J_iframe" name="iframe10000" width="100%" height="100%" src="'+url+'" frameborder="0" data-id="'+url+'" seamless="" style="display:inline;"></iframe>';
    $(window.parent.document).find('.J_menuTabs .page-tabs-content ').append(
        '<a href="javascript:;" class="J_menuTab active" parent="'+parent+'" data-id="'+url+'">'+title+' <i class="fa fa-times-circle"></i></a>');
    $(window.parent.document).find('.J_mainContent').append(iframe);
}

/*关闭iframe*/
function removeIframe(refresh) {
    refresh = refresh === undefined || refresh === '' ? false : refresh;
    var topWindow = $(window.parent.document);
    var tabs = topWindow.find(".J_menuTabs .page-tabs-content >a");
    $(tabs).each(function () {
        if($(this).hasClass('active')){
            var parent = $(this).attr('parent');
            if(typeof parent !== typeof undefined){
                var parentTab = topWindow.find(".J_menuTabs .page-tabs-content").children('a[menu="'+parent+'"]');
                parentTab.addClass("active");
                $(this).remove();
                var parentDataId = parentTab.attr('data-id');
                var parentIframe = topWindow.find('.J_mainContent').children('iframe[data-id="'+parentDataId+'"]');
                parentIframe.show();
                if(refresh){
                    parentIframe.attr('src',parentIframe.attr('src'));
                }
            }
            var dataId = $(this).attr('data-id');
            var showIframe = topWindow.find('.J_mainContent').children('iframe[data-id="'+dataId+'"]');
            showIframe.remove();
        }
    });
}

function removeIframeWithSwal(){
    swal({
        title: "关闭确定？",
        text: "请检查本页是否未保存，是否确定关闭？",
        type: "warning",
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "关闭",
        closeOnConfirm: false
    }, function () {
        removeIframe();
    });
}

function ajaxIsSuccess(data) {
    return 1 === data.code;
}

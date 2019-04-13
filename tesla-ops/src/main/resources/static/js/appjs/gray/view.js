var initNetWork = function (result) {
    if(result === null || result === undefined || result === ''){
        return;
    }
    var panelWidth = $(".ibox-content").width();
    var panelHeight = $(".ibox-content").height() +300;
    var container = document.getElementById('grayNetwork');
    var edges = result.edges;
    $.each(edges,function(index, value){
        value.font={'align': 'horizontal'};
    });

    var nodes = result.nodes;
    $.each(nodes,function(index, value){
        if(value.grayNode === false || value.grayNode === 'false'){
            value.label = '<i>'+value.label+'</i>';
        }else{
            value.label = '<b>'+value.label+'</b>';
        }
    });
    console.info(nodes);

    var data = {
        nodes: new vis.DataSet(nodes),
        edges: new vis.DataSet(edges)
    };

    var options = {
        autoResize: true,
        height: panelHeight+'px',
        width: panelWidth+'px',
        nodes:{
            font:{
                bold: {
                    color: '#000000',//029ff4
                    size: 16, // px
                    face: 'open sans","Helvetica Neue",Helvetica,Arial,sans-serif',
                    vadjust: 0,
                    mod: 'bold'
                },
                ital: {
                    color: '#929292',//#679ebf
                    size: 14, // px
                    face: 'open sans","Helvetica Neue",Helvetica,Arial,sans-serif',
                    vadjust: 0,
                    mod: 'normal',
                },
                multi:'html'
            },
            size: 16
        },
        edges: {
            arrows: 'to',
            color: 'red',
            font: '14px arial #ff0000',
            scaling:{label:true},
            shadow: true,
            smooth: {
                type: 'cubicBezier',
                forceDirection: 'vertical',
                roundness: 0.4
            }
        },
        layout: {
            hierarchical: {
                direction: 'UD',
                enabled:true,
                parentCentralization: true,
                sortMethod: 'directed'
            }
        },
        physics:{
            hierarchicalRepulsion: {
                centralGravity: 0.5,//拉近
                springLength: 200,//节点宽度
                springConstant: 0.19,//竖立
                nodeDistance: 210,
                damping: 0.10
            },
            solver: 'hierarchicalRepulsion',
            maxVelocity: 0,
            timestep: 0.35,
            stabilization: {iterations: 150}
        },
        interaction:{
            keyboard: false,
            hover:true
        }
    };

    var network = new vis.Network(container, data, options);
    network.on('hoverNode', function (event) {
        var node = data.nodes.get(event.node);
        if(node.conditions === null || node.conditions === undefined || node.conditions.length===0){
            $("#grayRuleTip").css({"visibility":"hidden"});
            return;
        }
        showTip(node.conditions,event.event);
    });
    network.on('hoverEdge', function (event) {
        var edges = data.edges.get(event.edge);
        if(edges.conditions === null || edges.conditions === undefined || edges.conditions.length===0){
            $("#grayRuleTip").css({"visibility":"hidden"});
            showGroovy('',event.event);
            return;
        }
        showTip(edges.conditions, event.event,options);
        showGroovy(edges.groovy,event.event);
    });
    network.on('blurNode', function (event) {
        $("#grayRuleTip").css({"visibility":"hidden"});
    });
    network.on('blurEdge', function (event) {
        $("#grayRuleTip").css({"visibility":"hidden"});
    });
};

var showTip = function(conditions, event){
    var html = '<ul>';
    $.each(conditions,function(index, item){
        html += '<li>'+item.paramKind+'::'+item.paramKey+'='+item.paramValue+'</li>';
    });
    html += '</ul>';
    $("#grayRuleTip").html(html);
    $("#grayRuleTip").css({
        "top": event.pageY+"px",
        "left": event.pageX+"px",
        "visibility":"visible"
    }).show("fast");
};

var showGroovy = function (groovy,event) {
    //console.info(groovy);
    /*if(groovy === '' || groovy === undefined){
        return;
    }*/
    $("#grayRule").html(groovy);
};
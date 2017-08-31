function makePageHref() {
    var search = window.location.search;
    var paramArray = parseUrlSearch(search);
    replaceParam(paramArray, 'page', '{{number}}');
    return '?' + serializeParamArray(paramArray) + '#' + window.location.hash;
}

/*
 * 解析参数字符串 返回参数数组
 */
function parseUrlSearch(search) {
    var paramArray = [];
    if (typeof (search) !== 'undefined' && search.indexOf("?") != -1) { // 获取url中"?"符后的字串
        var searchParams = search.substr(1).split("&");
        for (var i = 0; i < searchParams.length; i++) {
            var name = searchParams[i].split("=")[0];
            var value = searchParams[i].split("=")[1];
            paramArray.push({
                "name": name,
                "value": value
            });
        }
    }
    return paramArray;
}

/*
 * 替换参数
 */
function replaceParam(paramArray, name, value) {
    if (typeof (paramArray) === 'undefined') {
        paramArray = []
    }
    var exists = false;
    for (var i = 0; i < paramArray.length; i++) {
        if (paramArray[i]['name'] == name) {
            exists = true;
            paramArray[i]['value'] = value;
        }
    }
    if (!exists) {
        paramArray.push({
            "name": name,
            "value": value
        });
    }
    return paramArray;
}

/*
 * 序列化参数数组
 * 返回序列化后的参数字符串
 */
function serializeParamArray(paramArray) {
    var paramStrArray = [];
    for (var i = 0; i < paramArray.length; i++) {
        paramStrArray.push(paramArray[i]['name'] + "=" + paramArray[i]['value']);
    }
    return paramStrArray.join('&');
}

$('.pagination').twbsPagination({
    totalPages: pageCount, // the number of pages
    // (required)
    startPage: 1, // the current page that show on
    // start(default: 1)
    visiblePages: 10, // maximum visible pages
    // (default: 5)
    initiateStartPageClick: false, // fire onclick event at start
    // page on plugin initialization
    // (default true)
    href: makePageHref(), // template for pagination links
    // (default false)
    hrefVariable: '{{number}}', // variable name in href
    // template for page number
    // (default {{number}})
    first: '首页', // text label (default: 'First')
    prev: '上一页', // text label (default:
    // 'Previous')
    next: '下一页', // text label (default: 'Next')
    last: '末页', // text label (default: 'Last')
    loop: false, // carousel-style pagination
    // (default: false)
    paginationClass: 'pagination', // the root style for pagination
    // component (default:
    // 'pagination'). You can use
    // this option to apply your own
    // style

    /*
     * callback function event object page the number of page
     */
    onPageClick: function (event, page) {
    }
});

$('.pagination').append("<li class='disabled'><span>共" + totalElements + "条 " + pageCount + "页</span></li>")

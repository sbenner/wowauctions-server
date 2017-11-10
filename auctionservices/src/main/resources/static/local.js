window.onload = function () {
    $('#pager').hide();
    $.ajax({
        type: "OPTIONS",
        url: "http://localhost:8080/",
        cache: false,
        async: true,
        success: function (data, opts, xhr) {

            //   $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
            var token = xhr.getResponseHeader('X-CSRF-TOKEN');
            //runReq(token)

            $(document).ajaxSend(function (e, xhr, options) {
                var header = "X-CSRF-TOKEN";
                xhr.setRequestHeader(header, token);
            });


        },
        error: function (data) {

        }
    });


    var avg = function (arr) {
        var sum = arr.reduce(function (a, b) {
            return a + b;
        });
        var result = Math.round(sum / arr.length);

        return formatPrice(result);
    };

    var formatPrice = function(result){
        var price = result.toString();
        var len = price.length;

        if (len > 4) {
            price = price.substr(0, len - 4) + "g "
                + price.substr(len - 2, len) + "s " + price.substr(len - 2, len) + "c";
        }
        if (len > 2 && len <= 4) {
            price = price.substr(0, len - 2) + "s " + price.substr(len - 2, len) + "c";
        }
        if (len <= 2) {
            price = price.substr(0, len) + "c";
        }
        return price;
    }

    function runChart(id) {

        $.ajax({
            type: "GET",
            url: "http://localhost:8080/wow/v1/web/itemchart?id=" + id,
            cache: false,
            async: true,
            success: function (data) {
                var price = [];
                var label = [];
                var b = [];

                for (var p in data) {
                    if (data.hasOwnProperty(p)) {
                        b.push({x:data[p],y:parseInt(p)});
                    }
                }
                b.sort(compare);
                function compare(a, b) {
                    if (a.x < b.x) {
                        return -1;
                    }
                    if (a.x > b.x) {
                        return 1;
                    }
                    return 0;
                }


                for (var i=0;i<b.length;i++) {
                         label.push($.datepicker.formatDate('yy-mm-dd',new Date(b[i].x)));
                         price.push(b[i].y/10000);
                }


                new Chart(document.getElementById("chartjs-0"), {
                    "type": "line",
                    "data": {
                        "labels": label,
                        "datasets": [{
                            "label": "AVG Price "+avg(price),
                            "data": price,
                             "borderColor": "rgb(75, 192, 192)",
                        }]
                    }
                });

            },
            error: function (data) {

            }
        });


    }

    function runGetitems(name, params) {

        if(!params)params="";
        name = name.trim();
        $.ajax({
            type: "GET",
            url: "http://localhost:8080/wow/v1/web/items?name=" + name + params,
            cache: false,
            async: true,
            crossDomain: true,
            success: function (data) {
                if (data.numberOfElements > 0) {
                    $("#table tbody").text("");
                    $("#tbl").show();
                    $("#chartDiv").hide();
                    $.each(data.content, function (d, results) {

                        $("#table tbody").append(
                            "<tr >" +
                            "<td >" +
                            "<a name='" + results.auc + "' id='" + results.item.id +
                            "' href='#' onclick='return false;'>" + results.item.name + "</a>" +
                            "<div  id='tooltip' class='tooltip'></div></td>" +
                            "<td><span style='color: white;'>" + results.item.itemLevel + "</span></td>" +
                            "<td><span style='color: white;'>" + results.owner + "</span></td>" +
                            "<td><span style='color: white;'>" + results.bid + "</span></td>" +
                            "<td><span style='color: white;'>" + results.buyout + "</span></td>" +
                            "<td><span style='color: white;'>" + results.ppi + "</span></td>" +
                            "<td><span style='color: white;'>" + results.quantity + "</span></td>" +
                            "</tr>"
                        );
                        setAhrefColor(results);

                    });
                    var total = data.totalPages;
                    var page = data.number;
                    var p = page+1;
                    if (total > 1) {
                        $('#pager').show();
                        $('#pages').text("Page "+ p +" of "+total);
                        var first = document.getElementById('first');
                        first.onclick = function () {
                            runGetitems(name, "");
                        };
                        var last = document.getElementById('last');
                        last.onclick = function () {
                            runGetitems(name, "&page=" + total);
                        };
                        var prev = document.getElementById('prev');
                        prev.onclick = function () {
                            if (page > 0) {
                                runGetitems(name, "&page=" + (parseInt(p)));

                            }
                        };
                        var next = document.getElementById('next');
                        next.onclick = function () {
                            if (page < total) {
                                runGetitems(name, "&page=" + (parseInt(page + 1)));
                                
                            }
                        };

                    }

                }

            },
            error: function (data) {
            }
        });
    }


    function getItemFromWeb(id, ele) {

        $.ajax({
            type: 'GET',
            url: "http://localhost:8080/wow/v1/web/item/" + id,
            crossDomain: true,
            cache: false,
            async: true,
            dataType: 'text',
            success: function (result) {
                //var tooltip = ele.firstChild;
                $('.wiki-tooltip').remove();
                ele.innerHTML = result;

            },
            error: function (result, a, err) {
                // ele.setAttribute('title', result.responseText);

            }
        });

    }


    function setAhrefColor(results) {
        var quality = results.item.quality;
        var ahref = document.getElementsByName(results.auc)[0];//$("a[name=\'"+results.auc+"\']");
        var tooltip = document.getElementById('tooltip');

        ahref.onclick = function () {
            $("#tbl").hide();

            $("#chartDiv").show();
            runChart(results.item.id);
        };


        ahref.onmouseenter = function () {
            getItemFromWeb(results.item.id, tooltip);
        };

        ahref.onmouseleave = function () {
            $('.wiki-tooltip').remove();
        };

        if (ahref && ahref !== null) {
            ahref.className = 'color-q' + quality;
        }
    }

    $('#btn_search').on('click', function () {
        $('#pager').hide();
        runGetitems($('#search').val());
    });

    $("#search").on("keydown", function search(e) {
        if (e.keyCode == 13) {
            $('#pager').hide();
            runGetitems($(this).val());
        }
    });


}
window.onload = function () {
    $('#pager').hide();
    $('#chartDiv').hide();
    $.ajax({
        type: "OPTIONS",
        url: "http://wow.ooktioneer.com/",
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

            loadCountAndDate();

        },
        error: function (data) {

        }
    });

    var loadCountAndDate= function ()
    {
        $.ajax({
            type: "GET",
            url: "http://wow.ooktioneer.com/web/current_status",
            cache: false,
            async: true,
            crossDomain: true,
            success: function (data) {
                $('#date_num').html("Current Auctions Date:<b> "
                    + new Date(data.auctions_date).toUTCString()
                    + "</b><br> Count: <b>" + data.count
                    + "</b><br> Total Auctions: <b>" + data.total + "</b>");
            },
            error: function (err) {
                console.log(err);
            }
        });
    };

    var avg = function (arr) {
        var sum = arr.reduce(function (a, b) {
            return a + b;
        });
        var result = Math.log(sum / arr.length);

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
    };


    function runChart(id, name) {

        $.ajax({
            type: "GET",
            url: "http://wow.ooktioneer.com/web/itemchart?id=" + id,
            cache: false,
            async: true,
            success: function (data) {
                var price = [];
                var pp = [];
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
                    label.push($.datepicker.formatDate('yy-mm-dd', new Date(b[i].x)));
                    price.push(b[i].y / 10000);
                    pp.push(b[i].y);
                }

                $('#avg_price').html('AVG Price for <u>'+name+'</u> : '+avg(pp));

                $('#chartjs-0').remove();
                $('#chartDiv').append('<canvas id="chartjs-0" height="600" width="800" class="chartjs"></canvas>');
                new Chart(document.getElementById("chartjs-0"), {
                    "type": "line",
                    "data": {
                        "labels": label,
                        "datasets": [{
                            "label": "Price",
                            "data": price,
                            "borderColor": "rgb(75, 192, 192)",
                        }]
                    }
                });
                var canvas = document.getElementById("chartjs-0");

                canvas.style.maxHeight='768px';
                canvas.style.maxWidth='1024px';
                canvas.style.height='768px';
                canvas.style.width='1024px';

            },
            error: function (data) {

            }
        });


    }

    function getItems(name, params) {

        if(!params)params="";
        name = name.trim();
        $.ajax({
            type: "GET",
            url: "http://wow.ooktioneer.com/web/items?name=" + name + params,
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
                            "<tr>" +
                            "<td>" +
                            "<a name='" + results.auc + "' id='" + results.item.itemId +
                            "' href='#' class='tooltip' onclick='return false;'>" + results.item.name + "</a>" +
                            "<div id='tooltip'><div/>" +
                            "</td>" +

                            // "<div  id='"+ results.auc +"-tooltip' " +
                            // //"<div  id='tooltip' " +
                            // " class='wowhead-tooltip'  ></div></td>" +

                            "<td><span style='color: white;'>" + results.item.itemLevel + "</span>" +

                            "</td>" +
                            "<td><span style='color: white;'>" + results.ownerRealm + "</span></td>" +
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
                            getItems(name, "");
                        };
                        var last = document.getElementById('last');
                        last.onclick = function () {
                            getItems(name, "&page=" + total);
                        };
                        var prev = document.getElementById('prev');
                        prev.onclick = function () {
                            if (page > 0) {
                                getItems(name, "&page=" + (parseInt(page - 1)));
                            }
                        };
                        var next = document.getElementById('next');
                        next.onclick = function () {
                            if (page < total) {
                                getItems(name, "&page=" + (parseInt(page + 1)));
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
            url: "http://wow.ooktioneer.com/web/item/" + id,
            crossDomain: true,
            cache: false,
            async: true,
            dataType: 'text',
            success: function (result) {
                //var tooltip = ele.firstChild;
                //$('.tooltip').remove();
                ele.html(result);

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
            runChart(results.item.itemId,results.item.name);
        };


        $('.tooltip').hover(function() {
            var offset = $(this).offset();
            getItemFromWeb(results.item.itemId, $(this).next('div'));
            $(this).next('div').fadeIn(200).addClass('wowhead-tooltip');
            $(this).next('div').css('left', offset.left + 'px');
        }, function() {
            $(this).next('div').fadeOut(200);
        });


        // ahref.onmouseenter = function () {
        //     getItemFromWeb(results.item.itemId, tooltip);
        // };
        //
        // ahref.onmouseleave = function () {
        //    tooltip.innerHTML='';
        // };

        if (ahref) {
            ahref.className = 'color-q' + quality;
        }
    }

    $('#btn_search').on('click', function () {
        $('#pager').hide();
        getItems($('#search').val());
    });

    $("#search").on("keydown", function search(e) {
        if (e.keyCode == 13) {
            $('#pager').hide();
            getItems($(this).val());
        }
    });

};
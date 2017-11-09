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
        // var price = result.toString();
        // var len = price.length;
        //
        // if (len > 4) {
        //     price = price.substr(0, len - 4) + "g"
        //         + price.substr(len - 2, len) + "s" + price.substr(len - 2, len) + "c";
        // }
        // if (len > 2 && len <= 4) {
        //     price = price.substr(0, len - 2) + "s" + price.substr(len - 2, len) + "c";
        // }
        // if (len <= 2) {
        //     price = price.substr(0, len) + "c";
        // }

        return result;
    };


    Array.prototype.groupBy = function (prop) {
        return this.reduce(function (groups, item) {
            var val = item[prop];
            groups[val] = groups[val] || [];
            groups[val].push(item.y);
            return groups;
        }, {});
    };

//        //function to be executed before an Ajax request is sent.
    function runChart(id) {

        $.ajax({
            type: "GET",
            url: "http://localhost:8080/wow/v1/web/itemchart?id=" + id,
            cache: false,
            async: true,
            success: function (data) {
                var dt = [];
                for (var p in data) {
                    if (data.hasOwnProperty(p)) {
                        dt.push({
                            x: data[p], y: parseInt(p)
                        });
                    }
                }
                //
                // var grouped = dt.groupBy('x');
                //
                // var dt1 = [];
                // for (p in grouped) {
                //     dt1.push({x:p,y:avg(grouped[p])});
                // }

                new Chart(document.getElementById("chartjs-0"), {
                    "type": "line",
                    "data": {
                        //"labels": ["January", "February", "March", "April", "May", "June", "July"],
                        "datasets": [{
                            "label": "Price",
                            "data": dt,
                            "fill": false,
                            "borderColor": "rgb(75, 192, 192)",
                            //"lineTension": 0.1
                        }]
                    },
                    options: {
                        scales: {
                            xAxes: [{
                                type: 'time',
                                time: {
                                    displayFormats: {
                                        quarter: 'YYYY-mm-dd'
                                    }
                                }
                            }]
                        }
                    }
                });
                //Plotly.newPlot('chartDiv', [dt1]);


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
                ele.style.display = 'block';
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


        ahref.onmouseover = function () {
            getItemFromWeb(results.item.id, tooltip);
        };

        ahref.onmouseout = function () {
            tooltip.innerHTML = "";
            tooltip.style.display = 'none';
        };

        if (ahref && ahref !== null) {
            ahref.className = 'color-q' + quality;
        }
    }

    $('#btn_search').on('click', function () {
        runGetitems($('#search').val());
    });

    $("#search").on("keydown", function search(e) {
        if (e.keyCode == 13) {

            runGetitems($(this).val());
        }
    });


}
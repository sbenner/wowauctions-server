window.onload = function () {

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
        var price = result.toString();
        var len = price.length;

        if (len > 4) {
            price = price.substr(0, len - 4) + "g"
                + price.substr(len - 2, len) + "s" + price.substr(len - 2, len) + "c";
        }
        if (len > 2 && len <= 4) {
            price = price.substr(0, len - 2) + "s" + price.substr(len - 2, len) + "c";
        }
        if (len <= 2) {
            price = price.substr(0, len) + "c";
        }

        return price;
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
                            x: $.datepicker.formatDate('yy-mm-dd',
                                new Date(data[p])), y: parseInt(p)
                        });
                    }
                }

                var layout = {
                    title: 'Item Prices'
                };

                var grouped = dt.groupBy('x');

                var dt1 = {mode: 'markers', x: [], y: []};
                for (p in grouped) {
                    dt1.x.push(p);
                    dt1.y.push(avg(grouped[p]));
                }


                Plotly.newPlot('chartDiv', [dt1]);


            },
            error: function (data) {

            }
        });


    }

    function runGetitems(name) {

        name = name.trim();
        $.ajax({
            type: "GET",
            url: "http://localhost:8080/wow/v1/web/items?name=" + name,
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
                            "<td>" + results.item.itemLevel + "</td>" +
                            "<td>" + results.owner + "</td>" +
                            "<td>" + results.bid + "</td>" +
                            "<td>" + results.buyout + "</td>" +
                            "<td>" + results.ppi + "</td>" +
                            "<td>" + results.quantity + "</td>" +
                            "</tr>"
                        );
                        setAhrefColor(results);

                    });
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
        var ahref =  document.getElementsByName(results.auc)[0];//$("a[name=\'"+results.auc+"\']");
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
                ahref.className='color-q'+quality;
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
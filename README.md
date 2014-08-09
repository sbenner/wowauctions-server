wowauctions
===========

wow auctions api/services


Contains services to sync the wow auctions/and items.
Contains REST api to find items by name. 

e.g.
 http://localhost:8080/items?name=reborn 

The auctions are stored in mongodb for later statistics analysis. 
E.g. to see dynamics of an item price change and its deviation.


--------
"wowauctions.auction" collection


    "1" : {
        "key" : {
            "auc" : 1,
            "timestamp" : 1
        },
        "unique" : true,
        "name" : "auc_timestamp",
        "dropDups" : true
    },
    "2" : {
        "v" : 1,
        "key" : {
            "timestamp" : 1
        },
        "name" : "timestamp"
    }


----------
"wowauctions.item" collection:


    "1" : {
        "v" : 1,
        "key" : {
            "itemId" : 1
        },
        "unique" : true,
        "name" : "idx_itemId"
    }
    


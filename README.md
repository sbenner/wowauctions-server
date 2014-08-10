wowauctions
===========

wow auctions api/services


Contains services to sync the wow auctions/and items.
Contains REST api to find items by name. 

e.g.
 
    http://localhost:8080/items?name=reborn

or
    http://localhost:8080/items?name=fel iron ore&exact=1

for exact item name match

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
    

----------
"wowauctions.auctionsArchive":

this index ensures that we have only unique auctions for statistics

     db.auctionsArchive.ensureIndex({auc:1},{name:"auc_uniq",dropDups:true,unique:true,background:true})

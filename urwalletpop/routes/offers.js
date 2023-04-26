const {ObjectId} = require("mongodb");
module.exports = function (app, offerRepository) {
    app.get('/offer/add', function (req, res) {
        res.render("offer/add.twig");
    });

    app.post('/offer/add', function (req, res) {
        let offer = {
            author: req.session.user,
            title: req.body.title,
            description: req.body.description,
            date: new Date(Date.now()).toUTCString(),
            price: req.body.price,
            purchase: false
        }
        validateOffer(offer, res).then(result => {
            if (result) {
                offerRepository.insertOffer(offer).then(result => {
                    res.redirect("/offer/ownedlist");
                }).catch(error => {
                    res.redirect("/offer/add?message=Error al insertar la oferta. &messageType=alert-danger");
                })
            }
        })
    });

    app.get('/offer/ownedList', function (req, res) {
        let filter = {author: req.session.user};
        let options = {};
        let page = parseInt(req.query.page);
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            page = 1;
        }
        offerRepository.getOffersPage(filter, options, page).then(result => {
            if (result === null) {
                let errors = [];
                errors.push({field: "Ofertas", message: "No se encontraron ofertas"});
                res.render("error", {
                    errors: errors,
                });
            } else {
                let lastPage = result.total / 5;
                if (result.total % 5 > 0) {
                    lastPage = lastPage + 1;
                }
                let pages = [];
                for (let i = page - 2; i <= page + 2; i++) {
                    if (i > 0 && i <= lastPage) {
                        pages.push(i);
                    }
                }
                let response = {
                    offers: result.offers,
                    pages: pages,
                    currentPage: page
                }
                res.render("offer/ownedList.twig", response);
            }
        }).catch(error => {
            let errors = [];
            errors.push({field: "Ofertas", message: "Erros al buscar ofertas"});
            res.render("error", {
                errors: errors
            })
        })
    })

    app.get('/offer/delete/:id', function (req, res) {
        let filter = {$and: [{author: req.session.user}, {_id: ObjectId(req.params.id)}, {purchase: false}]};
        let options = {};
        offerRepository.deleteOffer(filter, options).then(result => {
            if (result === null || result.deletedCount === 0) {
                let errors = [];
                errors.push({field: "Ofertas", message: "La oferta no existe o no se encuentra disponible"});
                res.render("error", {
                    errors: errors,
                });
            } else {
                res.redirect("/offer/ownedList");
            }
        }).catch(error => {
            let errors = [];
            errors.push({field: "Ofertas", message: "Error al borrar oferta"});
            res.render("error", {
                errors: errors
            })
        })
    })

    app.get('/offer/offers', function (req, res) {
        let search = req.query.search || '';
        let filter = {author: {$ne: req.session.user}};
        if (search !== '') {
            filter = {
                $and: [
                    {author: {$ne: req.session.user}},
                    {title: {$regex: new RegExp(search, 'i')}}
                ]
            };
        }
        let page = parseInt(req.query.page);
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            page = 1;
        }
        offerRepository.getOffersPage(filter, {}, page).then(result => {
            if (result === null) {
                let errors = [];
                errors.push({field: "Ofertas", message: "No se encontraron ofertas"});
                res.render("error", {
                    errors: errors,
                });
            } else {
                let lastPage = result.total / 5;
                if (result.total % 5 > 0) {
                    lastPage = lastPage + 1;
                }
                let pages = [];
                for (let i = page - 2; i <= page + 2; i++) {
                    if (i > 0 && i <= lastPage) {
                        pages.push(i);
                    }
                }
                let response = {
                    offers: result.offers,
                    pages: pages,
                    currentPage: page,
                    search: search
                }
                res.render("offer/offer.twig", response);
            }
        }).catch(error => {
            let errors = [];
            errors.push({field: "Ofertas", message: "Error al buscar ofertas"});
            res.render("error", {
                errors: errors
            })
        })
    })

    async function validateOffer(offer, res) {
        let errors = [];
        if (typeof offer.title === "undefined" || offer.title === null || offer.title.trim().length <= 3) {
            errors.push({
                field: "Titulo",
                message: "Titulo de la oferta no puede estar vacio o tener menos de 3 caracteres"
            });
        }
        if (typeof offer.description === "undefined" || offer.description === null || offer.description.trim().length <= 10) {
            errors.push({
                field: "Descripcion",
                message: "Descripcion de la oferta no puede estar vacio o tener menos de 10 caracteres"
            });
        }
        if (typeof offer.price === "undefined" || offer.price === null || offer.price.trim().length === 0) {
            errors.push({field: "Precio", message: "Precio de la oferta no puede estar vacio"});
        } else if (offer.price < 0) {
            errors.push({field: "Precio", message: "Precio de la oferta no puede ser negativo"});
        }
        if (errors.length > 0) {
            res.render("error", {
                errors: errors
            });
            return false;
        }
        return true;
    }
    
    app.get('/offer/purchasedList', function (req, res) {
        const user=req.session.user;
        let filter = {buyer :user, purchased:true};
        let options = {sort: { title: 1}};
        let page = parseInt(req.query.page); // Es String !!!
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            //Puede no venir el param
            page = 1;
        }
        offersRepository.getOffersPg(filter, options, page).then(result => {
            let lastPage = result.total / 4;
            if (result.total % 4 > 0) { // Sobran decimales
                lastPage = lastPage + 1;
            }
            let pages = []; // paginas mostrar
            for (let i = page - 2; i <= page + 2; i++) {
                if (i > 0 && i <= lastPage) {
                    pages.push(i);
                }
            }
            let offers= [{
                price : 100,
                title: "RTX3090",
                description: "La mejor tarjeta gráfica",
                owner: "user99@email.com"
            }]
            let response = {
                offers : offers,
                //offers: result.offers,
                pages: pages,
                currentPage: page
            }
            res.render("purchasedList.twig", response);
        }).catch(error => {
            res.send("Se ha producido un error al listar las ofertas compradas " + error)
        });
    }),
        app.get('/offer/purchase/:id', function (req, res) {
            let filter = {_id: ObjectId(req.params.id)};
            offersRepository.findOffer(filter,{}).then(offer =>{
               if(req.session.user !== offer.owner && offer.purchased===false){
                   //Comprobar si el usuario tiene el dinero suficiente
                   if(req.session.amount >= offer.price){
                       offer.purchased=true;
                       offer.buyer=req.session.user;
                       req.session.amount=req.session.amount-offer.price
                       offersRepository.updateOffer(offer,filter,{}).then(result => {
                               if (result == null) {
                                   res.send("Error al comprar la oferta");
                               } else {
                                   res.redirect("/offer/searchList");
                               }
                       }).catch(error => {
                           res.send("Se ha producido un error al modificar la oferta " + error)
                       });
                   }
               }else{
                   res.send("Se ha producido un error al modificar la oferta");
               }
            })
        });
}
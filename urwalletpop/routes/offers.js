const {ObjectId} = require("mongodb");
module.exports = function (app, offerRepository, usersRepository) {

    /**
     * Método que devuelve la vista de la pagina de añadir una oferta
     */
    app.get('/offer/add', function (req, res) {
        res.render("offer/add.twig", {
            user: req.session.user,
            role: req.session.role,
            amount: req.session.amount,
            date: req.session.date
        });
    });

    /**
     * Método que añade una oferta a la base de datos y redirige a la lista de ofertas
     * si se añade correctamente o redirige a la pagina de añadir mostrando los errores
     */
    app.post('/offer/add', function (req, res) {
        let feature = false;
        if (req.body.feature === "on") {
            feature = true;
        }
        let offer = {
            author: req.session.user,
            title: req.body.title,
            description: req.body.description,
            date: new Date(Date.now()).toUTCString(),
            price: req.body.price,
            purchase: false,
            buyer: null,
            feature: feature,
        }
        validateOffer(offer, res).then(errors => {
            if (errors.length > 0) {
                res.render("offer/add.twig", {
                    errors: errors,
                    user: req.session.user,
                    role: req.session.role,
                    amount: req.session.amount,
                    date: req.session.date
                });
            } else {
                if (offer.feature) {
                    usersRepository.findUser({email: req.session.user}, {}).then(user => {
                            if (user.amount >= 20) {
                                req.session.amount = req.session.amount - 20;
                                user.amount -= 20;
                                usersRepository.updateUser(user, {email: user.email}, {}).then();
                            } else {
                                res.redirect("/offer/add?message=No dispone del dinero suficiente. &messageType=alert-danger");
                            }
                        }
                    )
                }
                offerRepository.insertOffer(offer).then(result => {
                    res.redirect("/offer/ownedlist");
                }).catch(error => {
                    res.redirect("/offer/add?message=Error al insertar la oferta. &messageType=alert-danger");
                })
            }
        })
    });

    /**
     * Metodo que devuelve la vista de la lista de ofertas que tiene el usuario
     */
    app.get('/offer/ownedList', function (req, res) {
        let filter = {author: req.session.user};
        let search = req.query.search || '';
        if (search !== '') {
            filter = {
                $and: [
                    {author:  req.session.user},
                    {title: {$regex: new RegExp(search, 'i')}}
                ]
            };
        }
        let options = {};
        let page = parseInt(req.query.page);
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            page = 1;
        }

        offerRepository.getOffersPage(filter, options, page).then(result => {
            if (result === null) {
                let errors = [];
                errors.push({type: "Ofertas", message: "No se encontraron ofertas"});
                res.render("offer/ownedList.twig", {
                    errors: errors,
                    user: req.session.user,
                    role: req.session.role,
                    amount: req.session.amount,
                    date: req.session.date
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
                    user: req.session.user,
                    role: req.session.role,
                    amount: req.session.amount,
                    date: req.session.date,
                    pages: pages,
                    currentPage: page,
                    search : search
                }
                res.render("offer/ownedList.twig", response);
            }
        }).catch(error => {
            let errors = [];
            errors.push({type: "Ofertas", message: "Error al buscar ofertas"});
            res.render("offer/ownedList.twig", {
                errors: errors,
                user: req.session.user,
                role: req.session.role,
                amount: req.session.amount,
                date: req.session.date
            })
        })
    })

    /**
     * Método que borra una oferta de la base de datos y redirige a la lista de ofertas si se borra correctamente,
     * o redirige a la página de añadir mostrando los errores si ya está vendida o no es del propietario
     */
    app.get('/offer/delete/:id', function (req, res) {
        let filter = {$and: [{author: req.session.user}, {_id: ObjectId(req.params.id)}, {purchase: false}]};
        let options = {};
        offerRepository.deleteOffer(filter, options).then(result => {
            if (result === null || result.deletedCount === 0) {
                let errors = [];
                errors.push({type: "Ofertas", message: "La oferta no existe o no se encuentra disponible"});
                res.render("offer/ownedList.twig", {
                    errors: errors,
                    user: req.session.user,
                    role: req.session.role,
                    amount: req.session.amount,
                    date: req.session.date
                });
            } else {
                res.redirect("/offer/ownedList");
            }
        }).catch(error => {
            let errors = [];
            errors.push({type: "Ofertas", message: "Error al borrar oferta"});
            res.render("offer/ownedList.twig", {
                errors: errors,
                user: req.session.user,
                role: req.session.role,
                amount: req.session.amount,
                date: req.session.date
            })
        })
    })

    /**
     * Metodo que devuelve la vista de la lista de ofertas disponibles para el usuario, sin tener en cuenta las propias
     * y pudiendo filtrar por el titulo de la oferta sin distinguir entre mayusculas y minusculas.
     */
    app.get('/offer/searchList', function (req, res) {
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
                errors.push({type: "Ofertas", message: "No se encontraron ofertas"});
                res.render("offer/searchList.twig", {
                    errors: errors,
                    user: req.session.user,
                    role: req.session.role,
                    amount: req.session.amount,
                    date: req.session.date
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
                    user: req.session.user,
                    role: req.session.role,
                    amount: req.session.amount,
                    date: req.session.date,
                    pages: pages,
                    currentPage: page,
                    search: search
                }
                res.render("offer/searchList.twig", response);
            }
        }).catch(error => {
            let errors = [];
            errors.push({type: "Ofertas", message: "Error al buscar ofertas"});
            res.render("offer/searchList.twig", {
                errors: errors,
                user: req.session.user,
                role: req.session.role,
                amount: req.session.amount,
                date: req.session.date
            })
        })
    })

    /**
     * Metodo que retorna las ofertas compradas
     */
    app.get('/offer/purchasedList', function (req, res) {
        let filter = {buyer: req.session.user};
        let options = {};
        let page = parseInt(req.query.page);
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            page = 1;
        }
        offerRepository.getOffersPage(filter, options, page).then(result => {
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
            let response = {
                offers: result.offers,
                user: req.session.user,
                role: req.session.role,
                amount: req.session.amount,
                date: req.session.date,
                pages: pages,
                currentPage: page
            }
            res.render("offer/purchasedList.twig", response);
        }).catch(error => {
            let errors = [];
            errors.push({
                type: "Listar ofertas",
                message: "Se ha producido un error al listar las ofertas compradas" + error
            });
            res.render("offer/purchasedList.twig", {
                errors: errors,
                user: req.session.user,
                role: req.session.role,
                amount: req.session.amount,
                date: req.session.date
            });
        });
    });

    /**
     * Metodo que marca la oferta com comprada y descuenta el dinero al comprador
     */
    app.get('/offer/purchase/:id', function (req, res) {
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
        let options = {};
        let page = parseInt(req.query.page);
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            page = 1;
        }
        offerRepository.getOffersPage(filter, options, page).then(result => {
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
            filter = {_id: ObjectId(req.params.id)};
            offerRepository.findOffer(filter, {}).then(offer => {
                if (req.session.user !== offer.author && offer.purchase === false) {
                    //Comprobar si el usuario tiene el dinero suficiente
                    if (req.session.amount >= offer.price) {
                        offer.purchase = true;
                        offer.buyer = req.session.user;
                        req.session.amount = req.session.amount - offer.price
                        offerRepository.updateOffer(offer, filter, {}).then(result => {
                            if (result == null) {
                                let errors = [];
                                errors.push({
                                    type: "Comprar oferta",
                                    message: "Se ha producido un error al comprar la oferta"
                                });
                                res.render("offer/searchList.twig", {
                                    offers: result.offers,
                                    pages: pages,
                                    currentPage: page,
                                    errors: errors,
                                    user: req.session.user,
                                    role: req.session.role,
                                    amount: req.session.amount,
                                    date: req.session.date
                                });
                            } else {
                                usersRepository.findUser({email: req.session.user}, {}).then(user => {
                                        user.amount = req.session.amount;
                                        usersRepository.updateUser(user, {email: user.email}, {}).then();
                                    }
                                )
                                res.redirect("/offer/searchList");
                            }
                        }).catch(error => {
                            let errors = [];
                            errors.push({
                                type: "Comprar oferta",
                                message: "Se ha producido un error al comprar la oferta"
                            });
                            res.render("offer/searchList.twig", {
                                offers: result.offers,
                                pages: pages,
                                currentPage: page,
                                errors: errors,
                                user: req.session.user,
                                role: req.session.role,
                                amount: req.session.amount,
                                date: req.session.date
                            });
                        });
                    } else {
                        let errors = [];
                        errors.push({type: "Comprar oferta", message: "El saldo es insuficiente"});
                        res.render("offer/searchList.twig", {
                            offers: result.offers,
                            pages: pages,
                            currentPage: page,
                            errors: errors,
                            user: req.session.user,
                            role: req.session.role,
                            amount: req.session.amount,
                            date: req.session.date
                        });
                    }
                } else {
                    let errors = [];
                    errors.push({type: "Comprar oferta", message: "Se ha producido un error al comprar la oferta"});
                    res.render("offer/searchList.twig", {
                        offers: result.offers,
                        pages: pages,
                        currentPage: page,
                        errors: errors,
                        user: req.session.user,
                        role: req.session.role,
                        amount: req.session.amount,
                        date: req.session.date
                    });
                }
            })
        });
    });

    /**
     * Metodo que marca una oferta como destacada y descuenta 20$ de la cuenta
     * del usuario
     */
    app.get('/offer/feature/:id', function (req, res) {
        let filter = {author: req.session.user};
        let options = {};
        let page = parseInt(req.query.page);
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            page = 1;
        }
        offerRepository.getOffersPage(filter, options, page).then(result => {
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
            filter = {_id: ObjectId(req.params.id)};
            offerRepository.findOffer(filter, {}).then(offer => {
                if (req.session.user === offer.author && offer.feature === false) {
                    //Comprobar si el usuario tiene el dinero suficiente
                    if (req.session.amount >= 20) {
                        offer.feature = true;
                        req.session.amount = req.session.amount - 20
                        offerRepository.updateOffer(offer, filter, {}).then(result => {
                            if (result == null) {
                                let errors = [];
                                errors.push({
                                    type: "Destacar oferta",
                                    message: "Se ha producido un error al destacar la oferta"
                                });
                                res.render("offer/ownedList.twig", {
                                    offers: result.offers,
                                    pages: pages,
                                    currentPage: page,
                                    errors: errors,
                                    user: req.session.user,
                                    role: req.session.role,
                                    amount: req.session.amount,
                                    date: req.session.date
                                });
                            } else {
                                usersRepository.findUser({email: req.session.user}, {}).then(user => {
                                        user.amount = req.session.amount;
                                        usersRepository.updateUser(user, {email: user.email}, {}).then();
                                    }
                                )
                                res.redirect("/offer/ownedList");
                            }
                        }).catch(error => {
                            let errors = [];
                            errors.push({
                                type: "Destacar oferta",
                                message: "Se ha producido un error al destacar la oferta" + error
                            });
                            res.render("offer/ownedList.twig", {
                                offers: result.offers,
                                pages: pages,
                                currentPage: page,
                                errors: errors,
                                user: req.session.user,
                                role: req.session.role,
                                amount: req.session.amount,
                                date: req.session.date
                            });
                        });
                    } else {
                        let errors = [];
                        errors.push({type: "Destacar oferta", message: "El saldo es insuficiente"});
                        res.render("offer/ownedList.twig", {
                            offers: result.offers,
                            pages: pages,
                            currentPage: page,
                            errors: errors,
                            user: req.session.user,
                            role: req.session.role,
                            amount: req.session.amount,
                            date: req.session.date
                        });
                    }
                } else {
                    let errors = [];
                    errors.push({type: "Destacar oferta", message: "Se ha producido un error al destacar la oferta"});
                    res.render("offer/ownedList.twig", {
                        offers: result.offers,
                        pages: pages,
                        currentPage: page,
                        errors: errors,
                        user: req.session.user,
                        role: req.session.role,
                        amount: req.session.amount,
                        date: req.session.date
                    });
                }
            })
        })
    });

    /**
     * Método que comprueba si los datos introducidos para crear una oferta son válidos
     * @param offer oferta a comprobar
     * @returns {Promise<*[]>} Lista de errores de los atributos
     */
    async function validateOffer(offer) {
        let errors = [];
        if (typeof offer.title === "undefined" || offer.title === null || offer.title.trim().length <= 3) {
            errors.push({
                type: "Titulo",
                message: "Titulo de la oferta no puede estar vacío o tener menos de 3 caracteres"
            });
        }
        if (typeof offer.description === "undefined" || offer.description === null || offer.description.trim().length <= 10) {
            errors.push({
                type: "Descripción",
                message: "Descripción de la oferta no puede estar vacío o tener menos de 10 caracteres"
            });
        }
        if (typeof offer.price === "undefined" || offer.price === null || offer.price.trim().length === 0) {
            errors.push({type: "Precio", message: "Precio de la oferta no puede estar vacio"});
        } else if (offer.price < 0) {
            errors.push({type: "Precio", message: "Precio de la oferta no puede ser negativo"});
        }
        return errors;
    }
}
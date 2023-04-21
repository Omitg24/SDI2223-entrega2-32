const {ObjectId} = require("mongodb");
module.exports = function (app, offersRepository, userRepository) {
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
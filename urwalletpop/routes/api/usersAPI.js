const {ObjectId} = require("mongodb");
module.exports = function (app, usersRepository, offerRepository,conversationRepository) {

    app.get("/api/conversation/:offerId/:interestedId", function (req, res) {
        let filter = {$and: [{interested: {_id:ObjectId(req.params.interestedId)}}
                                        , {offer: {_id:ObjectId(req.params.offerId)}}]};
        let options = {};
        conversationRepository.findConversation(filter, options).then(conversation => {
            res.status(200);
            res.json({conversation: conversation});
        }).catch(error => {
            res.status(500);
            res.json({error: "Error al obtener la conversacion"});
        })
    });

    app.get("/api/conversation/list", function (req, res) {
        let filter = {$or: [{interested: res.user}, {offer: {author:res.user}}]};
        let options = {};
        conversationRepository.getConversations(filter, options).then(conversations => {
            res.status(200);
            res.json({conversations: conversations});
        }).catch(error => {
            res.status(500);
            res.json({error: "Error al obtener las conversaciones."});
        })
    });

    app.get("/api/conversation/delete/:id", function (req, res) {
        conversationService.deleteConversation(id);
        return "redirect:/conversation/list";
    });

    app.post("/api/conversation/:offerId/:interestedId", function (req, res) {
        //Validamos que el mensaje no este vacio
        //sendMessageValidator.validate(message, result);
        //if (result.hasErrors()) {
        //    prepareConversation(offerId, interestedId, model);
        //    return "conversation/conversation";
        //}

        let message = {
            date: new Date(),
            owner: res.user,
            text: req.body.message
        }
        //Obtenemos o creamos la conversación en función de si existe o no
        let conversationFilter = {$and: [{interested: {_id:ObjectId(req.params.interestedId)}}
                , {offer: {_id:ObjectId(req.params.offerId)}}]};

        let offerFilter = {_id:ObjectId(req.params.offerId)};
        let options = {};
        conversationRepository.findConversation(conversationFilter, options).then(conversation => {
            let conver;
            if(conversation){
                conver = conversation;
                conver.messages.push(message);
                conversationRepository.updateConversation(conver,{_id:ObjectId(conver._id)},options).then(res=>{
                    res.status(200);
                    res.json({message: "Conversación actualizada"});
                }).catch(error=>{
                    res.status(500);
                    res.json({error: "Error al actualizar la conversacion"});
                });
            }else{
                offerRepository.findOffer(offerFilter,options).then(offer=>{
                    conver={offer:offer,interested:res.user,messages:[message]}
                    conversationRepository.insertConversation(conver).then(res=>{
                        res.status(200);
                        res.json({message: "Conversación insertada"});
                    }).catch(error=>{
                        res.status(500);
                        res.json({error: "Error al insertar la conversacion"});
                    })
                }).catch(error=>{
                    res.status(500);
                    res.json({error: "Error al obtener la oferta"});
                })
                conver={}
            }
        }).catch(error => {
            res.status(500);
            res.json({error: "Error al obtener la conversacion"});
        })
    });

    app.get("/api/offers", function (req, res) {
        let filter = {author: {$ne: res.user}};
        let options = {};
        offerRepository.getOffers(filter, options).then(offers => {
            console.log(res.user);
            res.status(200);
            res.json({ offers: offers});
        }).catch(error => {
            res.status(500);
            res.json({error: "Error al obtener las ofertas."});
        })
    });

    app.post("/api/users/login", function (req, res) {
        try {
            let securePassword = app.get("crypto").createHmac('sha256', app.get('clave'))
                .update(req.body.password).digest('hex');
            let filter = {
                email: req.body.email,
                password: securePassword
            }
            let user = {
                email: req.body.email,
                password: req.body.password
            }
            let options = {};
            validateUsersData(user).then(errors => {
                if (errors !== null && errors.length > 0) {
                    res.status(403);
                    res.json({
                        errors: errors
                    })
                } else {
                    usersRepository.findUser(filter, options).then(user => {
                        if (user == null) {
                            res.status(401);
                            res.json({
                                message: "Inicio de sesión no correcto", authenticated: false
                            })
                        } else {
                            let token = app.get('jwt').sign(
                                {user: user.email, time: Date.now() / 1000},
                                "secreto");
                            res.status(200);
                            res.json({
                                message: "Inicio de sesión correcto",
                                authenticated: true,
                                token: token
                            })
                        }
                    }).catch(error => {
                        res.status(401);
                        res.json({
                            message: "Se ha producido un error al verificar credenciales", authenticated: false
                        })
                    });
                }
            });
        } catch (e) {
            res.status(500);
            res.json({
                message: "Se ha producido un error al verificar credenciales", authenticated: false
            })
        }
    });


    async function validateUsersData(user) {
        let errors = new Array();
        if (user.email === null || typeof user.email === 'undefined' ||
            user.email === "") {
            errors.push("El campo email no puede estar vacío");
        }
        if (user.password === null || typeof user.password === 'undefined' ||
            user.password === "") {
            errors.push("El campo contraseña no puede estar vacío");
        }
        if (errors.length <= 0)
            return null;
        return errors;
    }


}
const {ObjectId} = require("mongodb");
module.exports = function (app, usersRepository, offerRepository,conversationRepository) {

    app.get("/api/conversation/:offerId/:interestedEmail", function (req, res) {
        //Obtenemos o creamos la conversación en función de si existe o no
        let conversationFilter = {
            interested: req.params.interestedEmail,
            'offer._id': ObjectId(req.params.offerId)
        };
        let options = {};
        conversationRepository.findConversation(conversationFilter, options).then(conversation => {
            res.status(200);
            res.json({conversation: conversation,user:res.user});
        }).catch(error => {
            res.status(500);
            res.json({error: "Error al obtener la conversacion"});
        })
    });

    app.get("/api/conversation/list", function (req, res) {
        let filter = {
            $or: [
                { interested: res.user },
                { 'offer.author': res.user }
            ]
        };
        let options = {};
        conversationRepository.getConversations(filter, options).then(conversations => {
            res.status(200);
            res.json({conversations: conversations,user:res.user});
        }).catch(error => {
            res.status(500);
            res.json({error: "Error al obtener las conversaciones."});
        })
    });

    app.post("/api/conversation/delete/:id", function (req, res) {
        let filter = {
            _id:ObjectId(req.params.id)
        };
        let options = {};
        conversationRepository.deleteConversation(filter,options).then(result=>{
            res.status(200);
            res.json({result: result});
        }).catch(error=>{
            res.status(500);
            res.json({error: "Error al eliminar las conversaciones."});
        })
    });

    app.post("/api/conversation/:offerId/:interestedEmail", function (req, res) {
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
        let offerFilter = {_id:ObjectId(req.params.offerId)};
        //Obtenemos o creamos la conversación en función de si existe o no
        let conversationFilter = {
            interested: req.params.interestedEmail,
            'offer._id': ObjectId(req.params.offerId)
        };

        let options = {};
        let user = res.user;
        conversationRepository.findConversation(conversationFilter, options).then(conversation => {
            let conver;
            console.log(conversation+"tratratra");
            if(conversation!=null){
                conver = conversation;
                conver.messages.push(message);
                conversationRepository.updateConversation(conver,{_id:ObjectId(conver._id)},options).then(result=>{
                    res.status(200);
                    res.json({message: "Conversación actualizada"});
                }).catch(error=>{
                    res.status(500);
                    res.json({error: "Error al actualizar la conversacion"});
                });
            }else{
                offerRepository.findOffer(offerFilter,options).then(offer=>{
                    conver={offer:offer,interested:user,messages:[message]}
                    conversationRepository.insertConversation(conver).then(result=>{
                        res.status(200);
                        res.json({message: "Conversación insertada"});
                    }).catch(error=>{
                        console.log(error)
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
            console.log("b3");
            res.status(500);
            res.json({error: "Error al obtener la conversacion"});
        })
    });

    app.get("/api/offers", function (req, res) {
        let filter = {author: {$ne: res.user}};
        let options = {};
        offerRepository.getOffers(filter, options).then(offers => {
            res.status(200);
            res.json({ offers: offers,interested: res.user});
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
const {ObjectId} = require("mongodb");
const {validationResult} = require('express-validator');
const {messageValidatorInsert} = require('./sendMessageValidate');
module.exports = function (app, offerRepository, conversationRepository, messageRepository) {

    /**
     * Método que marca una mensaje como leído a través de una
     * petición
     */
    app.put("/api/messages/:id", function (req, res) {
        let messageId = ObjectId(req.params.id);
        let filter = {_id: messageId};
        //Si la _id NO no existe, no crea un nuevo documento.
        messageRepository.findMessage(filter, {}).then(message => {
            if (message.read === false) {
                if(message.owner !== res.user){
                    message.read=true;
                    messageRepository.updateMessage(message, filter, {}).then(result => {
                        if (result === null) {
                            res.status(404);
                            res.json({error: "ID inválido o no existe, no se ha actualizado el mensaje."});
                        }
                        //La _id No existe o los datos enviados no difieren de los ya almacenados.
                        else if (result.modifiedCount == 0) {
                            res.status(409);
                            res.json({error: "No se ha modificado ningun mensaje."});
                        }
                        else{
                            res.status(200);
                            res.json({
                                message: "Mensaje modificado correctamente.",
                                result: result
                            })
                        }
                    }).catch(error => {
                        res.status(500);
                        res.json({error: "Se ha producido un error al modificar el mensaje."})
                    });
                }
            }
        }).catch(error => {
                res.status(500);
                res.json({error: "Error al marcar el mensaje como leído: "+error});
        });
    });

    /**
     * Metodo que obtiene la conversacion dada una oferta y el interesado y obtiene sus mensajes
     * La conversacion solo la puede obtener el dueno de la oferta y el intersado
     */
    app.get("/api/conversation/:offerId/:interestedEmail", function (req, res) {
        let conversationFilter = {
            interested: req.params.interestedEmail,
            'offer._id': ObjectId(req.params.offerId)
        };
        let options = {};
        conversationRepository.findConversation(conversationFilter, options).then(conversation => {
            //Si no es ni interesado ni vendedor
            if(conversation!=null && !(conversation.offer.author == res.user || req.params.interestedEmail ==res.user) && req.params.interestedEmail ==conversation.offer.author){
                res.status(403);
                res.json({error: "No puedes obtener la conversación"});
                return;
            }
            //Mensajes que sean de la conversacion concreta
            let messageFilter = {
                offer: ObjectId(req.params.offerId),
                interested: req.params.interestedEmail
            }
            messageRepository.findMessages(messageFilter, {}).then(messages => {
                res.status(200);
                res.json({conversation: conversation, messages: messages, user: res.user});
            })
        }).catch(error => {
            res.status(500);
            res.json({error: "Error al obtener la conversacion"});
        })
    });

    /**
     * Metodo que devuelve las conversaciones en las que aparece la persona que lo solicita,como interesado o como vendedor
     */
    app.get("/api/conversation/list", function (req, res) {
        //Conversaciones del usuario como interesado o como author
        let filter = {
            $or: [
                {interested: res.user},
                {'offer.author': res.user}
            ]
        };
        let options = {};
        conversationRepository.getConversations(filter, options).then(conversations => {
            for (let i = 0; i < conversations.length; i++) {
                let owner;
                if (res.user === conversations[i].interested) {
                    owner = conversations[i].offer.author;
                } else {
                    owner = conversations[i].interested;
                }
                let filter = {
                    offer: conversations[i].offer._id,
                    interested: conversations[i].interested,
                    owner: owner,
                    read: false
                }
                conversations[i].numberMessages = 0;
                messageRepository.findMessages(filter, {}).then(messages => {
                    conversations[i].numberMessages = messages.length;
                    if(i === conversations.length -1){
                        res.status(200);
                        res.json({conversations: conversations, user: res.user});
                    }
                })

            }
            if(conversations.length === 0){
                res.status(200);
                res.json({conversations: conversations, user: res.user});
            }
        }).catch(error => {
            res.status(500);
            res.json({error: "Error al obtener las conversaciones."});
        })
    });

    /**
     * Metodo que elimina un conversacion y sus mensajes dado un id (de conversacion)
     */
    app.post("/api/conversation/delete/:id", function (req, res) {
        let conversationFilter = {
            _id: ObjectId(req.params.id)
        };
        let options = {};
        conversationRepository.findConversation(conversationFilter, options).then(conversation => {
            if (conversation.offer.author == res.user || conversation.interested == res.user) {
                let messageFilter = {offer: conversation.offer._id, interested: conversation.interested}
                messageRepository.deleteMessages(messageFilter, options).then(result => {
                    conversationRepository.deleteConversation(conversationFilter, options).then(result => {
                        res.status(200);
                        res.json({result: result});
                    }).catch(error => {
                        res.status(500);
                        res.json({error: "Error al eliminar las conversaciones."});
                    })
                }).catch(error => {
                    res.status(500);
                    res.json({error: "Error al eliminar los mensajes."});
                })
            } else {
                res.status(403);
                res.json({error: "No puedes eliminar la conversación"});
            }
        }).catch(error => {
            console.log(error);
            res.status(500);
            res.json({error: "Error al obtener la conversacion."});
        })


    });

    /**
     * Metodo que envia un mensaje en la conversacion, si no se ha enviado un mensaje en la conversacion, se crea
     */
    app.post("/api/conversation/:offerId/:interestedEmail", messageValidatorInsert, function (req, res) {
        //Creamos el mensaje
        let message = {
            owner: res.user,
            interested: req.params.interestedEmail,
            offer: ObjectId(req.params.offerId),
            date: new Date(),
            text: req.body.message,
            read: false
        }
        let offerFilter = {_id: ObjectId(req.params.offerId)};
        let conversationFilter = {
            interested: req.params.interestedEmail,
            'offer._id': ObjectId(req.params.offerId)
        };

        let user = res.user;
        let options = {};
        //Obtenemos la oferta asociada al id
        offerRepository.findOffer(offerFilter, options).then(offer => {
            //Si no es ni author ni interesado no envia mensaje
            if (offer.author == user || user == req.params.interestedEmail) {
                const errors = validationResult(req);
                //Validamos el mensaje
                if (!errors.isEmpty()) {
                    res.status(400);
                    res.json({errors: errors.array()})
                } else {
                    //Insertamos el mensaje
                    messageRepository.insertMessage(message).then(result => {
                        conversationRepository.findConversation(conversationFilter, options).then(conversation => {
                            if (conversation === null) {
                                let conver;
                                //Comprobamos que no se puede iniciar una conversacion en tu propia oferta
                                if(offer.author != req.params.interestedEmail){
                                    conver={offer:offer,interested:user}
                                    conversationRepository.insertConversation(conver).then(result=>{
                                        res.status(200);
                                        res.json({message: "Conversación insertada"});
                                    }).catch(error => {
                                        res.status(500);
                                        res.json({errors: "Error al insertar la conversacion"});
                                    })
                                    conver = {}
                                } else {
                                    res.status(403);
                                    res.json({errors: "No puedes iniciar una conversación en tu propia oferta"});
                                }
                            } else {
                                res.status(200);
                                res.json({message: "Mensaje enviado"});
                            }
                        }).catch(error => {
                            res.status(500);
                            res.json({errors: "Error al obtener la conversacion"});
                        })
                    })
                }
            } else {
                res.status(403);
                res.json({errors: "No puedes enviar mensajes para esta oferta"});
            }
        }).catch(error => {
            res.status(500);
            res.json({errors: "Error al obtener la oferta"});
        })


    });
}
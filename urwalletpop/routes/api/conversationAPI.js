const {ObjectId} = require("mongodb");
module.exports = function (app, offerRepository, conversationRepository,messageRepository) {

    app.put("/api/messages/:id", function (req, res) {
        let messageId = ObjectId(req.params.id);
        let filter = {_id: messageId};
        //Si la _id NO no existe, no crea un nuevo documento.
        messageRepository.findMessage(filter, {}).then(message => {
            if (message.read === false) {
                if(message.interested=== res.user){
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
                        res.json({error : "Se ha producido un error al modificar el mensaje."})
                    });
                }
            }
        }).catch(error => {
                res.status(500);
                res.json({error: "Error al marcar el mensaje como leído."});
        });
    });

    app.get("/api/conversation/:offerId/:interestedEmail", function (req, res) {
        //Obtenemos o creamos la conversación en función de si existe o no
        let conversationFilter = {
            interested: req.params.interestedEmail,
            'offer._id': ObjectId(req.params.offerId)
        };
        let options = {};
        conversationRepository.findConversation(conversationFilter, options).then(conversation => {

            if(conversation!=null && !(conversation.offer.author == res.user || req.params.interestedEmail ==res.user)){
                res.status(403);
                res.json({error: "No puedes obtener la conversación"});
                return;
            }
            let messageFilter = {
                offer: ObjectId(req.params.offerId)
            }
            messageRepository.findMessages(messageFilter,{}).then(messages =>{
                res.status(200);
                res.json({conversation: conversation, messages: messages,user:res.user});
            })
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
        let conversationFilter = {
            _id:ObjectId(req.params.id)
        };
        let options = {};
        conversationRepository.findConversation(conversationFilter,options).then(conversation=>{
            if(conversation.offer.author == res.user || conversation.interested == res.user){
                let messageFilter={offer:conversation.offer._id,interested:conversation.interested}
                messageRepository.deleteMessages(messageFilter,options).then(result=>{
                    conversationRepository.deleteConversation(conversationFilter,options).then(result=>{
                        res.status(200);
                        res.json({result: result});
                    }).catch(error=>{
                        res.status(500);
                        res.json({error: "Error al eliminar las conversaciones."});
                    })
                }).catch(error=>{
                    res.status(500);
                    res.json({error: "Error al eliminar los mensajes."});
                })
            }else{
                res.status(403);
                res.json({error: "No puedes eliminar la conversación"});
            }
        }).catch(error=>{
            console.log(error);
            res.status(500);
            res.json({error: "Error al obtener la conversacion."});
        })


    });

    app.post("/api/conversation/:offerId/:interestedEmail", function (req, res) {

        let message = {
            owner: res.user,
            interested:req.params.interestedEmail,
            offer : ObjectId(req.params.offerId),
            date: new Date(),
            text: req.body.message,
            read: false
        }
        let offerFilter = {_id:ObjectId(req.params.offerId)};
        //Obtenemos o creamos la conversación en función de si existe o no
        let conversationFilter = {
            interested: req.params.interestedEmail,
            'offer._id': ObjectId(req.params.offerId)
        };


        messageRepository.insertMessage(message).then(result =>{
            let options = {};
            let user = res.user;
            conversationRepository.findConversation(conversationFilter, options).then(conversation => {
                if(conversation === null){
                    let conver;
                    if(user == req.params.interestedEmail){
                        offerRepository.findOffer(offerFilter,options).then(offer=>{
                            conver={offer:offer,interested:user}
                            conversationRepository.insertConversation(conver).then(result=>{
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
                    }else{
                        res.status(403);
                        res.json({error: "No puedes iniciar una conversación en tu propia oferta"});
                    }

                }
            }).catch(error => {
                res.status(500);
                res.json({error: "Error al obtener la conversacion"});
            })
        })
    });
}
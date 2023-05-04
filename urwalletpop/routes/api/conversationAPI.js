const {ObjectId} = require("mongodb");
module.exports = function (app, usersRepository, conversationsRepository,messagesRepository) {

    app.put("/api/messages/:id", function (req, res) {
        let messageId = ObjectId(req.params.id);
        let filter = {_id: messageId};
        //Si la _id NO no existe, no crea un nuevo documento.
        messagesRepository.findMessage(filter, {}).then(message => {
            if (message.read === false) {
                if(message.author === res.user || message.buyer=== res.user){
                    message.read=true;
                    messagesRepository.updateMessage(message, filter, {}).then(result => {
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


}
var express = require('express');

// Create wrapper function that will adjust router based on provided configuration
var loggerRouter = function (logsRepository) {

    let router = express.Router();
    //Interceptamos las peticiones para logearlas
    router.use(function (req, res, next) {
        let url = req.url;
        //Comprobamos los tipos de peticiones especiales
        if (!url.includes("login")) {
            let type = "PET";
            if (url.includes("signup")) {
                type = "ALTA";
            }
            let log = {
                date: Date.now(),
                action: req.method,
                url: req.originalUrl,
                type: type,
            }
            logsRepository.insertLog(log).catch(error => {
                console.log("No se ha podido registrar la peticion " + req.method)
            });
        }
        next();
    });

    return router;
}

module.exports = loggerRouter;
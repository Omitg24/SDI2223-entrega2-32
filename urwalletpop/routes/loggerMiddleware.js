var express = require('express');

// Create wrapper function that will adjust router based on provided configuration
var loggerRouter = function (logsRepository) {

    let router = express.Router();

    router.use(function (req, res, next) {
        let type = "PET";
        let url = req.url;
        if(url.includes("signup")){
            type="ALTA";
        }

        let log = {
            date:Date.now(),
            action:req.method,
            url:req.url,
            type:type,
        }
        logsRepository.insertLog(log).catch(error => {
            console.log("No se ha podido registrar la peticion " + req.method)
        });
        next();
    });

    return router;
}

module.exports = loggerRouter;
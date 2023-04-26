const express = require('express');
const userSessionRouter = express.Router();
userSessionRouter.use(function (req, res, next) {
    console.log("userStandardSessionRouter");

    if (req.session.role === 'standard') {
        next();
    } else {
        let errors = [];
        errors.push({field: "Error de permisos", message: "No tienes permisos para acceder a esta página"});
        console.log("va a: " + req.originalUrl);
        res.render("error", {errors: errors});
    }
});
module.exports = userSessionRouter;
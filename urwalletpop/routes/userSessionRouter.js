const express = require('express');
const userSessionRouter = express.Router();
/**
 * Método que verifica si el usuario está logeado
 */
userSessionRouter.use(function (req, res, next) {
    console.log("routerUsuarioSession");
    if (req.session.user) {
        next();
    } else {
        console.log("va a: " + req.originalUrl);
        res.redirect("/users/login");
    }
});
module.exports = userSessionRouter;
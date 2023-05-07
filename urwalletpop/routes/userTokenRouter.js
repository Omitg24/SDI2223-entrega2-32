const jwt = require("jsonwebtoken");
const express = require('express');
const userTokenRouter = express.Router();
/**
 * Método que verifica si el usuario tiene un token activo
 */
userTokenRouter.use(function (req, res, next) {
    console.log("userAuthorRouter");
    let token = req.headers['token'] || req.body.token || req.query.token;
    if (token != null) {
        jwt.verify(token, 'secreto', {}, function (err, infoToken) {
            if (err || (Date.now() / 1000 - infoToken.time) > 240) {
                res.status(403);
                res.json({
                    authorized: false,
                    error: 'Token inválido o caducado'
                });
            } else {
                res.user = infoToken.user;
                next();
            }
        });
    } else {
        res.status(403);
        res.json({
            authorized: false,
            error: 'No hay Token'
        });
    }
});
module.exports = userTokenRouter;
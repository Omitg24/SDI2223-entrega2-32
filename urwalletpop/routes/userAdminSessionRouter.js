const express = require('express');
const userSessionRouter = express.Router();
/**
 * Método que verifica si el usuario tiene rol de administrador
 */
userSessionRouter.use(function (req, res, next) {
    console.log("userAdminSessionRouter");

    if (req.session.role === 'admin') {
        next();
    } else {
        if(req.session.role ==null){
            res.redirect("/users/login");
        }else{
            let errors = [];
            errors.push({field: "Error de permisos", message: "No tienes permisos de administrador para acceder a esta página"});
            console.log("va a: " + req.originalUrl);
            res.render("error", {
                errors: errors,
                user: req.session.user,
                role: req.session.role,
                amount: req.session.amount,
                date: req.session.date
            });
        }
    }
});
module.exports = userSessionRouter;
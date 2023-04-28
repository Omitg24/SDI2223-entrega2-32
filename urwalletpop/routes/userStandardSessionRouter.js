const express = require('express');
const userSessionRouter = express.Router();
userSessionRouter.use(function (req, res, next) {
    console.log("userStandardSessionRouter");

    if (req.session.role === 'standard') {
        next();
    } else {
        let errors = [];
        errors.push({field: "Error de permisos", message: "No tienes permisos de usuario estándar para acceder a esta página"});
        console.log("va a: " + req.originalUrl);
        res.render("error", {
            errors: errors,
            user: req.session.user,
            role: req.session.role,
            amount: req.session.amount,
            date: req.session.date
        });
    }
});
module.exports = userSessionRouter;
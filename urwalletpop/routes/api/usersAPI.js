const {userValidatorLogin} = require("./userValidator");
const {validationResult} = require("express-validator");
module.exports = function (app, usersRepository, offerRepository) {

    /**
     * Metodo que devuelve todas las ofertas disponibles para el usuario
     */
    app.get("/api/offers", function (req, res) {
        let filter = {author: {$ne: res.user}};
        let options = {};
        offerRepository.getOffers(filter, options).then(offers => {
            res.status(200);
            res.json({offers: offers, interested: res.user});
        }).catch(error => {
            res.status(500);
            res.json({error: "Error al obtener las ofertas."});
        })
    });

    app.post("/api/users/login", userValidatorLogin, function (req, res) {
        try {
            let securePassword = app.get("crypto").createHmac('sha256', app.get('clave'))
                .update(req.body.password).digest('hex');
            let filter = {
                email: req.body.email,
                password: securePassword
            }
            let options = {};
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                res.status(403);
                res.json({errors: errors.array()})
            } else {
                usersRepository.findUser(filter, options).then(user => {
                    if (user == null) {
                        res.status(401);
                        res.json({
                            errors: [{msg: "Inicio de sesión incorrecto", authenticated: false}]
                        })
                    } else {
                        let token = app.get('jwt').sign(
                            {user: user.email, time: Date.now() / 1000},
                            "secreto");
                        res.status(200);
                        res.json({
                            message: "Inicio de sesión correcto",
                            authenticated: true,
                            token: token
                        })
                    }
                }).catch(error => {
                    res.status(401);
                    res.json({
                        errors: [{
                            msg: "Se ha producido un error al verificar credenciales", authenticated: false
                        }]
                    })
                });
            }
        } catch (e) {
            res.status(500);
            res.json({
                errors: [{
                    msg: "Se ha producido un error al verificar credenciales", authenticated: false
                }]
            })
        }
    });
}
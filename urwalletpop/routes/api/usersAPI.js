const {ObjectId} = require("mongodb");
module.exports = function (app, usersRepository, offerRepository) {

    app.get("/api/offers", function (req, res) {
        let filter = {author: {$ne: res.user}};
        let options = {};
        offerRepository.getOffers(filter, options).then(offers => {
            res.status(200);
            res.json({ offers: offers,interested: res.user});
        }).catch(error => {
            res.status(500);
            res.json({error: "Error al obtener las ofertas."});
        })
    });

    app.post("/api/users/login", function (req, res) {
        try {
            let securePassword = app.get("crypto").createHmac('sha256', app.get('clave'))
                .update(req.body.password).digest('hex');
            let filter = {
                email: req.body.email,
                password: securePassword
            }
            let user = {
                email: req.body.email,
                password: req.body.password
            }
            let options = {};
            validateUsersData(user).then(errors => {
                if (errors !== null && errors.length > 0) {
                    res.status(403);
                    res.json({
                        errors: errors
                    })
                } else {
                    usersRepository.findUser(filter, options).then(user => {
                        if (user == null) {
                            res.status(401);
                            res.json({
                                message: "Inicio de sesión no correcto", authenticated: false
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
                            message: "Se ha producido un error al verificar credenciales", authenticated: false
                        })
                    });
                }
            });
        } catch (e) {
            res.status(500);
            res.json({
                message: "Se ha producido un error al verificar credenciales", authenticated: false
            })
        }
    });


    async function validateUsersData(user) {
        let errors = new Array();
        if (user.email === null || typeof user.email === 'undefined' ||
            user.email === "") {
            errors.push("El campo email no puede estar vacío");
        }
        if (user.password === null || typeof user.password === 'undefined' ||
            user.password === "") {
            errors.push("El campo contraseña no puede estar vacío");
        }
        if (errors.length <= 0)
            return null;
        return errors;
    }


}
module.exports = function (app, usersRepository) {
    app.get('/signup', function (req, res) {
        res.render("signup.twig");
    });
    app.post('/signup', function (req, res) {
        let securePassword = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(req.body.password).digest('hex');
        let passwordConfirm = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(req.body.passwordConfirm).digest('hex');
        let date = new Date(req.body.date);
        let day = date.getDate().toString().padStart(2, '0');
        let month = (date.getMonth() + 1).toString().padStart(2, '0');
        let year = date.getFullYear();
        let formattedDate = `${day}/${month}/${year}`;
        let user = {
            email: req.body.email,
            name: req.body.name,
            lastName: req.body.lastName,
            password: securePassword,
            date: formattedDate,
            role: "standard",
            amount: 100
        }
        validate(user, passwordConfirm, function (errors) {
            if (errors != null && errors.length > 0) {
                res.render("error", {errors: errors});
            } else {
                usersRepository.insertUser(user).then(userId => {
                    req.session.user = user.email;
                    req.session.role = user.role;
                    req.session.amount = user.amount;
                    res.redirect("/offer/ownedList");
                }).catch(error => {
                    res.redirect("/signup" +
                        "?message=Se ha producido un error al registrar el usuario." +
                        "&messageType=alert-danger");
                });
            }
        });
    });

    function validate(user, confirmPassword, callback) {
        let errors = [];
        if (user.email.trim().toString().length === 0) {
            errors.push({field: "Email", message: "El email no puede ser vacío."});
        }
        if (user.name.trim().toString().length === 0) {
            errors.push({field: "Nombre", message: "El nombre no puede ser vacío." });
        }
        if (user.lastName.trim().toString().length === 0) {
            errors.push({field: "Apellido", message: "El apellido no puede ser vacío."});
        }
        if (user.password.trim().toString().length === 0) {
            errors.push({field: "Contraseña", message: "La contraseña no puede ser vacía." });
        }
        if (confirmPassword === undefined || confirmPassword.trim().toString().length === 0) {
            errors.push({field: "Confirmar contraseña", message: "La confirmación de contraseña no puede ser vacía." });
        }
        if (user.password !== confirmPassword) {
            errors.push({field: "Contraseñas", message: "Las contraseñas no coinciden." });
        }
        if (user.password.length < 4 && user.password.length > 24) {
            errors.push({field: "Contraseña", message: "La contraseña debe de tener un mínimo de 4 caracteres y un máximo de 24." });
        }
        if (!user.email.includes("@")) {
            errors.push({field: "Contraseña", message: "El email debe seguir el siguiente formato: \"x@y\"." });
        }
        let dateString = user.date;
        let dateParts = dateString.split("/");
        let dateObject = new Date(+dateParts[2], dateParts[1] - 1, +dateParts[0]);
        if (new Date(dateObject).getTime() >= new Date().getTime()) {
            errors.push({field: "Fecha de nacimiento", message: "La fecha de nacimiento no puede ser la fecha actual." });
        }
        let filter = {"email": user.email};
        usersRepository.findUser(filter, {}).then(user => {
            if (user !== null) {
                errors.push({field: "Email", message: "Este email ya pertenece a otro usuario, " + user.email + "."});
            }
            if (errors.length <= 0) {
                callback(null);
            } else {
                callback(errors);
            }
        }).catch(error => {
            errors.push({field: "Email", message: "Se ha producido un error al buscar el usuario." + error});
        });
    }
}
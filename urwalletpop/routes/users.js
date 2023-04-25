//const {ObjectId} = require("mongodb");
const {ObjectId} = require("mongodb");
module.exports = function (app, usersRepository) {
    app.get('/home', function (req, res) {
        res.render("home.twig", {
            user: req.session.user,
            role: req.session.role,
            amount: req.session.amount,
            date: req.session.date
        });
    });
    app.get('/users/signup', function (req, res) {
        res.render("signup.twig");
    });
    app.post('/users/signup', function (req, res) {
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
        validateSignUp(user, passwordConfirm, function (errors) {
            if (errors != null && errors.length > 0) {
                res.render("error", {errors: errors});
            } else {
                usersRepository.insertUser(user).then(userId => {
                    req.session.user = user.email;
                    req.session.role = user.role;
                    req.session.amount = user.amount;
                    req.session.date = user.date;
                    res.redirect("/offer/ownedList");
                }).catch(error => {
                    res.redirect("/users/signup" +
                        "?message=Se ha producido un error al registrar el usuario." +
                        "&messageType=alert-danger");
                });
            }
        });
    });
    app.get('/users/login', function (req, res) {
        res.render("login.twig");
    })
    app.post('/users/login', function (req, res) {
        let securePassword = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(req.body.password).digest('hex');
        let filter = {
            email: req.body.email,
            password: securePassword
        }
        let options = {};
        usersRepository.findUser(filter, options).then(user => {
            if (user == null) {
                req.session.user = null;
                req.session.role = null;
                req.session.amount = null;
                req.session.date = null;
                let errors = [];
                errors.push({field: "Email", message: "Email o password incorrecto"});
                res.render("error", {
                    errors: errors,
                    user: req.session.user,
                    role: req.session.role,
                    amount: req.session.amount,
                    date: req.session.date
                });
            } else {
                req.session.user = user.email;
                req.session.role = user.role;
                req.session.amount = user.amount;
                req.session.date = user.date;
                if (user.role === "admin") {
                    res.redirect("/users/list");
                } else {
                    res.redirect("/offer/ownedList");
                }
            }
        }).catch(error => {
            req.session.user = null;
            req.session.role = null;
            req.session.amount = null;
            req.session.date = null;
            let errors = [];
            errors.push({field: "Email", message: "Se ha producido un error al buscar el usuario"});
            res.render("error", {
                errors: errors,
                user: req.session.user,
                role: req.session.role,
                amount: req.session.amount,
                date: req.session.date
            });
        })
    });
    app.get('/users/logout', function (req, res) {
        req.session.user = null;
        req.session.role = null;
        req.session.amount = null;
        req.session.date = null;
        res.redirect("/users/login");
    });
    app.get('/users/list', function (req, res) {
        let filter = {};
        let options = {sort: {name: 1}};
        if (req.query.search != null && typeof (req.query.search) != "undefined" && req.query.search != "") {
            filter = {"email": {$regex: ".*" + req.query.search + ".*"}};
        }
        let page = parseInt(req.query.page);
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            page = 1;
        }
        usersRepository.getUsersPg(filter, options, page).then(result => {
            if (result === null) {
                let errors = [];
                errors.push({field: "Email", message: "No hay usuarios registrados."});
                res.render("/error", {
                    errors: errors,
                    user: req.session.user,
                    role: req.session.role,
                    amount: req.session.amount,
                    date: req.session.date
                });
            } else {
                let lastPage = result.total / 4;
                if (result.total % 4 > 0) {
                    lastPage = lastPage + 1;
                }
                let pages = [];
                for (let i = page - 2; i <= page + 2; i++) {
                    if (i > 0 && i <= lastPage) {
                        pages.push(i);
                    }
                }
                let response = {
                    usersList: result.users,
                    user: req.session.user,
                    role: req.session.role,
                    amount: req.session.amount,
                    date: req.session.date,
                    pages: pages,
                    currentPage: page
                }
                res.render("user/list.twig", response);
            }
        }).catch(error => {
            let errors = [];
            errors.push({field: "Base de datos", message: "Se ha producido un error al buscar los usuarios."});
            res.render("error", {
                errors: errors,
                user: req.session.user,
                role: req.session.role,
                amount: req.session.amount,
                date: req.session.date
            });
        });
    });

    function validateSignUp(user, confirmPassword, callback) {
        let errors = [];
        if (user.email.trim().toString().length === 0) {
            errors.push({field: "Email", message: "El email no puede ser vacío."});
        }
        if (user.name.trim().toString().length === 0) {
            errors.push({field: "Nombre", message: "El nombre no puede ser vacío."});
        }
        if (user.lastName.trim().toString().length === 0) {
            errors.push({field: "Apellido", message: "El apellido no puede ser vacío."});
        }
        if (user.password.trim().toString().length === 0) {
            errors.push({field: "Contraseña", message: "La contraseña no puede ser vacía."});
        }
        if (confirmPassword === undefined || confirmPassword.trim().toString().length === 0) {
            errors.push({
                field: "Confirmar contraseña",
                message: "La confirmación de contraseña no puede ser vacía."
            });
        }
        if (user.password !== confirmPassword) {
            errors.push({field: "Contraseñas", message: "Las contraseñas no coinciden."});
        }
        if (user.password.length < 4 && user.password.length > 24) {
            errors.push({
                field: "Contraseña",
                message: "La contraseña debe de tener un mínimo de 4 caracteres y un máximo de 24."
            });
        }
        if (!user.email.includes("@")) {
            errors.push({field: "Contraseña", message: "El email debe seguir el siguiente formato: \"x@y\"."});
        }
        let dateString = user.date;
        let dateParts = dateString.split("/");
        let dateObject = new Date(+dateParts[2], dateParts[1] - 1, +dateParts[0]);
        if (new Date(dateObject).getTime() >= new Date().getTime()) {
            errors.push({
                field: "Fecha de nacimiento",
                message: "La fecha de nacimiento no puede ser la fecha actual."
            });
        }
        let filter = {"email": user.email};
        usersRepository.findUser(filter, {}).then(user => {
            if (user !== null) {
                errors.push({
                    field: "Email",
                    message: "Este email ya pertenece a otro usuario, " + user.email + "."
                });
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
    app.get('/signup', function (req, res) {
        res.render("signup.twig");
    });

    app.post('/users/delete', function (req, res) {
        let ids = req.body.users;
        let filter;
        if(typeof ids == "string"){
            ids=[ids];
        }
        for(let i=0;i<ids.length;i++){
            filter = {_id: ObjectId(ids[i])};
            usersRepository.deleteUser(filter, {}).then(result => {
                if (result === null || result.deletedCount === 0) {
                    //res.send(req.body.users)
                    res.send("No se ha podido eliminar el registro");
                } else {
                    res.redirect("list.twig");
                }
            }).catch(error => {
                res.send("Se ha producido un error al intentar eliminar la canción: " + error)
            });
        }
    })
}
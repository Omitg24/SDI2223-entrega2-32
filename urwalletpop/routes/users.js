const {ObjectId} = require("mongodb");
module.exports = function (app, usersRepository, offerRepository, logsRepository) {
    /** Método que devuelve la vista del panel principal de la web **/
    app.get('/home', function (req, res) {
        let filter = {feature: true};
        let page = parseInt(req.query.page);
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            page = 1;
        }
        offerRepository.getOffersPage(filter, {}, page).then(result => {
            let lastPage = result.total / 4;
            if (result.total % 4 > 0) { // Sobran decimales
                lastPage = lastPage + 1;
            }
            let pages = []; // paginas mostrar
            for (let i = page - 2; i <= page + 2; i++) {
                if (i > 0 && i <= lastPage) {
                    pages.push(i);
                }
            }
            res.render("home.twig", {
                offers: result.offers,
                user: req.session.user,
                role: req.session.role,
                amount: req.session.amount,
                date: req.session.date,
                pages: pages,
                currentPage: page
            });
        });
    });
    /** Método que devuelve la vista del panel de registro de la web **/
    app.get('/users/signup', function (req, res) {
        res.render("signup.twig", {
            user: req.session.user,
            role: req.session.role,
            amount: req.session.amount,
            date: req.session.date
        });
    });
    /** Método que realiza el registro de un usuario nuevo en la web **/
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
        validateSignUp(user, passwordConfirm).then(errors => {
            if (errors != null && errors.length > 0) {
                res.render("signup", {
                    errors: errors,
                    user: req.session.user,
                    role: req.session.role,
                    amount: req.session.amount,
                    date: req.session.date
                });
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
        })
    });
    /** Método que devuelve la vista del panel de logeo de la web **/
    app.get('/users/login', function (req, res) {
        res.render("login.twig");
    })
    /** Método que realiza el logeo de un usuario en la web **/
    app.post('/users/login', function (req, res) {
        let securePassword = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(req.body.password).digest('hex');
        let user = {
            email: req.body.email,
            password: req.body.password
        }
        let filter = {
            email: req.body.email,
            password: securePassword
        }
        let options = {};
        validateLogin(user).then(errors => {
            if (errors !== null && errors.length > 0) {
                res.render("login", {
                    errors: errors,
                    user: req.session.user,
                    role: req.session.role,
                    amount: req.session.amount,
                    date: req.session.date
                });
            } else {
                usersRepository.findUser(filter, options).then(user => {
                    if (user == null) {
                        insertLog(req, "LOGIN-ERR", req.body.email);
                        req.session.user = null;
                        req.session.role = null;
                        req.session.amount = null;
                        req.session.date = null;
                        let errors = [];
                        errors.push({type: "Email", message: "Email o password incorrecto"});
                        res.render("login", {
                            errors: errors,
                            user: req.session.user,
                            role: req.session.role,
                            amount: req.session.amount,
                            date: req.session.date
                        });
                    } else {
                        insertLog(req, "LOGIN-EX", req.body.email);
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
                    insertLog(req, "LOGIN-ERR", req.body.email);
                    req.session.user = null;
                    req.session.role = null;
                    req.session.amount = null;
                    req.session.date = null;
                    let errors = [];
                    errors.push({type: "Email", message: "Se ha producido un error al buscar el usuario"});
                    res.render("login.twig", {
                        errors: errors,
                        user: req.session.user,
                        role: req.session.role,
                        amount: req.session.amount,
                        date: req.session.date
                    });
                })
            }
        })
    });
    /** Método que realiza el logout de la sesión de un usuario en la web y lo redirige al panel de logeo **/
    app.get('/users/logout', function (req, res) {
        insertLog(req, "LOGOUT", res.user);
        req.session.user = null;
        req.session.role = null;
        req.session.amount = null;
        req.session.date = null;
        res.redirect("/users/login");
    });
    /** Método que muestra una lista de los usuarios registrados en la web **/
    app.get('/users/list', function (req, res) {
        let search = req.query.search || '';
        let filter = {};
        let options = {sort: {name: 1}};
        if (search !== '') {
            filter = {"email": {$regex: new RegExp(search, 'i')}};
        }
        let page = parseInt(req.query.page);
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            page = 1;
        }
        usersRepository.getUsersPg(filter, options, page).then(result => {
            if (result === null) {
                let errors = [];
                errors.push({type: "Email", message: "No hay usuarios registrados."});
                res.render("user/list.twig", {
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
                    currentPage: page,
                    search: search
                }
                res.render("user/list.twig", response);
            }
        }).catch(error => {
            let errors = [];
            errors.push({type: "Base de datos", message: "Se ha producido un error al buscar los usuarios."});
            res.render("user/list.twig", {
                errors: errors,
                user: req.session.user,
                role: req.session.role,
                amount: req.session.amount,
                date: req.session.date
            });
        });
    });
    /** Método que elimina una lista de usuarios de la web **/
    app.post('/users/delete', function (req, res) {
        let ids = req.body.users;
        let filter;
        if (typeof ids == "string") {
            ids = [ids];
        }

        let emailFilter = {email: res.user};
        usersRepository.findUser(emailFilter, {}).then(user => {
            if (ids.includes(user._id)) {
                ids.splice(ids.indexOf(user._id), 1);
            }
        }).catch(error => {
            let errors = [];
            errors.push({
                type: "Borrado",
                message: "Se ha producido un error al buscar al usuario"
            });
        });

        filter = {_id: {$in: ids.map(id => ObjectId(id))}};
        usersRepository.deleteUsers(filter, {}).then(result => {
            if (result === null || result.deletedCount === 0) {
                res.send("No se ha podido eliminar el registro");
            } else {
                res.redirect("/users/list");
            }
        }).catch(error => {
            res.send("Se ha producido un error al intentar eliminar los usuarios")
        });
    });
    /** Método que inserta un log en la vista de registro cuando un usuario realiza una petición **/
    function insertLog(req, type, email) {
        let log = {
            date: Date.now(),
            action: req.method,
            url: email,
            type: type,
        }
        logsRepository.insertLog(log).catch(error => {
            console.log("No se ha podido registrar la peticion " + req.method)
        });
    }
    /** Método que valida los datos de login **/
    async function validateLogin(user) {
        let errors = [];
        console.log(user);
        if (typeof user.email === "undefined" || user.email.trim().toString().length === 0) {
            errors.push({type: "Email", message: "El email no puede ser vacío."});
        }
        if (typeof user.password === "undefined" || user.password.trim().toString().length === 0) {
            errors.push({type: "Contraseña", message: "La contraseña no puede ser vacía."});
        }
        return errors;
    }
    /** Método que valida los datos de registro **/
    async function validateSignUp(user, confirmPassword) {
        let errors = [];
        if (typeof user.email === "undefined" || user.email.trim().toString().length === 0) {
            errors.push({type: "Email", message: "El email no puede ser vacío."});
        }
        if (typeof user.name === "undefined" || user.name.trim().toString().length === 0) {
            errors.push({type: "Nombre", message: "El nombre no puede ser vacío."});
        }
        if (typeof user.lastName === "undefined" || user.lastName.trim().toString().length === 0) {
            errors.push({type: "Apellido", message: "El apellido no puede ser vacío."});
        }
        if (typeof user.password === "undefined" || user.password.trim().toString().length === 0) {
            errors.push({type: "Contraseña", message: "La contraseña no puede ser vacía."});
        }
        if (typeof confirmPassword === "undefined" || confirmPassword === undefined || confirmPassword.trim().toString().length === 0) {
            errors.push({
                type: "Confirmar contraseña",
                message: "La confirmación de contraseña no puede ser vacía."
            });
        }
        if (typeof user.date === "undefined" || user.date.trim().toString().length === 0 || user.date.includes("NaN")) {
            errors.push({type: "Fecha de nacimiento", message: "La fecha de nacimiento no puede ser vacía."});
        }
        if (user.password !== confirmPassword) {
            errors.push({type: "Contraseñas", message: "Las contraseñas deben coincidir."});
        }
        if (user.password.length < 4 && user.password.length > 24) {
            errors.push({
                type: "Contraseña",
                message: "La contraseña debe de tener un mínimo de 4 caracteres y un máximo de 24."
            });
        }
        if (!user.email.includes("@")) {
            errors.push({type: "Email", message: "El email debe seguir el siguiente formato: \"x@y\"."});
        }
        let dateString = user.date;
        let dateParts = dateString.split("/");
        let dateObject = new Date(+dateParts[2], dateParts[1] - 1, +dateParts[0]);
        if (new Date(dateObject).getTime() >= new Date().getTime()) {
            errors.push({
                type: "Fecha de nacimiento",
                message: "La fecha de nacimiento no puede ser la fecha actual."
            });
        }
        let filter = {"email": user.email};
        await usersRepository.findUser(filter, {}).then(user => {
            if (user !== null) {
                errors.push({
                    type: "Email",
                    message: "Este email ya pertenece a otro usuario, " + user.email + "."
                });
            }
            if (errors.length === 0) {
                return null;
            }
        }).catch(error => {
            errors.push({type: "Email", message: "Se ha producido un error al buscar el usuario." + error});
        });
        return errors;
    }
}
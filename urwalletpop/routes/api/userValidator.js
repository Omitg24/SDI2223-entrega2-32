const {check} = require('express-validator');

exports.userValidatorLogin = [
    check("email", "El email no puede ser vacío").trim().not().isEmpty(),
    check("password", "La contraseña no puede ser vacía").trim().not().isEmpty()
]
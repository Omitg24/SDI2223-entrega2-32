const {check} = require('express-validator');

/** Método que valida los datos del usuario en el panel de logeo **/
exports.userValidatorLogin = [
    check("email", "El email no puede ser vacío").trim().not().isEmpty(),
    check("password", "La contraseña no puede ser vacía").trim().not().isEmpty()
]
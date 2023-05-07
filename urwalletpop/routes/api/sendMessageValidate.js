const {check} = require('express-validator');
//El mensaje no puede estar vacio
exports.messageValidatorInsert = [
    check('message', 'Mensaje vacío').trim().not().isEmpty(),
]
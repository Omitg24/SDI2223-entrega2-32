const {check} = require('express-validator');

exports.messageValidatorInsert = [
    check('message', 'Mensaje vacío').trim().not().isEmpty(),
]